package org.nutz.el.opt.logic;

/**
 * 等于
 * 
 * @author juqkai(juqkai@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class EQOpt extends AbstractCompareOpt {
    
    public int fetchPriority() {
        return 7;
    }

    public Boolean calculate() {
        return compare() == 0;
    }

    public String fetchSelf() {
        return "==";
    }
}
