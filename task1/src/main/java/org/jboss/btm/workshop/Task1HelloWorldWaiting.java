package org.jboss.btm.workshop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * <p>
 * Simple class running infinite cycle until you insert <code>quit</code>
 * or <code>exit</code>.<br>
 * On different input it prints: <code>Hellow world!<code>.
 * </p>
 * <pre>
 * java -cp target/byteman-workshop-task1-1.0.0-SNAPSHOT.jar org.jboss.btm.workshop.Task1HelloWorldWaiting
 * </pre>
 */
public class Task1HelloWorldWaiting {

    public static void main( String[] args ) {
    	try(BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
    		String readValue = "";
    		while(!readValue.matches("exit|q|quit")) {
    			Task1HelloWorldWaiting.write("Hello world!");
    			System.out.print("(type any or 'quit') >> ");
    			readValue = br.readLine();
    		}
    	} catch (IOException ioe) {
    		ioe.printStackTrace();
    	}
    }
    
    private static void write(String message) {
    	System.out.println( message );
    }
}
