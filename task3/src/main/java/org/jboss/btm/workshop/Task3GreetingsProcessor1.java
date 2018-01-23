package org.jboss.btm.workshop;

public class Task3GreetingsProcessor1 implements Task3GreetingsProcessor {

	@Override
	public String process(String param) {
		return "#1:" + param;
	}

}
