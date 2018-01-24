package org.jboss.btm.workshop;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.jboss.byteman.contrib.bmunit.BMUnitConfig;
import org.jboss.byteman.contrib.bmunit.BMUnitRunner;
import org.jboss.byteman.contrib.dtest.InstrumentedClass;
import org.jboss.byteman.contrib.dtest.Instrumentor;
import org.jboss.byteman.contrib.dtest.RuleConstructor;
import org.jboss.byteman.rule.helper.Helper;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(BMUnitRunner.class)
@BMUnitConfig(
  verbose = true,
  debug = true
)
public class DTestTest {
    
    private static Instrumentor instrumentor;

    @BeforeClass
    public static void setUpTestClass() throws Exception {
        instrumentor = new Instrumentor();
    }

    @Before
    public void setUp() {
        Repository.COUNTER.set(0);
    }

    @Test
    public void notAllowingSubtractThread() throws Exception {
        // dTest rule definition
        RuleConstructor rule = RuleConstructor
            .createRule("not allowing subtract thread")
            .onInterface(Callable.class)
            .inMethod("call")
            .atEntry()
            .helper(Helper.class)
            .ifCondition("callerEquals(\"SubtractorThread.call\", true)")
            .doAction("RETURN 0");
        instrumentor.installRule(rule);
        InstrumentedClass instrumentedAdderThreadClass = instrumentor.instrumentClass(AdderThread.class);

        // test
        Set<Future<?>> futures = new HashSet<>();

        ExecutorService es = Executors.newCachedThreadPool();
        futures.add(es.submit(new SubtractorThread()));
        futures.add(es.submit(new AdderThread()));
        futures.add(es.submit(new SubtractorThread()));
        futures.add(es.submit(new AdderThread()));

        TestUtils.waitToEnd(futures, es);

        // verify
        assertThat(Repository.COUNTER.get()).isGreaterThan(0)
            .as("Byteman do not allow run any subtract there should be possitive value at the counter");
        instrumentedAdderThreadClass.assertKnownInstances(2);
        instrumentedAdderThreadClass.assertSumMethodCallCount("call", 2);
    }
}
