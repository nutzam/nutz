package org.nutz.lang.tmpl;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutBean;
import org.nutz.lang.util.NutMap;

/**
 * 占位符支持 `${路径<类型:格式>?默认值}` 的写法
 * <p/>
 * 支持的类型为:
 * <ul>
 * <li><b>int</b> : %d 格式化字符串
 * <li><b>long</b> : %d 格式化字符串
 * <li><b>float</b> : %f 格式化字符串
 * <li><b>double</b>: %f 格式化字符串
 * <li><b>boolean</b>: 否/是 格式化字符串
 * <li><b>date</b> : yyyyMMdd 格式化字符串
 * <li><b>string</b>: -无格式化-
 * </ul>
 * <p/>
 * 如果没有声明类型，则当做 "string"
 *
 * @author zozoh(zozohtnt@gmail.com)
 */
public class Tmpl {

    private static final Pattern _P2 = Pattern.compile("([\\w\\d_.\\[\\]'\"-]+)"
                                                       + "(<(int|long|boolean|float|double|date|string)( *: *([^>]*))?>)?"
                                                       + "([?] *(.*) *)?");
    private Pattern _P;
    private int groupIndex;
    private List<TmplEle> list;
    private List<String> keys;

    /**
     * 解析模板对象
     *
     * @param tmpl
     *            模板字符串
     * @return 解析好的模板对象
     *
     * @see #parse(String, Pattern, int)
     */
    public static Tmpl parse(String tmpl) {
        return new Tmpl(tmpl, null, -1);
    }

    public static Tmpl parsef(String fmt, Object... args) {
        return new Tmpl(String.format(fmt, args), null, -1);
    }

    /**
     * 解析模板对象，并用上下文进行渲染。DDD
     * <p/>
     * 你可以通过参数 ptn 指定自定义的正则表达式来声明自己的模板占位符形式。 <br>
     * 默认的模板占位符是 <code>(?&lt;![$])[$][{]([^}]+)[}]</code>
     * <p/>
     * 即，形式如 <code>${xxxx}</code> 的会被当做占位符， 同时 <code>$$</code> 可以逃逸
     *
     * @param tmpl
     *            模板字符串
     * @param ptn
     *            一个正则表达式，指明占位符的形式。
     * @param groupIndex
     *            指定正则表达式，哪个匹配组作为你的占位符内容
     * @return 模板对象
     */
    public static Tmpl parse(String tmpl, Pattern ptn, int groupIndex) {
        return new Tmpl(tmpl, ptn, groupIndex);
    }

    /**
     * @see #exec(String, Pattern, int, NutMap, boolean)
     */
    public static String exec(String tmpl, NutBean context) {
        return exec(tmpl, null, -1, context, true);
    }

    /**
     * @see #exec(String, Pattern, int, NutMap, boolean)
     */
    public static String exec(String tmpl, NutBean context, boolean showKey) {
        return exec(tmpl, null, -1, context, showKey);
    }

    /**
     * 解析模板对象，并用上下文进行渲染。
     *
     * @param tmpl
     *            模板字符串
     * @param ptn
     *            一个正则表达式，指明占位符的形式。
     * @param groupIndex
     *            指定正则表达式，哪个匹配组作为你的占位符内容
     * @param context
     *            上下文
     * @param showKey
     *            如果占位符不存在，也没有默认值，是否显示 KEY
     * @return 渲染结果
     * 
     * @see #parse(String, Pattern, int)
     */
    public static String exec(String tmpl,
                              Pattern ptn,
                              int groupIndex,
                              NutBean context,
                              boolean showKey) {
        return new Tmpl(tmpl, ptn, groupIndex).render(context, showKey);
    }

    private Tmpl() {
        list = new LinkedList<TmplEle>();
        keys = new LinkedList<String>();
    }

    private Tmpl(Pattern ptn, int groupIndex) {
        this();
        // 默认的模板占位符
        if (null == ptn) {
            _P = Pattern.compile("(?<![$])[$][{]([^}]+)[}]");
            this.groupIndex = 1;
        }
        // 自定义的占位符
        else {
            _P = ptn;
            this.groupIndex = groupIndex;
        }
    }

    private Tmpl(String tmpl, Pattern ptn, int groupIndex) {
        this(ptn, groupIndex);

        // 开始解析
        Matcher m = _P.matcher(tmpl);
        int lastIndex = 0;

        while (m.find()) {
            int pos = m.start();
            // 看看是否要生成静态对象
            if (pos > lastIndex) {
                list.add(new TmplStaticEle(tmpl.substring(lastIndex, pos)));
            }

            // 分析键
            Matcher m2 = _P2.matcher(m.group(this.groupIndex));

            if (!m2.find())
                throw Lang.makeThrow("Fail to parse tmpl key '%s'", m.group(1));

            String key = m2.group(1);
            String type = Strings.sNull(m2.group(3), "string");
            String fmt = m2.group(5);
            String dft = m2.group(7);

            // 记录键
            keys.add(key);

            // 创建元素
            if ("string".equals(type)) {
                list.add(new TmplStringEle(key, dft));
            }
            // int
            else if ("int".equals(type)) {
                list.add(new TmplIntEle(key, fmt, dft));
            }
            // long
            else if ("long".equals(type)) {
                list.add(new TmplLongEle(key, fmt, dft));
            }
            // boolean
            else if ("boolean".equals(type)) {
                list.add(new TmplBooleanEle(key, fmt, dft));
            }
            // float
            else if ("float".equals(type)) {
                list.add(new TmplFloatEle(key, fmt, dft));
            }
            // double
            else if ("double".equals(type)) {
                list.add(new TmplDoubleEle(key, fmt, dft));
            }
            // date
            else if ("date".equals(type)) {
                list.add(new TmplDateEle(key, fmt, dft));
            }
            // 靠不可能
            else {
                throw Lang.impossible();
            }

            // 记录
            lastIndex = m.end();
        }

        // 最后结尾是否要加入一个对象
        if (!(lastIndex >= tmpl.length())) {
            list.add(new TmplStaticEle(tmpl.substring(lastIndex)));
        }

    }

    public String render(NutBean context) {
        return render(context, true);
    }

    public String render(NutBean context, boolean showKey) {
        StringBuilder sb = new StringBuilder();
        if (null == context)
            context = new NutMap();
        for (TmplEle ele : list) {
            ele.join(sb, context, showKey);
        }
        return sb.toString();
    }

    public List<String> keys() {
        return this.keys;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (TmplEle ele : list) {
            sb.append(ele);
        }
        return sb.toString();
    }

}
