package org.nutz.el.opt.object;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import org.nutz.el.ElException;
import org.nutz.el.Operator;
import org.nutz.el.obj.MethodObj;
import org.nutz.el.opt.RunMethod;
import org.nutz.el.opt.TwoTernary;
import org.nutz.el.opt.custom.CustomMake;


/**
 * 方法体封装.
 * 主要是把方法的左括号做为边界
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class MethodOpt extends TwoTernary {

    private int size = 0;
    
    public void setSize(int size) {
        this.size = size;
    }
    
    public int getSize() {
        return size;
    }

    public int fetchPriority() {
        return 1;
    }
    
    public void wrap(Queue<Object> rpn) {
        if(getSize() <= 0){
            left = rpn.poll();
        } else {
            right = rpn.poll();
            left = rpn.poll();
        }
    }
    
    public Object calculate(){
        return fetchMethod().run(fetchParam());
    }
    
    private RunMethod fetchMethod(){
        if(!(left instanceof AccessOpt)){
            if (left instanceof MethodObj) {
                final Object val = ((MethodObj)left).fetchVal();
                if (val != null) {
                    if (val instanceof Method) {
                        return new CustomMake.StaticMethodRunMethod((Method)val);
                    } else if (val instanceof RunMethod) {
                        return (RunMethod)val;
                    } else {
                        throw new ElException("must be Method or RunMethod, key="+left);
                    }
                }
            }
            RunMethod run = CustomMake.me().make(left.toString());
            if (run == null)
                throw new ElException("no such key="+left);
            return run;
        }
        return (AccessOpt) left;
    }
    
    
    /**
     * 取得方法执行的参数
     * @return
     */
    @SuppressWarnings("unchecked")
    private List<Object> fetchParam(){
        List<Object> rvals = new ArrayList<Object>();
        if(right != null){
            if(right instanceof CommaOpt){
                rvals = (List<Object>) ((CommaOpt) right).calculate();
            } else {
                rvals.add(calculateItem(right));
            }
        }
        if(!rvals.isEmpty()){
            for(int i = 0; i < rvals.size(); i ++){
                if(rvals.get(i) instanceof Operator){
                    rvals.set(i, ((Operator)rvals.get(i)).calculate());
                }
            }
        }
        return rvals;
    }
    
    public String fetchSelf() {
        return "method";
    }

    public String toString() {
        return super.toString() + "(" + size + ")";
    }
}
