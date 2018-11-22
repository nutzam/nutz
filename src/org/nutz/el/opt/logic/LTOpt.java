package org.nutz.el.opt.logic;

/**
 * 小于
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class LTOpt extends AbstractCompareOpt {

    @Override
    public int fetchPriority() {
        return 6;
    }

    @Override
    public String fetchSelf() {
        return "<";
    }

    @Override
    public Object calculate() {
        return compare() < 0;
    }

}
