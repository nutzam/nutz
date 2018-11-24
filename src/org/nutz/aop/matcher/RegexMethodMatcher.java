package org.nutz.aop.matcher;

import static java.lang.reflect.Modifier.TRANSIENT;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.regex.Pattern;

import org.nutz.aop.MethodMatcher;
import org.nutz.lang.Maths;

public class RegexMethodMatcher implements MethodMatcher {

    private Pattern active;
    private Pattern ignore;
    private int mods;

    public RegexMethodMatcher(String active) {
        this(active, null);
    }

    public RegexMethodMatcher(String active, String ignore) {
        this(active, ignore, Modifier.PUBLIC | Modifier.PROTECTED);
    }

    public RegexMethodMatcher(String active, String ignore, int mods) {
        if (active != null)
        this.active = Pattern.compile(active);
        if (ignore != null)
            this.ignore = Pattern.compile(ignore);
        this.mods = mods;
    }

    public boolean match(Method method) {
        int mod = method.getModifiers();
        String name = method.getName();
        if (null != ignore)
            if (ignore.matcher(name).find())
                return false;
        if (null != active)
            if (!active.matcher(name).find())
                return false;
        if (mods <= 0)
            return true;

        if (mod == 0)
            mod |= TRANSIENT;

        return Maths.isMask(mod, mods);
    }

}
