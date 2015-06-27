package org.nutz.dao;

import java.util.regex.Pattern;

import org.nutz.lang.Strings;

/**
 * 字段匹配器. 判断顺序 lock--actived-->ignoreNull
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author wendal
 */
public class FieldMatcher {

    /**
     * 构建一个字段匹配器.
     * @param actived 需要保留的字段,必须是合法的正则表达式,可以为null
     * @param locked  需要忽略的字段,必须是合法的正则表达式,可以为null
     * @param ignoreNull 是否忽略空值
     * @return 字段匹配器
     */
    public static FieldMatcher make(String actived, String locked, boolean ignoreNull) {
        FieldMatcher fm = new FieldMatcher();
        fm.ignoreNull = ignoreNull;
        if (!Strings.isBlank(actived))
            fm.actived = Pattern.compile(actived);
        if (!Strings.isBlank(locked))
            fm.locked = Pattern.compile(locked);
        return fm;
    }

    /**
     * 哪些字段可用
     */
    private Pattern actived;
    /**
     * 哪些字段不可用
     */
    private Pattern locked;
    /**
     * 是否忽略空值
     */
    private boolean ignoreNull;

    /**
     * 匹配顺序 locked -- actived-- ignoreNull
     * @param str 需要匹配的字段名称
     * @return true,如果可用
     */
    public boolean match(String str) {
        if (null != locked)
            if (locked.matcher(str).find())
                return false;
        if (null != actived)
            if (!actived.matcher(str).find())
                return false;
        return true;
    }

    public boolean isIgnoreNull() {
        return ignoreNull;
    }

    public void setIgnoreNull(boolean ignoreNull) {
        this.ignoreNull = ignoreNull;
    }

    public Pattern getActived() {
        return actived;
    }

    public Pattern getLocked() {
        return locked;
    }

    public void setActived(Pattern actived) {
        this.actived = actived;
    }

    public void setLocked(Pattern locked) {
        this.locked = locked;
    }

}
