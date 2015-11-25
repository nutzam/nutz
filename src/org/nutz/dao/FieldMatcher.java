package org.nutz.dao;

import java.util.regex.Pattern;

import org.nutz.lang.Strings;

/**
 * 字段匹配器. 判断顺序 locked--actived-->ignoreNull. 
 * 除locked/actived/ignoreNull之外的属性, 当前仅用于Cnd.from和Chain.from方法. <p/>
 * ignoreId现在可以用于dao.insert了<p/>
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
     * @return 字段/属性匹配器
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
     * 仅配置是否忽略@Id标注的属性,然后生成实例
     * @param ignoreId 是否忽略@Id标注的属性
     * @return 字段匹配器实例
     */
    public static FieldMatcher create(boolean ignoreId) {
        FieldMatcher fm = new FieldMatcher();
        fm.ignoreId = ignoreId;
        return fm;
    }
    
    /**
     * 构建一个字段匹配器.
     * @param actived 需要保留的字段,必须是合法的正则表达式,可以为null
     * @param locked  需要忽略的字段,必须是合法的正则表达式,可以为null
     * @param ignoreNull 是否忽略空值
     * @param ignoreZero 是否忽略零值,仅针对数值类型
     * @param ignoreDate 是否忽略java.util.Date及其子类的属性
     * @param ignoreId   是否忽略@Id标注的属性
     * @param ignoreName 是否忽略@Name标注的属性
     * @param ignorePk   是否忽略@Pk引用的属性
     * @return 字段/属性匹配器
     */
    public static FieldMatcher make(String actived, String locked, boolean ignoreNull, 
                                    boolean ignoreZero, boolean ignoreDate, 
                                    boolean ignoreId,
                                    boolean ignoreName,
                                    boolean ignorePk) {
        FieldMatcher fm = make(actived, locked, ignoreNull);
        fm.ignoreZero = ignoreZero;
        fm.ignoreDate = ignoreDate;
        fm.ignoreId = ignoreId;
        fm.ignoreName = ignoreName;
        fm.ignorePk = ignorePk;
        return fm;
    }
    
    /**
     * 构建一个字段匹配器.
     * @param actived 需要保留的字段,必须是合法的正则表达式,可以为null
     * @param locked  需要忽略的字段,必须是合法的正则表达式,可以为null
     * @param ignoreNull 是否忽略空值
     * @param ignoreZero 是否忽略零值,仅针对数值类型
     * @param ignoreDate 是否忽略java.util.Date及其子类的属性
     * @param ignoreId   是否忽略@Id标注的属性
     * @param ignoreName 是否忽略@Name标注的属性
     * @param ignorePk   是否忽略@Pk引用的属性
     * @return 字段/属性匹配器
     */
    public static FieldMatcher make(String actived, String locked, boolean ignoreNull, 
                                    boolean ignoreZero, boolean ignoreDate, 
                                    boolean ignoreId,
                                    boolean ignoreName,
                                    boolean ignorePk,
                                    boolean ignoreBlankStr) {
        FieldMatcher fm = make(actived, locked, ignoreNull);
        fm.ignoreZero = ignoreZero;
        fm.ignoreDate = ignoreDate;
        fm.ignoreId = ignoreId;
        fm.ignoreName = ignoreName;
        fm.ignorePk = ignorePk;
        fm.ignoreBlankStr = ignoreBlankStr;
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
    private boolean ignoreNull = true;
    /**
     * 是否忽略空白字符串
     */
    private boolean ignoreBlankStr;
    /**
     * 是否忽略零值
     */
    private boolean ignoreZero = true;
    /**
     * 是否忽略日期
     */
    private boolean ignoreDate;
    /**
     * 是否忽略@Id标注的属性
     */
    private boolean ignoreId = true;
    /**
     * 是否忽略@Name标注的属性
     */
    private boolean ignoreName;
    /**
     * 是否忽略@Pk标注引用的属性
     */
    private boolean ignorePk;

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

    /**
     * 是否忽略控制
     * @return true,如果忽略控制
     */
    public boolean isIgnoreNull() {
        return ignoreNull;
    }

    /**
     * 设置是否忽略空值
     * @param ignoreNull 是否忽略空值属性
     * @return 原对象,用于链式调用
     */
    public FieldMatcher setIgnoreNull(boolean ignoreNull) {
        this.ignoreNull = ignoreNull;
        return this;
    }

    /**
     * 获取可用字段/属性的正则表达式
     * @return 正则表达式,可能为null
     */
    public Pattern getActived() {
        return actived;
    }

    /**
     * 获取不可用字段/属性的正则表达式
     * @return 正则表达式,可能为null
     */
    public Pattern getLocked() {
        return locked;
    }

    /**
     * 设置过滤可用字段/属性的正则表达式
     * @param actived 正则表达式
     * @return 原对象,用于链式调用
     */
    public FieldMatcher setActived(String actived) {
        if (actived != null)
            this.actived = Pattern.compile(actived);
        else
            this.actived = null;
        return this;
    }

    /**
     * 设置过滤不可用字段/属性的正则表达式
     * @param locked 正则表达式
     * @return 原对象,用于链式调用
     */
    public FieldMatcher setLocked(String locked) {
        if (locked != null)
            this.locked = Pattern.compile(locked);
        else
            this.locked = null;
        return this;
    }

    /**
     * 是否忽略零值
     * @return true,如果是的话,默认为false
     */
    public boolean isIgnoreZero() {
        return ignoreZero;
    }

    /**
     * 设置是否忽略零值
     * @param ignoreZero 是否忽略零值
     * @return 原对象,用于链式调用
     */
    public FieldMatcher setIgnoreZero(boolean ignoreZero) {
        this.ignoreZero = ignoreZero;
        return this;
    }

    /**
     * 是否忽略Date及其子类的属性值
     * @return true,如果需要忽略的话
     */
    public boolean isIgnoreDate() {
        return ignoreDate;
    }

    /**
     * 设置是否忽略Date及其子类的属性值
     * @param ignoreDate 是否忽略
     * @return 原对象,用于链式调用
     */
    public FieldMatcher setIgnoreDate(boolean ignoreDate) {
        this.ignoreDate = ignoreDate;
        return this;
    }

    /**
     * 是否忽略@Id标注的属性,默认忽略
     * @return true,如果忽略
     */
    public boolean isIgnoreId() {
        return ignoreId;
    }

    /**
     * 设置是否忽略@Id标注的属性
     * @param ignoreId 是否忽略
     * @return 原对象,用于链式调用
     */
    public FieldMatcher setIgnoreId(boolean ignoreId) {
        this.ignoreId = ignoreId;
        return this;
    }

    /**
     * 是否忽略@Name标注的属性,默认是false
     * @return true,如果忽略的话
     */
    public boolean isIgnoreName() {
        return ignoreName;
    }

    /**
     * 设置是否忽略@Name标注的属性
     * @param ignoreName 是否忽略
     * @return 原对象,用于链式调用
     */
    public FieldMatcher setIgnoreName(boolean ignoreName) {
        this.ignoreName = ignoreName;
        return this;
    }

    /**
     * 是否忽略@Pk所引用的属性,默认是false
     * @return true,如果忽略的话
     */
    public boolean isIgnorePk() {
        return ignorePk;
    }

    /**
     * 设置是否忽略@Pk所引用的属性
     * @param ignorePk 是否忽略
     * @return 原对象,用于链式调用
     */
    public FieldMatcher setIgnorePk(boolean ignorePk) {
        this.ignorePk = ignorePk;
        return this;
    }

    public boolean isIgnoreBlankStr() {
        return ignoreBlankStr;
    }

    public void setIgnoreBlankStr(boolean ignoreBlankStr) {
        this.ignoreBlankStr = ignoreBlankStr;
    }

}
