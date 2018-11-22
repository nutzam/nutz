package org.nutz.el.opt.arithmetic;

import org.nutz.el.opt.TwoTernary;

/**
 * 除
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class DivOpt extends TwoTernary {

    @Override
    public int fetchPriority() {
        return 3;
    }

    @Override
    public Object calculate() {
        Number lval = (Number) calculateItem(this.left);
        Number rval = (Number) calculateItem(this.right);
        if(rval instanceof Double || lval instanceof Double){
            return lval.doubleValue() / rval.doubleValue();
        }
        if(rval instanceof Float || lval instanceof Float){
            return lval.floatValue() / rval.floatValue();
        }
        if(rval instanceof Long || lval instanceof Long){
            return lval.longValue() / rval.longValue();
        }
        return lval.intValue() / rval.intValue();
    }
    
    @Override
    public String fetchSelf() {
        return "/";
    }

}
