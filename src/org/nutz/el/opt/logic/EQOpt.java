package org.nutz.el.opt.logic;

import org.nutz.el.opt.TwoTernary;

/**
 * 等于
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
        if(lval == rval){
            return true;
        }
        return lval.equals(rval);
    }
    
    public String fetchSelf() {
        return "==";
    }
}
