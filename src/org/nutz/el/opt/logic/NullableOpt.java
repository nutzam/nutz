package org.nutz.el.opt.logic;

import java.util.Queue;

import org.nutz.el.opt.AbstractOpt;

public class NullableOpt extends AbstractOpt {
    
    private Object right;

    public int fetchPriority() {
        return 0;
    }

    public void wrap(Queue<Object> rpn) {
        right = rpn.poll();
    }

    public Object calculate() {
        try {
            return this.calculateItem(right);
        } catch (Throwable e) {
        }
        return null;
    }

    public String fetchSelf() {
        return "!!";
    }

}
