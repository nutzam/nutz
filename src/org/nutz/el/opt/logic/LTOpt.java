package org.nutz.el.opt.logic;

/**
 * 小于
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class LTOpt extends AbstractCompareOpt {

    public int fetchPriority() {
        return 6;
    }

    public String fetchSelf() {
        return "<";
    }

    public Object calculate() {
        return compare() < 0;
    }

}
