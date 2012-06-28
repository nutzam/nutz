package org.nutz.el.opt.logic;

import org.nutz.el.opt.TwoTernary;

/**
 * 不等于
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class NEQOpt extends TwoTernary{
    public int fetchPriority() {
        return 6;
    }
    public Object calculate() {
        Object lval = calculateItem(this.left);
        Object rval = calculateItem(this.right);
        if(lval == rval){
            return false;
        }
        return !lval.equals(rval);
    }
    public String fetchSelf() {
        return "!=";
    }

}
