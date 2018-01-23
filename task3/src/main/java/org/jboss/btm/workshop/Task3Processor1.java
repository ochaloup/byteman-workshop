package org.jboss.btm.workshop;

public class Task3Processor1 implements Task3Processor {

	@Override
	public String process(String param) {
		return "#1:" + param;
	}

}
