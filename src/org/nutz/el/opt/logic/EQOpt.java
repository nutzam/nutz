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

    public Boolean calculate() {
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
            if (!(lval instanceof BigDecimal))
                lval = new BigDecimal(lval.toString());
            if (!(rval instanceof BigDecimal))
                rval = new BigDecimal(rval.toString());
            return ((BigDecimal)lval).compareTo((BigDecimal)rval) == 0;
        }
        return lval.equals(rval);
    }

    public String fetchSelf() {
        return "==";
    }
}
