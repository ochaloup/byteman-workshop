package org.jboss.btm.workshop;

import java.util.Random;
import java.util.concurrent.Callable;


/**
 * Multiplying value by 1-10 at {@link Repository#COUNTER}.<br>
 * Returning previous value - before counter was multiplied. 
 */
public class MultiplyThread implements Callable<Integer> {

    private final int multiplyValue;

    public MultiplyThread() {
        this.multiplyValue = new Random().nextInt(10) + 1;
    }
    
    @Override
    public Integer call() throws Exception {
        int previousValue = Repository.COUNTER.getAndUpdate(i -> i * multiplyValue);
        return previousValue;
    }

}
