package org.nutz.el;

import java.util.Queue;

import org.nutz.el.arithmetic.ShuntingYard;
import org.nutz.el.arithmetic.RPN;
import org.nutz.lang.util.Context;

public class El {
    private RPN rc = null;
    private CharSequence elstr = "";
    
    public El(){}
    /**
     * 预编译
     */
    public El(CharSequence cs){
        elstr = cs;
        ShuntingYard sy = new ShuntingYard();
        Queue<Object> rpn = sy.parseToRPN(cs.toString());
        rc = new RPN(rpn);
    }
    /**
     * 解析预编译后的EL表达式
     */
    public Object eval(Context context) {
        if(rc == null){
            throw new ElException("没有进行预编译!");
        }
        return rc.calculate(context);
    }

    /**
     * 对参数代表的表达式进行运算
     */
    public static Object eval(String val) {
        //逆波兰表示法（Reverse Polish notation，RPN，或逆波兰记法）
        return eval(null, val);
    }

    public static Object eval(Context context, String val) {
        ShuntingYard sy = new ShuntingYard();
        RPN rc = new RPN();
        Queue<Object> rpn = sy.parseToRPN(val);
        return rc.calculate(context, rpn);
    }
    
    public String toString() {
        return elstr.toString();
    }
    
    /**
     * 说明:
     * 1. 操作符优先级参考<Java运算符优先级参考图表>, 但不完全遵守,比如"()"
     * 2. 使用Queue 的原因是,调用peek()方法不会读取串中的数据.
     * 因为我希望达到的效果是,我只读取我需要的,我不需要的数据我不读出来.
     */
    
    //@ JKTODO 删除原来的EL包,并修改当前为EL
    //@ JKTODO 自己实现一个QUEUE接口, 主要是实现队列,头部检测,头部第几个元素检测
}
