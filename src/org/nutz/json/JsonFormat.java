package org.nutz.json;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

import org.nutz.castor.Castors;

/**
 * 描述Json输出的格式
 *
 * @author zozoh(zozohtnt@gmail.com)
 * @author Wendal(wendal1985@gmail.com)
 * @author 有心猴(belialofking@163.com)
 *
 */
public class JsonFormat implements Cloneable {

    /**
     * 紧凑模式 -- 无换行,忽略null值
     */
    public static JsonFormat compact() {
        return new JsonFormat(true).setIgnoreNull(true);
    }

    /**
     * 全部输出模式 -- 换行,不忽略null值
     */
    public static JsonFormat full() {
        return new JsonFormat(false).setIgnoreNull(false);
    }

    /**
     * 一般模式 -- 换行,但忽略null值
     */
    public static JsonFormat nice() {
        return new JsonFormat(false).setIgnoreNull(true);
    }

    /**
     * 为了打印出来容易看，把名字去掉引号
     */
    public static JsonFormat forLook() {
        return new JsonFormat(false).setQuoteName(false).setIgnoreNull(true);
    }

    /**
     * 不换行,不忽略空值
     */
    public static JsonFormat tidy() {
        return new JsonFormat(true).setIgnoreNull(false);
    }

    /**
     * 获得一个Json输出格式，默认格式如下: <br>
     * <li>使用紧凑模式输出
     * <li>缩进时用的字符串为『&nbsp;&nbsp;&nbsp;』（三个空格）
     * <li>给字段添加双引号
     * <li>分隔符为『"』
     *
     */
    public JsonFormat() {
        this(true);
    }

    /**
     * 获得一个Json输出格式，默认格式如下: <br>
     * <li>缩进时用的字符串为『&nbsp;&nbsp;&nbsp;』（三个空格）
     * <li>给字段添加双引号
     * <li>分隔符为『"』
     *
     * @param compact
     *            true: 使用紧凑模式输出，false: 不使用紧凑模式输出
     */
    public JsonFormat(boolean compact) {
        this.compact = compact;
        this.indentBy = "   ";
        this.quoteName = true;
        this.castors = Castors.me();
        this.separator = '\"';
    }

    /**
     * 缩进
     */
    private int indent;
    /**
     * 缩进时用的字符串
     */
    private String indentBy;
    /**
     * 是否使用紧凑模式输出
     */
    private boolean compact;
    /**
     * 是否给字段添加双引号
     */
    private boolean quoteName;
    /**
     * 是否忽略null值
     */
    private boolean ignoreNull;
    /**
     * 仅输出的字段的正则表达式
     */
    private Pattern actived;
    /**
     * 不输出的字段的正则表达式
     */
    private Pattern locked;
    /**
     * 用到的类型转换器
     */
    @JsonField(ignore = true)
    private Castors castors;
    /**
     * 分隔符
     */
    private char separator;
    /**
     * 是否自动将值应用Unicode编码
     */
    private boolean autoUnicode;
    /**
     * unicode编码用大写还是小写
     */
    private boolean unicodeLower;
    /**
     * 日期格式
     */
    private SimpleDateFormat dateFormat;
    /**
     * 数字格式
     */
    private NumberFormat numberFormat;

    /**
     * 判断该字段是否是指定输出方式中的字段
     *
     * @param name
     *            字段名
     * @return true: 该字段在忽略字段中，false: 该字段不在忽略字段中
     */
    public boolean ignore(String name) {
        if (null != actived)
            return !actived.matcher(name).find();
        if (null != locked)
            return locked.matcher(name).find();
        return false;
    }

    // ===================================================================
    // getter setter

    /**
     * Json输出格式的紧凑模式
     *
     * @return true: 使用紧凑模式输出，false: 不使用紧凑模式输出
     */
    public boolean isCompact() {
        return compact;
    }

    /**
     * 设置Json输出格式的紧凑模式
     *
     * @param compact
     *            true: 使用紧凑模式输出，false: 不使用紧凑模式输出
     * @return 该Json输出格式
     */
    public JsonFormat setCompact(boolean compact) {
        this.compact = compact;
        return this;
    }

    public int getIndent() {
        return indent;
    }

    public JsonFormat setIndent(int indent) {
        this.indent = indent;
        return this;
    }

    public JsonFormat increaseIndent() {
        this.indent++;
        return this;
    }

    public JsonFormat decreaseIndent() {
        this.indent--;
        return this;
    }

    /**
     * Json输出格式的缩进时用的字符串
     *
     * @return 缩进时用的字符串
     */
    public String getIndentBy() {
        return indentBy;
    }

    /**
     * 设置Json输出格式的缩进时用的字符串
     *
     * @param indentBy
     *            设置缩进时用的字符串
     * @return 该Json输出格式
     */
    public JsonFormat setIndentBy(String indentBy) {
        this.indentBy = indentBy;
        return this;
    }

    /**
     * Json输出格式的给字段添加双引号
     *
     * @return 是否给字段添加双引号
     */
    public boolean isQuoteName() {
        return quoteName;
    }

