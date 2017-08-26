package org.nutz.el.opt.logic;

import java.math.BigDecimal;

import org.nutz.el.opt.TwoTernary;

/**
 * 等于
 * 
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class EQOpt extends TwoTernary {
    public int fetchPriority() {
        return 7;
    }

    public Object calculate() {
        Object lval = calculateItem(this.left);
        Object rval = calculateItem(this.right);
        if (lval == null && rval == null)
            return true;

        if (lval == null || rval == null)
            return false;

        if (lval == rval) {
            return true;
        }
        if (lval instanceof Number && rval instanceof Number) {
            return new BigDecimal(lval.toString()).compareTo(new BigDecimal(rval.toString())) == 0;
        }
        return lval.equals(rval);
    }

    public String fetchSelf() {
        return "==";
    }
}
