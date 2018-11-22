package org.nutz.el.opt.logic;

/**
 * 大于
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class GTOpt extends AbstractCompareOpt {
    
    @Override
    public int fetchPriority() {
        return 6;
    }

    @Override
    public Object calculate() {
        return compare() > 0;
    }

    @Override
    public String fetchSelf() {
        return ">";
    }

}
