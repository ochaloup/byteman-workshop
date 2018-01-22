package org.jboss.btm.workshop;

import java.util.Random;
import java.util.concurrent.Callable;


/**
 * Adding value between 1-10 to {@link Repository#COUNTER}.<br>
 * Returning previous value - before counter was increased. 
 */
public class AdderThread implements Callable<Integer> {
    private final int addValue;

    public AdderThread() {
        this.addValue = new Random().nextInt(10) + 1;
    }
    
    public AdderThread(int addValue) {
        this.addValue = addValue;
    }

    @Override
    public Integer call() throws Exception {
        int previousValue = Repository.COUNTER.getAndAdd(addValue);
        return previousValue;
    }

}
