package org.nutz.el.opt.bit;

import org.nutz.el.opt.TwoTernary;

/**
 * 异或
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class BitXro extends TwoTernary{
    public int fetchPriority() {
        return 9;
    }
    public Object calculate() {
        Integer lval = (Integer) calculateItem(left);
        Integer rval = (Integer) calculateItem(right);
        return lval ^ rval;
    }
    public String fetchSelf() {
        return "^";
    }
}
