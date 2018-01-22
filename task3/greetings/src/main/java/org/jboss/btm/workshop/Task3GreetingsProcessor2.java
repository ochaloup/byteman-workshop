package org.jboss.btm.workshop;

public class Task3GreetingsProcessor2 extends Task3AbstractGreetingsProcessor {

	@Override
	public String process(String param) {
		return "#2:" + parentProcessor(param);
	}

}
