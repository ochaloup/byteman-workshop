package org.jboss.btm.workshop;

/**
 * <p>
 * On running it prints: <code>Hellow world!<code>.
 * </p>
 * <pre>
 * java -cp target/byteman-workshop-task1-1.0.0-SNAPSHOT.jar org.jboss.btm.workshop.Task1HelloWorld
 * </pre>
 */
public class Task1HelloWorld 
{
    public static void main( String[] args )
    {
        String message = "Hello World!";
        System.out.println( message );
    }
}
