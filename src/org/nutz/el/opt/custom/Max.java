package org.nutz.el.opt.custom;

import java.util.List;

import org.nutz.el.opt.RunMethod;
import org.nutz.plugin.Plugin;

/**
 * 取大
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class Max implements RunMethod, Plugin{
    public Object run(List<Object> param) {
        if(param.size() <= 0){
            return null;
        }
        Number n1 = (Number) param.get(0);
        for(int i = 1; i < param.size(); i ++){
            Number n2 = (Number) param.get(i);
            n1 = (Number) max(n1, n2);
        }
        return n1;
    }
    
    private Object max(Number n1, Number n2){
        if(n1 == null){
            return n2;
        }
        if(n2 == null){
            return n1;
        }
        if(n1 instanceof Double || n2 instanceof Double){
            return Math.max(n1.doubleValue(), n2.doubleValue());
        }
        if(n1 instanceof Float || n2 instanceof Float){
            return Math.max(n1.floatValue(), n2.floatValue());
        }
        if(n1 instanceof Long || n2 instanceof Long){
            return Math.max(n1.longValue(), n2.longValue());
        }
        return Math.max(n1.intValue(), n2.intValue());
    }

    public boolean canWork() {
        return true;
    }

    public String fetchSelf() {
        return "max";
    }

}
