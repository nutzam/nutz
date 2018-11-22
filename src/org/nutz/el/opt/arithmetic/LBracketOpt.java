package org.nutz.el.opt.arithmetic;

import java.util.Queue;

import org.nutz.el.ElException;
import org.nutz.el.opt.AbstractOpt;

/**
 * "("
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class LBracketOpt extends AbstractOpt{
    @Override
    public String fetchSelf() {
        return "(";
    }
    @Override
    public int fetchPriority() {
        return 100;
    }
    
    @Override
    public void wrap(Queue<Object> obj) {
        throw new ElException("'('符号不能进行wrap操作!");
    }
    @Override
    public Object calculate() {
        throw new ElException("'('符号不能进行计算操作!");
    }
}
