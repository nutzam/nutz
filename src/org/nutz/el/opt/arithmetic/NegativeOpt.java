package org.nutz.el.opt.arithmetic;

import java.util.Queue;

import org.nutz.el.opt.AbstractOpt;
import org.nutz.el.opt.object.CommaOpt;

/**
 * 负号:'-'
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class NegativeOpt extends AbstractOpt {
    private Object right;

    public int fetchPriority() {
        return 2;
    }

    public void wrap(Queue<Object> operand) {
        right = operand.poll();
    }

    public Object calculate() {
        Object rval = calculateItem(this.right);
        if(rval instanceof Double)
            return 0 - (Double)rval;
        if(rval instanceof Float)
            return 0 - (Float)rval;
        if(rval instanceof Long)
            return 0 - (Long)rval;
        return 0 - (Integer)rval;
    }

    public String fetchSelf() {
        return "-";
    }
    
    public static boolean isNegetive(Object prev){
        if(prev == null){
            return true;
        }
        if (prev instanceof Object[]) {
            Object[] tmp = (Object[])prev;
            if (tmp.length == 0)
                return true;
            prev = tmp[tmp.length - 1];// 最后一个
        }
        if(prev instanceof LBracketOpt){
            return true;
        }
        if(prev instanceof PlusOpt){
            return true;
        }
        if(prev instanceof MulOpt){
            return true;
        }
        if(prev instanceof DivOpt){
            return true;
        }
        if(prev instanceof ModOpt){
            return true;
        }
        if(prev instanceof SubOpt){
            return true;
        }
        if (prev instanceof CommaOpt)
            return true;
        return false;
    }

}
