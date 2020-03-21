package org.nutz.validate.impl;

import org.nutz.validate.NutValidateException;
import org.nutz.validate.NutValidator;

public class StrValueValidator implements NutValidator {
    
    private String str;
    
    public StrValueValidator(Object any){
        this.str = any.toString();
    }

    @Override
    public Object check(Object val) throws NutValidateException {
        if(null==val) {
            return val;
        }
        return str.equals(val);
    }

    @Override
    public int order() {
        return 1000;
    }

}
