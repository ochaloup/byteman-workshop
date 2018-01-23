package org.jboss.btm.workshop;


public abstract class Task3AbstractProcessor implements Task3Processor {

	@Override
	public String process(String param) {
		return "abstract:" + param;
	}

}
