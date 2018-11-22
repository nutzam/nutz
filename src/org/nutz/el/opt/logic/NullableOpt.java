package org.nutz.el.opt.logic;

import java.util.Queue;

import org.nutz.el.opt.AbstractOpt;

public class NullableOpt extends AbstractOpt {
    
    private Object right;

    @Override
    public int fetchPriority() {
        return 0;
    }

    @Override
    public void wrap(Queue<Object> rpn) {
        right = rpn.poll();
    }

    @Override
    public Object calculate() {
        try {
            return this.calculateItem(right);
        } catch (Throwable e) {
        }
        return null;
    }

    @Override
    public String fetchSelf() {
        return "!!";
    }

}
