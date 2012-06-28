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
    public int fetchPriority() {
        return 2;
    }
    public void wrap(Queue<Object> operand) {
        right = operand.poll();
    }
    public Object calculate() {
        Integer rval = (Integer) calculateItem(right);
        return ~rval;
    }
    public String fetchSelf() {
        return "~";
    }
}
