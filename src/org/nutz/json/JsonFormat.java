package org.nutz.json;

import java.util.regex.Pattern;

import org.nutz.castor.Castors;

public class JsonFormat {

	public static JsonFormat compact() {
		return new JsonFormat(true).setIgnoreNull(true);
	}

	public static JsonFormat full() {
		return new JsonFormat(false).setQuoteName(false).setIgnoreNull(false);
	}

	public static JsonFormat nice() {
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
	}

	private int indent;
	private String indentBy;
	private boolean compact;
	private boolean quoteName;
	private boolean ignoreNull;
	private Pattern actived;
	private Pattern locked;
	private Castors castors;

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

	Castors getCastors() {
		return castors;
	}

	public JsonFormat setCastors(Castors castors) {
		this.castors = castors;
		return this;
	}

}