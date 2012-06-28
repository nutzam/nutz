package org.nutz.el.opt.logic;

import org.nutz.el.opt.TwoTernary;

/**
 * 小于
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class LTOpt extends TwoTernary {

    public int fetchPriority() {
        return 6;
    }

    public String fetchSelf() {
        return "<";
    }

    public Object calculate() {
        Number lval = (Number) calculateItem(this.left);
        Number rval = (Number) calculateItem(this.right);
        if(rval instanceof Double || lval instanceof Double){
            return lval.doubleValue() < rval.doubleValue();
        }
        if(rval instanceof Float || lval instanceof Float){
            return lval.floatValue() < rval.floatValue();
        }
        if(rval instanceof Long || lval instanceof Long){
            return lval.longValue() < rval.longValue();
        }
        return lval.intValue() < rval.intValue();
    }

}
