package org.jboss.btm.workshop;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.jboss.byteman.contrib.bmunit.BMRule;
import org.jboss.byteman.contrib.bmunit.BMRules;
import org.jboss.byteman.contrib.bmunit.BMScript;
import org.jboss.byteman.contrib.bmunit.BMUnitConfig;
import org.jboss.byteman.contrib.bmunit.BMUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(BMUnitRunner.class)
@BMUnitConfig(
    enforce = true,
    debug = true,
    verbose = true
)
public class BMUnitTest {

    @Before
    public void setUp() {
        Repository.COUNTER.set(0);
    }

    /**
     * Byteman rule should ensure that invocation of {@link SubtractorThread} wont' have
     * any influence at the content of the Repository.COUNTER.<br>
     * In other words expecting that when called it just returns without
     * any change at the shared stated.<br>
     * Thus we can be sure only the AdderThread is invoked
     * and the counter will have value greater than 0.
     */
    @Test
    @BMRule(
        name = "not allowing subtract thread",
        targetClass = "Callable",
        isInterface = true,
        targetMethod = "call",
        targetLocation = "ENTRY",
        condition = "callerEquals(\"SubtractorThread.call\", true)",
        action = " RETURN 0"
            // + ", traceStack(\">>>> \")"
    )
    public void notAllowingSubtractThread() {
        Set<Future<?>> futures = new HashSet<>();

        ExecutorService es = Executors.newCachedThreadPool();
        futures.add(es.submit(new SubtractorThread()));
        futures.add(es.submit(new SubtractorThread()));
        futures.add(es.submit(new AdderThread()));
        futures.add(es.submit(new SubtractorThread()));
        futures.add(es.submit(new SubtractorThread()));

        TestUtils.waitToEnd(futures, es);

        assertThat(Repository.COUNTER.get()).isGreaterThan(0)
            .as("Byteman do not allow run any subtract there should be possitive value at the counter");
    }

    /**
     * Byteman rule should ensures that {@link Callable} invocation
     * will have not influence on the {@link Repository#COUNTER} value.<br>
     * In other word the call <code>call</code> will return immediately.
     */
    @Test
    @BMScript("threadMethodNotExecuted.btm")
    public void threadMethodNotExecuted() {
        Set<Future<?>> futures = new HashSet<>();
        
        ExecutorService es = Executors.newCachedThreadPool();
        futures.add(es.submit(new AdderThread()));
        futures.add(es.submit(new AdderThread()));
        
        TestUtils.waitToEnd(futures, es);
        
        assertThat(Repository.COUNTER.get()).isEqualTo(0)
            .as("Byteman do not allow run any Thread method, counter is at default value");
    }

    /**
     * The Byteman rules should ensures that {@link ReaderThread} will be the
     * first thread reaching the value of {@link Repository#COUNTER}.<br>
     * When reader reads value the rest of the threads can be processed. 
     */
    @Test
    @BMRules (rules = {
        @BMRule(
                name = "wait for reader",
                targetClass = "Callable",
                isInterface = true,
                targetMethod = "call",
                targetLocation = "ENTRY",
                condition = "callerEquals(\"SubtractorThread.call\", true) || callerEquals(\"AdderThread.call\", true)",
                action = "waitFor(\"wasRead\")"
            ),
        @BMRule(
                name = "signal was read",
                targetClass = "ReaderThread",
                targetMethod = "call",
                targetLocation = "AFTER INVOKE java.util.concurrent.atomic.AtomicInteger.get",
                action = "signalWake(\"wasRead\")"
                )
    })
    public void waitForSignal() throws Exception {
        Set<Future<?>> futures = new HashSet<>();
        
        ExecutorService es = Executors.newCachedThreadPool();
        futures.add(es.submit(new AdderThread()));
        futures.add(es.submit(new AdderThread()));
        futures.add(es.submit(new AdderThread()));
        Thread.sleep(100); // bigger chance the add thread starts before reader thread
        Future<Integer> readValue = es.submit(new ReaderThread());
        futures.add(readValue);
        
        TestUtils.waitToEnd(futures, es);
        
        assertThat(readValue.get()).isEqualTo(0)
            .as("Byteman made waiting the threads making changes for reader can see the default value.");
    }

    /**
     * Byteman rules should ensure that the {@link AdderThread} will be changing value of counter
     * three times and other ivocations will be just immediatelly returned without
     * changing value of the counter.
     */
    @Test
    @BMRules (rules = {
            @BMRule(
                    name = "thread counter creation",
                    targetClass = "AdderThread",
                    targetMethod = "<init>",
                    condition = "!flagged(\"counterCreated\")",
                    action = "debug(\">> creating counter\"), flag(\"counterCreated\"), createCounter(\"threadCounter\")"
                    ),
            @BMRule(
                    name = "thread countdown",
                    targetClass = "AdderThread",
                    targetMethod = "call",
                    condition = "incrementCounter(\"threadCounter\") > 3",
                    action = "debug(\">> incrementing counter\"), RETURN 0"
                    )
    })
    public void allowProcessThreeThreads() throws Exception {
        Set<Future<?>> futures = new HashSet<>();
        
        ExecutorService es = Executors.newCachedThreadPool();
        for (int i = 1; i <= 10; i++) {
            futures.add(es.submit(new AdderThread(1)));
        }
        
        TestUtils.waitToEnd(futures, es);
        
        assertThat(Repository.COUNTER.get()).isEqualTo(3)
            .as("Byteman allow run only three threads others will throw exception but not adding counter value");
    }

    /**
     * Here is just rather showcase of using link map which is capable to store
     * data and then they could be retrieved.  
     */
    @Test
    @BMRules (rules = {
            @BMRule(
                    name = "track subtractor events",
                    targetClass = "SubtractorThread",
                    targetMethod = "call",
                    targetLocation = "AFTER INVOKE getAndAdd",
                    binding = "subtractorValue:int = $this.substractValue, counter:int = incrementCounter(\"linkSubtractorCounter\")",
                    action = "link(\"subtractorMap\", counter, subtractorValue)"
                    ),
            @BMRule(
                    name = "change the counter return value",
                    targetClass = "AtomicInteger",
                    helper = "org.jboss.btm.workshop.TestHelper",
                    targetMethod = "get",
                    targetLocation = "AT EXIT",
                    condition = "callerEquals(\"BMUnitTest.recordSubtractEvents\", true)",
                    action = "RETURN sumList(linkValues(\"subtractorMap\"))"
                    )
    })
    public void recordSubtractEvents() throws Exception {
        Set<Future<?>> futures = new HashSet<>();
        
        ExecutorService es = Executors.newCachedThreadPool();
        for (int i = 1; i <= 3; i++) {
            futures.add(es.submit(new AdderThread(3)));
            futures.add(es.submit(new SubtractorThread(42)));
        }
        
        TestUtils.waitToEnd(futures, es);

        assertThat(Repository.COUNTER.get()).isEqualTo(3 * 42)
            .as("Byteman changed counter get behaviour to get only subtractor values");
    }
    
}
