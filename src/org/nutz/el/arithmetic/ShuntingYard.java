package org.nutz.el.arithmetic;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import org.nutz.el.Operator;
import org.nutz.el.opt.arithmetic.LBracketOpt;
import org.nutz.el.opt.arithmetic.RBracketOpt;
import org.nutz.el.opt.logic.QuestionOpt;
import org.nutz.el.opt.logic.QuestionSelectOpt;
import org.nutz.el.parse.Converter;

/**
 * Shunting yard算法是一个用于将中缀表达式转换为后缀表达式的经典算法，由艾兹格·迪杰斯特拉引入，因其操作类似于火车编组场而得名。<br/>
 * 参考:
 * <a href='http://zh.wikipedia.org/wiki/Shunting_yard%E7%AE%97%E6%B3%95'>Shunting yard算法</a>
 * 
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class ShuntingYard {
    private LinkedList<Operator> opts;
    private Queue<Object> rpn;
    
    /**
     * 转换操作符.
     * 根据 ShuntingYard 算法进行操作
     * @param current
     */
    private void parseOperator(Operator current){
        //空,直接添加进操作符队列
        if(opts.isEmpty()){
            opts.addFirst(current);
            return;
        }
        //左括号
        if(current instanceof LBracketOpt){
            opts.addFirst(current);
            return;
        }
        //遇到右括号
        if(current instanceof RBracketOpt){
            while(!(opts.peek() instanceof LBracketOpt)){
                rpn.add(opts.poll());
            }
            opts.poll();
            return;
        }
        
        //符号队列top元素优先级大于当前,则直接添加到
        if(!opts.isEmpty() && opts.peek().fetchPriority() > current.fetchPriority()){
            opts.addFirst(current);
            return;
        }
        //一般情况,即优先级小于栈顶,那么直接弹出来,添加到逆波兰表达式中
        while(!opts.isEmpty() && opts.peek().fetchPriority() <= current.fetchPriority()){
            //三元表达式嵌套的特殊处理
            if(opts.peek() instanceof QuestionOpt && current instanceof QuestionOpt){
                break;
            }
            if(opts.peek() instanceof QuestionOpt && current instanceof QuestionSelectOpt){
                rpn.add(opts.poll());
                break;
            }
            rpn.add(opts.poll());
        }
        opts.addFirst(current);
    }
    
    /**
     * 转换成 逆波兰表示法（Reverse Polish notation，RPN，或逆波兰记法）
     * @param val
     * @throws IOException 
     */
    public Queue<Object> parseToRPN(String val) {
        rpn = new LinkedList<Object>();
        opts = new LinkedList<Operator>();
        
        Converter converter = new Converter(val);
        converter.initItems();
        while(!converter.isEnd()){
            Object item = converter.fetchItem();
            if(item instanceof Operator){
                parseOperator((Operator) item);
                continue;
            }
            rpn.add(item);
        }
        while(!opts.isEmpty()){
            rpn.add(opts.poll());
        }
        
        return rpn;
    }
}
