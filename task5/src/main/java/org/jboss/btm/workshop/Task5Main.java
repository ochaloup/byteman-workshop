package org.jboss.btm.workshop;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * <pre>
 * java -cp target/byteman-workshop-task5-1.0.0-SNAPSHOT.jar org.jboss.btm.workshop.Task5Main
 * </pre>
 */
public class Task5Main {
    private static Random random = new Random();

    public static void main( String[] args ) {
        ExecutorService es = Executors.newCachedThreadPool();

        for (int i = 1; i <= 10; i++) {
            es.submit(instance());
        }

        try {
            es.shutdown();
            es.awaitTermination(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException("Waiting on service " + es + " failed", e);
        }

        System.out.printf("Counter value: %s%n", Repository.COUNTER.get());
    }

    private static Callable<Integer> instance() {
        int rNum = random.nextInt(3) + 1;
        switch(rNum) {
            case 1: return new SubtractorThread();
            case 2: return new MultiplyThread();
            case 3:
            default:
                return new AdderThread();
        }
    }
}
