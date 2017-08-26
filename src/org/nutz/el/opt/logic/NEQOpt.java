package org.nutz.el.opt.logic;

/**
 * 不等于
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class NEQOpt extends EQOpt{
    
    public int fetchPriority() {
        return 6;
    }

    public Boolean calculate() {
        return !super.calculate();
    }

    public String fetchSelf() {
        return "!=";
    }

}