    /**
     * 设置Json输出格式的给字段添加双引号
     *
     * @param quoteName
     *            true: 给字段添加双引号，false: 不给字段添加双引号
     * @return 该Json输出格式
     */
    public JsonFormat setQuoteName(boolean quoteName) {
        this.quoteName = quoteName;
        return this;
    }

    /**
     * Json输出格式的忽略null值
     *
     * @return 是否忽略null的值
     */
    public boolean isIgnoreNull() {
        return ignoreNull;
    }

    /**
     * 设置Json输出格式中是否忽略null
     *
     * @param ignoreNull
     *            true: 忽略null的值，false: 不忽略null的值
     * @return 该Json输出格式
     */
    public JsonFormat setIgnoreNull(boolean ignoreNull) {
        this.ignoreNull = ignoreNull;
        return this;
    }

    /**
     * 设置Json输出格式中输出的字段
     *
     * @param regex
     *            输出的字段的正则表达式
     * @return 该Json输出格式
     */
    public JsonFormat setActived(String regex) {
        this.actived = Pattern.compile(regex);
        return this;
    }

    /**
     * 设置Json输出格式中不输出的字段
     *
     * @param regex
     *            不输出的字段的正则表达式
     * @return 该Json输出格式
     */
    public JsonFormat setLocked(String regex) {
        this.locked = Pattern.compile(regex);
        return this;
    }

    public Castors getCastors() {
        return castors == null ? Castors.me() : castors;
    }

    public JsonFormat setCastors(Castors castors) {
        this.castors = castors;
        return this;
    }

    /**
     * 设置Json输出格式的分隔符
     *
     * @param separator
     *            分隔符
     * @return 该Json输出格式
     */
    public JsonFormat setSeparator(char separator) {
        this.separator = separator;
        return this;
    }

    /**
     * Json输出格式的分隔符
     *
     * @return 分隔符
     */
    public char getSeparator() {
        return separator;
    }

    /**
     * 设置Json输出格式的自动将值应用unicode编码
     *
     * @param autoUnicode
     *            true: 自动将值应用unicode编码，false: 不自动将值应用unicode编码
     * @return 该Json输出格式
     */
    public JsonFormat setAutoUnicode(boolean autoUnicode) {
        this.autoUnicode = autoUnicode;
        return this;
    }

    /**
     * Json输出格式的自动unicode编码
     *
     * @return true: 自动将值应用unicode编码，false: 不自动将值应用unicode编码
     */
    public boolean isAutoUnicode() {
        return autoUnicode;
    }

    /**
     * Json输出格式的unicode编码
     *
     * @return true: unicode编码用大写，false: unicode编码用小写
     */
    public boolean isUnicodeLower() {
        return unicodeLower;
    }

    /**
     * 设置Json输出格式的unicode编码大小写规则
     *
     * @param unicodeLower
     *            true: unicode编码用大写，false: unicode编码用小写
     * @return 该Json输出格式
     */
    public JsonFormat setUnicodeLower(boolean unicodeLower) {
        this.unicodeLower = unicodeLower;
        return this;
    }

    /**
     * 设置Json输出格式的设置日期格式
     *
     * @param df
     *            日期格式
     * @return 该Json输出格式
     */
    public JsonFormat setDateFormat(String df) {
        if (df == null) {
            this.dateFormat = null;
        } else {
            this.dateFormat = new SimpleDateFormat(df);
        }
        return this;
    }

    /**
     * 设置Json输出格式的日期格式
     *
     * @param df
     *            日期格式
     * @return 该Json输出格式
     */
    public JsonFormat setDateFormat(SimpleDateFormat df) {
        this.dateFormat = df;
        return this;
    }

    /**
     * Json输出格式的日期格式
     *
     * @return 日期格式
     */
    public SimpleDateFormat getDateFormat() {
        return dateFormat == null ? null : (SimpleDateFormat) dateFormat.clone();
    }

    /**
     * Json输出格式的数字格式
     *
     * @return 数字格式
     */
    public NumberFormat getNumberFormat() {
        return numberFormat == null ? null : (NumberFormat) numberFormat.clone();
    }

    /**
     * 设置Json输出格式的数字格式
     *
     * @param numberFormat
     *            数字格式
     * @return 该Json输出格式
     */
    public JsonFormat setNumberFormat(NumberFormat numberFormat) {
        this.numberFormat = numberFormat;
        return this;
    }

    /**
     * 生成一个该Json输出格式的副本
     *
     * @return 该Json输出格式的副本
     */
    public JsonFormat clone() {
        JsonFormat jf = new JsonFormat();
        jf.indent = this.indent;
        jf.indentBy = this.indentBy;
        jf.compact = this.compact;
        jf.quoteName = this.quoteName;
        jf.ignoreNull = this.ignoreNull;
        jf.actived = this.actived;
        jf.locked = this.locked;
        jf.castors = this.castors;
        jf.separator = this.separator;
        jf.autoUnicode = this.autoUnicode;
        jf.unicodeLower = this.unicodeLower;
        jf.dateFormat = this.dateFormat;
        jf.numberFormat = this.numberFormat;
        return jf;
    }
}
