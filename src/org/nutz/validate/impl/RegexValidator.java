package org.nutz.validate.impl;

import java.util.regex.Pattern;

import org.nutz.validate.NutValidateException;
import org.nutz.validate.NutValidator;

public class RegexValidator implements NutValidator {

    private Pattern p;

    public RegexValidator(String regex) {
        this.p = Pattern.compile(regex);
    }

    @Override
    public Object check(Object val) throws NutValidateException {
        if(null==val)
            return null;
        String s = val.toString();
        if (!p.matcher(s).find()) {
            throw new NutValidateException("InvalidString", p.toString(), s);
        }
        return val;
    }

    @Override
    public int order() {
        return 1000;
    }

}
