package org.jboss.btm.workshop;

public class Task3Processor2 extends Task3AbstractProcessor {

	@Override
	public String process(String param) {
		return "#2:" + super.process(param);
	}

}
