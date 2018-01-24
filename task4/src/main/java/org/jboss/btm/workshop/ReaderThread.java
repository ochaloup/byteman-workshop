package org.jboss.btm.workshop;

import java.util.concurrent.Callable;


/**
 * Reading counter value {@link Repository#COUNTER}.
 */
public class ReaderThread implements Callable<Integer> {

    @Override
    public Integer call() throws Exception {
        return Repository.COUNTER.get();
    }

}
