package org.jboss.btm.workshop;

import java.util.Random;
import java.util.concurrent.Callable;

/**
 * Subtracting value between 1-10 from {@link Repository#COUNTER}.<br>
 * Returning previous value - before counter was subtracted.
 */
public class SubtractorThread implements Callable<Integer> {

    private final int substractValue;

    public SubtractorThread() {
        this.substractValue = new Random().nextInt(10) + 1;
    }
    
    public SubtractorThread(int substractValue) {
        this.substractValue = substractValue;
    }

    @Override
    public Integer call() throws Exception {
        int previousValue = Repository.COUNTER.getAndAdd(- substractValue);
        return previousValue;
    }

}
