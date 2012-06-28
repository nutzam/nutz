package org.nutz.json;

import java.util.regex.Pattern;

import org.nutz.castor.Castors;

/**
 * 描述Json输出的格式
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author Wendal(wendal1985@gmail.com)
 * 
 */
public class JsonFormat {

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

    public JsonFormat() {
        this(true);
    }

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
     * 紧凑
     */
    private boolean compact;
    private boolean quoteName;
    /**
     * 是否忽略null值
     */
    private boolean ignoreNull;
    private Pattern actived;
    private Pattern locked;
    /**
     * 用到的类型转换器
     */
    private Castors castors;
    /**
     * 分隔符
     */
    private char separator;
    /**
     * 是否自动将值应用Unicode编码
     */
    private boolean autoUnicode;

    public boolean ignore(String name) {
        if (null != actived)
            return !actived.matcher(name).find();
        if (null != locked)
            return locked.matcher(name).find();
        return false;
    }
    
//===================================================================
//getter setter
    
    public boolean isCompact() {
        return compact;
    }

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

    public String getIndentBy() {
        return indentBy;
    }

    public JsonFormat setIndentBy(String indentBy) {
        this.indentBy = indentBy;
        return this;
    }

    public boolean isQuoteName() {
        return quoteName;
    }

    public JsonFormat setQuoteName(boolean qn) {
        this.quoteName = qn;
        return this;
    }

    public boolean isIgnoreNull() {
        return ignoreNull;
    }

    public JsonFormat setIgnoreNull(boolean ignoreNull) {
        this.ignoreNull = ignoreNull;
        return this;
    }

    public JsonFormat setActived(String regex) {
        this.actived = Pattern.compile(regex);
        return this;
    }

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

    public JsonFormat setSeparator(char separator) {
        this.separator = separator;
        return this;
    }

    public char getSeparator() {
        return separator;
    }

    public JsonFormat setAutoUnicode(boolean autoUnicode) {
        this.autoUnicode = autoUnicode;
        return this;
    }

    public boolean isAutoUnicode() {
        return autoUnicode;
    }
    
}