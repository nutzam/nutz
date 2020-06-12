package org.nutz.validate;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.nutz.lang.util.NutMap;
import org.nutz.validate.impl.*;

/**
 * 一个简便的验证工具类，接受一个 Map
 * 
 * <pre>
 * {
       // 针对字符串型的值，检查前是否要预先去掉左右空白
       trim : true,
       // 数字区间
       intRange : "(10,20]",
       // 日期范围的区间
       dateRange : "(2018-12-02,2018-12-31]",
       // 验证值的字符串形式，支持 "!" 开头
       regex : "^...$",
       // 枚举验证·整数
       intEnum : [1,3,4],
       intEnum : "1,3,4",
       // 枚举验证·字符串
       strEnum : ["A","B","C],
       strEnum : "A,B,C",
       // 通配符
       wildcard : "*A*",
       // 精确数字
       intValue : 3,
       intValue : "3",
       // 精确字符串
       strValue : "ABC",
       // 精确布尔
       boolValue : true,
       // 确保值非 null
       notNull : true,
       // 针对字符串的值，最大长度不超过多少
       maxLength : 23,
       // 针对字符串的值，最小长度不能低于多少
       minLength : 5,
   }
 * </pre>
 * 
 * <ul>
 * <li>所有项目都是 `AND`的关系
 * <li>检查的顺序会是 `trim > notNull > max/minLength > 其他`
 * </ul>
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class NutValidate {

    private List<NutValidator> items;

    /**
     * 构建一个空白的检查器
     */
    public NutValidate() {
        items = new LinkedList<NutValidator>();
    }

    /**
     * @param map
     *            复合条件
     */
    public NutValidate(Map<String, Object> map) {
        this();
        items.clear();
        this.addAll(map);
        this.ready();
    }

    /**
     * 根据输入字符串自动判断类型
     * 
     * @param str
     *            输入字符串
     */
    /**
     * @param str
     */
    public NutValidate(String str) {
        this();

        // 一定是非空的
        this.add(new NotNullValidator());
        // 通配符
        if (str.startsWith("*") || str.endsWith("*")) {
            this.add(new WildcardValidator(str));
        }
        // 正则表达式
        else if (str.startsWith("^") || str.startsWith("!^")) {
            this.add(new RegexValidator(str));
        }
        // 范围
        else if (str.matches("^[\\[(].+[\\])]$")) {
            // 范围·日期
            if (str.indexOf('/') >= 0 || str.indexOf('-') >= 0) {
                this.add(new DateRangeValidator(str));
            }
            // 范围·数字
            else {
                this.add(new IntRangeValidator(str));
            }
        }
        // 精确匹配数字
        else if (str.matches("^\\d+$")) {
            this.add(new IntValueValidator(str));
        }
        // 精确匹配字符串
        else {
            this.add(new StrValueValidator(str));
        }
    }

    /**
     * 根据一个描述的表增加自身的检查项，
     * 
     * @param map
     *            描述检查项的 Map
     * @return 自身以便链式赋值
     */
    public NutValidate addAll(Map<String, Object> map) {
        NutMap m2 = NutMap.WRAP(map);
        for (String key : m2.keySet()) {
            // 针对字符串型的值，检查前是否要预先去掉左右空白
            if ("trim".equals(key)) {
                this.items.add(new TrimValidator());
            }
            // 数字区间
            else if ("intRange".equals(key)) {
                String str = m2.getString(key);
                this.items.add(new IntRangeValidator(str));
            }
            // 日期范围的区间
            else if ("dateRange".equals(key)) {
                String str = m2.getString(key);
                this.items.add(new DateRangeValidator(str));
            }
            // 验证值的字符串形式，支持 "!" 开头
            else if ("regex".equals(key)) {
                String str = m2.getString(key);
                this.items.add(new RegexValidator(str));
            }
            // 枚举验证·整数
            else if ("intEnum".equals(key)) {
                Object val = m2.get(key);
                this.items.add(new IntEnumValidator(val));
            }
            // 枚举验证·字符串
            else if ("strEnum".equals(key)) {
                Object val = m2.get(key);
                this.items.add(new StrEnumValidator(val));
            }
            // 通配符
            else if ("wildcard".equals(key)) {
                String str = m2.getString(key);
                this.items.add(new WildcardValidator(str));
            }
            // 精确数字
            else if ("intValue".equals(key)) {
                Object val = m2.get(key);
                this.items.add(new IntValueValidator(val));
            }
            // 精确字符串
            else if ("strValue".equals(key)) {
                Object val = m2.get(key);
                this.items.add(new StrValueValidator(val));
            }
            // 精确布尔
            else if ("boolValue".equals(key)) {
                Object val = m2.get(key);
                this.items.add(new BoolValueValidator(val));
            }
            // 确保值非 null
            else if ("notNull".equals(key)) {
                this.items.add(new NotNullValidator());
            }
            // 针对字符串的值，最大长度不超过多少
            else if ("maxLength".equals(key)) {
                int len = m2.getInt(key);
                this.items.add(new MaxLengthValidator(len));
            }
            // 针对字符串的值，最小长度不能低于多少
            else if ("minLength".equals(key)) {
                int len = m2.getInt(key);
                this.items.add(new MinLengthValidator(len));
            }
            // 其他的无视
        }
        return this;
    }

    /**
     * 增加一个检查器
     * 
     * @param nvs
     *            检查器列表
     * @return 自身以便链式赋值
     */
    public NutValidate add(NutValidator... nvs) {
        for (NutValidator nv : nvs)
            this.items.add(nv);
        return this;
    }

    /**
     * 根据检查器的优先顺序，重新调整检查列表
     * 
     * @return 自身以便链式赋值
     */
    public NutValidate ready() {
        Collections.sort(items, new Comparator<NutValidator>() {
            public int compare(NutValidator v1, NutValidator v2) {
                return v1.order() - v2.order();
            }
        });
        return this;
    }

    /**
     * 清除自身的检查链
     * 
     * @return 自身以便链式赋值
     */
    public NutValidate reset() {
        items.clear();
        return this;
    }

    /**
     * 执行检查
     * 
     * @param val
     *            被检查的值
     * @return 检查后的结果，可能会被修改，譬如 `trim` 操作
     * @throws NutValidateException
     *             - 如果任何一个检查器除了错误，就会抛出本错误，并中断后续的检查
     */
    public Object check(Object val) throws NutValidateException {
        Object re = val;
        for (NutValidator nv : items) {
            re = nv.check(re);
        }
        return re;
    }

    /**
     * 看看某个给定的值是否能通过所有检查器的检查
     * 
     * @param val
     *            被检查的值
     * @return true 通过了所有的检查。 false 某些检查未被通过
     */
    public boolean match(Object val) {
        try {
            this.check(val);
            return true;
        }
        catch (NutValidateException e) {
            return false;
        }
    }
}
