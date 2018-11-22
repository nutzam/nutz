package org.nutz.el.opt.bit;

import java.util.Queue;

import org.nutz.el.opt.AbstractOpt;

/**
 * 非
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class BitNot extends AbstractOpt{
    private Object right;
    @Override
    public int fetchPriority() {
        return 2;
    }
    @Override
    public void wrap(Queue<Object> operand) {
        right = operand.poll();
    }
    @Override
    public Object calculate() {
        Integer rval = (Integer) calculateItem(right);
        return ~rval;
    }
    @Override
    public String fetchSelf() {
        return "~";
    }
}
