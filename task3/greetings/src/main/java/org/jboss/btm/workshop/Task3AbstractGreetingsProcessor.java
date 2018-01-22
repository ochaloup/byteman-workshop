package org.jboss.btm.workshop;


public abstract class Task3AbstractGreetingsProcessor implements Task3GreetingsProcessor {

	public abstract String process(String param);
	
	protected String parentProcessor(String param) {
		return "abstract:" + param;
	}

}
