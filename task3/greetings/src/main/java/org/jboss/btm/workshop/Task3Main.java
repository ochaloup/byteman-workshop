package org.jboss.btm.workshop;

import java.util.Arrays;

/**
 * <pre>
 * java -cp target/byteman-workshop-task3-1.0.0-SNAPSHOT.jar org.jboss.btm.workshop.Task3Main
 * </pre>
 */
public class Task3Main {

    public static void main( String[] args ) {
    	if(args.length < 1) throw new IllegalArgumentException("expecting at least one argument");
    	String arg1 = args[0];
    	String arg2 = arg1;
    	if(args.length >= 2) arg2 = args[1];

    	Task3GreetingsProcessor smt1 = new Task3GreetingsProcessor1();
    	Task3GreetingsProcessor smt2 = new Task3GreetingsProcessor2();

        System.out.printf("We get [%s] [%s]%n", smt1.process(arg1), smt2.process(arg2));
    }
}
