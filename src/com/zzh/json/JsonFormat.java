package com.zzh.json;

public class JsonFormat {

	public static JsonFormat compact() {
		return new JsonFormat(true);
	}

	public static JsonFormat clear() {
		return new JsonFormat(false).setNotNeedQuoteName(false).setIgnoreNull(false);
	}

	public static JsonFormat nice() {
		return new JsonFormat(false).setNotNeedQuoteName(true).setIgnoreNull(true);
	}

	public JsonFormat(boolean compact) {
		this.compact = compact;
		this.indentBy = "   ";
	}

	protected int indent;
	protected String indentBy;
	protected boolean compact;
	protected boolean notNeedQuoteName;
	protected boolean ignoreNull;
	protected String activedFields;
	protected String ignoreFields;

	public boolean ignore(String name) {
		if (null != activedFields)
			return activedFields.indexOf("[" + name + "]") == -1;
		if (null != ignoreFields)
			return ignoreFields.indexOf("[" + name + "]") != -1;
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

	public String getIndentBy() {
		return indentBy;
	}

	public JsonFormat setIndentBy(String indentBy) {
		this.indentBy = indentBy;
		return this;
	}

	public boolean isNotNeedQuoteName() {
		return notNeedQuoteName;
	}

	public JsonFormat setNotNeedQuoteName(boolean notNeedQuoteName) {
		this.notNeedQuoteName = notNeedQuoteName;
		return this;
	}

	public boolean isIgnoreNull() {
		return ignoreNull;
	}

	public JsonFormat setIgnoreNull(boolean ignoreNull) {
		this.ignoreNull = ignoreNull;
		return this;
	}

	public String getActivedFields() {
		return activedFields;
	}

	public JsonFormat setActivedFields(String activedFields) {
		this.activedFields = activedFields;
		return this;
	}

	public String getIgnoreFields() {
		return ignoreFields;
	}

	public JsonFormat setIgnoreFields(String ignoreFields) {
		this.ignoreFields = ignoreFields;
		return this;
	}
}