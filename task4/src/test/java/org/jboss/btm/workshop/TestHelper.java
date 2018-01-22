package org.jboss.btm.workshop;

import java.util.List;

import org.jboss.byteman.rule.Rule;
import org.jboss.byteman.rule.helper.Helper;

public class TestHelper extends Helper {

    protected TestHelper(Rule rule) {
        super(rule);
    }

    public int sumList(List<Object> list) {
        int out = list.stream().mapToInt(i -> (Integer) i).sum();
        return out;
    }

}
