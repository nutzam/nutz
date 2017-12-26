package org.nutz.el.opt.logic;

import org.nutz.castor.Castors;
import org.nutz.el.opt.TwoTernary;

/**
 * and
 * 
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class AndOpt extends TwoTernary {
    public int fetchPriority() {
        return 11;
    }

    public Object calculate() {
        Object lval = calculateItem(this.left);
        if (null == lval)
            return false;

        if (!(lval instanceof Boolean)) {
            // throw new ElException("操作数类型错误!");
            if (!Castors.me().castTo(lval, Boolean.class)) {
                return false;
            }
        } else if (!(Boolean) lval) {
            return false;
        }

        Object rval = calculateItem(this.right);
        if (null == rval)
            return false;
        if (!(rval instanceof Boolean)) {
            // throw new ElException("操作数类型错误!");
            return Castors.me().castTo(rval, Boolean.class);
        }
        return (Boolean) rval;
    }

    public String fetchSelf() {
        return "&&";
    }

}
