package org.nutz.el.obj;

import org.nutz.el.ElCache;
import org.nutz.lang.util.Context;

/**
 * 对象
 * @author juqkai(juqkai@gmail.com)
 */
public class AbstractObj implements Elobj{
    private String val;
    private ElCache ec;
    public AbstractObj(String val) {
        this.val = val;
    }
    @Override
    public String getVal() {
        return val;
    }
    @Override
    public Object fetchVal(){
        Context context = ec.getContext();
        if(context != null && context.has(val)){
            return context.get(val);
        }
        return null;
    }
    @Override
    public String toString() {
        return val;
    }
    @Override
    public void setEc(ElCache ec) {
        this.ec = ec;
    }
}
