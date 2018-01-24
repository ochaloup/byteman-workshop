package org.jboss.btm.workshop;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.jboss.byteman.contrib.bmunit.BMRule;
import org.jboss.byteman.contrib.bmunit.BMRules;
import org.jboss.byteman.contrib.bmunit.BMUnitConfig;
import org.jboss.byteman.contrib.bmunit.BMUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(BMUnitRunner.class)
@BMUnitConfig(
    debug = true,
    verbose = true
)
public class BMUnitTest {

    @Before
    public void setUp() {
        Repository.COUNTER.set(0);
    }

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
    
    @Test
    @BMRule(
        name = "not allowing thread run",
        targetClass = "Callable",
        isInterface = true,
        targetMethod = "call",
        targetLocation = "ENTRY",
        condition = "callerEquals(\"Thread.run\", true, 99)",
        action = "RETURN 0"
    )
    public void threadMethodNotExecuted() {
        Set<Future<?>> futures = new HashSet<>();
        
        ExecutorService es = Executors.newCachedThreadPool();
        futures.add(es.submit(new AdderThread()));
        futures.add(es.submit(new AdderThread()));
        
        TestUtils.waitToEnd(futures, es);
        
        assertThat(Repository.COUNTER.get()).isEqualTo(0)
            .as("Byteman do not allow run any Thread method, counter is at default value");
    }
    
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
    
}
