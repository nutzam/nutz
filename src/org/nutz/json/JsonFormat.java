package org.nutz.json;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
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
		
		this.mates = new ArrayList<String>();
		this.filterType = FilterType.exclude;
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

	/**
	 * 采用 Nutz JSON 特殊兼容模式，这样，循环引用的字段，会标记成特殊字符串表一个路径
	 */
	private boolean nutzJson;
	
	/**
	 * 过滤匹配列表
	 */
	private List<String> mates;
	/**
	 * JSON层级路径
	 */
    private LinkedList<String> path = new LinkedList<String>();
    /**
     * 过滤类型, true为包含, false为排除
     */
    private FilterType filterType;
    
    
    
    /**
     * 包含
     */
    public boolean filter() {
        if (mates == null) {
            return true;
        }
        String path = fetchPath();
        for (String s : mates) {
            if (filterType == FilterType.include) {
                //包含
                if (s.startsWith(path)) {
                    return true;
                }
            } else {
                //排除
                if (s.equals(path)) {
                    return false;
                }
            }
        }
        return filterType == FilterType.include ? false : true;
    }
    /**
     * 获取路径
     * @return
     */
    private String fetchPath(){
        StringBuffer sb = new StringBuffer();
        for(String s : path){
            if(sb.length() > 0){
                sb.append(".");
            }
            sb.append(s);
        }
        return sb.toString();
    }
    /**
     * 压栈
     * @param path
     */
    public void pushPath(String path){
        this.path.addLast(path);
    }
    /**
     * 退栈
     */
    public void pollPath(){
        this.path.removeLast();
    }
    
    
    

	public void setMates(List<String> mates) {
        this.mates = mates;
    }

    public void setFilterType(FilterType filterType) {
        this.filterType = filterType;
    }

    public boolean isNutzJson() {
		return nutzJson;
	}

	public JsonFormat setNutzJson(boolean nutzJson) {
		this.nutzJson = nutzJson;
		return this;
	}

	public boolean ignore(String name) {
		if (null != actived)
			return !actived.matcher(name).find();
		if (null != locked)
			return locked.matcher(name).find();
		return false;
	}
	

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
	
	
	/**
	 * filter类型
	 * @author juqkai(juqkai@gmail.com)
	 */
	public static enum FilterType{
	    include, exclude
	}
    
    
}