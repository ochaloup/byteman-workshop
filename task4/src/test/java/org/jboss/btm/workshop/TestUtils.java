package org.jboss.btm.workshop;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public final class TestUtils {

    public static void waitToEnd(Set<Future<?>> futures, ExecutorService es) {
        try {
            // waiting for all threads will be finished
            for(Future<?> f: futures) {
                f.get();
            }
            es.shutdown();
            es.awaitTermination(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException("Waiting on service " + es + " failed", e);
        }
    }
}