package org.nutz.el.opt.logic;

import java.util.Queue;

import org.nutz.castor.Castors;
import org.nutz.el.opt.AbstractOpt;

/**
 * Not(!)
 * 
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class NotOpt extends AbstractOpt {
    private Object right;

    @Override
    public int fetchPriority() {
        return 7;
    }

    @Override
    public void wrap(Queue<Object> rpn) {
        right = rpn.poll();
    }

    @Override
    public Object calculate() {
        Object rval = calculateItem(this.right);
        if (null == rval) {
            return true;
        }
        if (rval instanceof Boolean) {
            return !(Boolean) rval;
        }
        // throw new ElException("'!'操作符操作失败!");
        return !Castors.me().castTo(rval, Boolean.class);
    }

    @Override
    public String fetchSelf() {
        return "!";
    }
}
