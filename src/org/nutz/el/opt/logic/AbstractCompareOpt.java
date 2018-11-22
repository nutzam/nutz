package org.nutz.el.opt.logic;

import java.math.BigDecimal;

import org.nutz.el.opt.TwoTernary;

public abstract class AbstractCompareOpt extends TwoTernary {

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected int compare() {
        Object lval = calculateItem(this.left);
        Object rval = calculateItem(this.right);
        
        if (lval == rval)
            return 0;
        if (lval == null && rval == null)
            return 0;

        if (lval != null && rval == null)
            return 1;
        if (lval == null)
            return -1;
        if (lval instanceof Number && rval instanceof Number) {
            if (!(lval instanceof BigDecimal))
                lval = new BigDecimal(lval.toString());
            if (!(rval instanceof BigDecimal))
                rval = new BigDecimal(rval.toString());
            return ((BigDecimal)lval).compareTo((BigDecimal)rval);
        }
        if (lval instanceof Comparable && lval.getClass().isInstance(rval))
            return ((Comparable)lval).compareTo(rval);
        if (lval.equals(rval))
            return 0;
        return lval.toString().compareTo(rval.toString());
    }
}
