package org.nutz.el.opt.arithmetic;

import java.util.Queue;

import org.nutz.el.ElException;
import org.nutz.el.opt.AbstractOpt;

/**
 * 右括号')'
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class RBracketOpt extends AbstractOpt{

    public int fetchPriority() {
        return 100;
    }
    public String fetchSelf() {
        return ")";
    }
    public void wrap(Queue<Object> obj) {
        throw new ElException("')符号不能进行wrap操作!'");
    }
    public Object calculate() {
        throw new ElException("')'符号不能进行计算操作!");
    }

}
