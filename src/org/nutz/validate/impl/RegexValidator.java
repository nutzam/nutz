package org.nutz.validate.impl;

import java.util.regex.Pattern;

import org.nutz.lang.Strings;
import org.nutz.lang.util.Regex;
import org.nutz.validate.NutValidateException;
import org.nutz.validate.NutValidator;

public class RegexValidator implements NutValidator {
    
    private boolean not;

    private Pattern p;

    public RegexValidator(String regex) {
        if(regex.startsWith("!")) {
            this.not = true;
            regex = Strings.trim(regex.substring(1));
        }
        this.p = Regex.getPattern(regex);
    }

    @Override
    public Object check(Object val) throws NutValidateException {
        if(null==val)
            return null;
        String s = val.toString();
        boolean isMatched = p.matcher(s).find();
        if (isMatched ^ !not) {
            throw new NutValidateException("InvalidString", p.toString(), s);
        }
        return val;
    }

    @Override
    public int order() {
        return 1000;
    }

}
