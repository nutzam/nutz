package org.nutz.el.arithmetic;

import java.util.LinkedList;
import java.util.Queue;

import org.nutz.el.ElCache;
import org.nutz.el.Operator;
import org.nutz.el.obj.Elobj;
import org.nutz.lang.util.Context;

/**
 * 逆波兰表示法（Reverse Polish notation，RPN，或逆波兰记法），是一种是由波兰数学家扬·武卡谢维奇1920年引入的数学表达式方式，在逆波兰记法中，所有操作符置于操作数的后面，因此也被称为后缀表示法。<br/>
 * 参考:<a href="http://zh.wikipedia.org/wiki/%E9%80%86%E6%B3%A2%E5%85%B0%E8%A1%A8%E7%A4%BA%E6%B3%95">逆波兰表达式</a>
 * 
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class RPN {
    //存放context
    private final ElCache ec = new ElCache();
    //预编译后的对象
    private LinkedList<Object> el;
    
    public RPN() {}
    
    /**
     * 进行EL的预编译
     */
    public RPN(Queue<Object> rpn) {
        compile(rpn);
    }
    
    /**
     * 执行已经预编译的EL
     */
    public Object calculate(Context context){
        ec.setContext(context);
        return calculate(el);
    }
    /**
     * 根据逆波兰表达式进行计算
     */
    public Object calculate(Context context, Queue<Object> rpn) {
        ec.setContext(context);
        LinkedList<Object> operand = OperatorTree(rpn);
        return calculate(operand);
    }
    
    /**
     * 计算
     */
    private Object calculate(LinkedList<Object> el2){
        if(el2.peek() instanceof Operator){
            Operator obj = (Operator) el2.peek();
            return obj.calculate();
        }
        if(el2.peek() instanceof Elobj){
            return ((Elobj) el2.peek()).fetchVal();
        }
        return el2.peek();
    }
    
    /**
     * 预先编译
     */
    public void compile(Queue<Object> rpn){
        el = OperatorTree(rpn);
    }
    
    /**
     * 转换成操作树
     */
    private LinkedList<Object> OperatorTree(Queue<Object> rpn){
        LinkedList<Object> operand = new LinkedList<Object>();
        while(!rpn.isEmpty()){
            if(rpn.peek() instanceof Operator){
                Operator opt = (Operator) rpn.poll();
                opt.wrap(operand);
                operand.addFirst(opt);
                continue;
            }
            if(rpn.peek() instanceof Elobj){
                ((Elobj) rpn.peek()).setEc(ec);
            }
            operand.addFirst(rpn.poll());
        }
        return operand;
    }
}
