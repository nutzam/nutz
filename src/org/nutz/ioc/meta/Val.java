package org.nutz.ioc.meta;

import java.util.regex.Pattern;

import org.nutz.dao.entity.annotation.*;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.json.ToJson;

@Table("nut_value")
@ToJson
public class Val {

	public static final Pattern SPECIAL = Pattern
			.compile("^(refer|jsp|file|disk|java|env|redirect|server|config)$");

	public static final Pattern INT = Pattern.compile("^[0-9]$");
	public static final Pattern FLOAT = Pattern.compile("^([0-9.]){2,}$");

	public static Val normal(String value) {
		return make(normal, value);
	}

	public static Val make(String type, Object value) {
		Val v = new Val();
		v.setType(type);
		v.setValue(value);
		return v;
	}

	public static final String normal = "normal";
	public static final String inner = "inner";
	public static final String Null = "null";
	public static final String bool = "bool";
	public static final String map = "map";
	public static final String array = "array";
	public static final String refer = "refer";
	public static final String file = "file";
	public static final String disk = "disk";
	public static final String java = "java";
	public static final String env = "env";

	@Column
	@Id
	private int id;

	@Column
	private String type;

	@Column
	private Object value;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public boolean isNormal() {
		return null == type || normal.equals(type);
	}

	public boolean isInner() {
		return inner.equals(type);
	}

	public boolean isNull() {
		return null == value || Null.equals(type);
	}

	public boolean isBoolean() {
		return bool.equals(type);
	}

	public boolean isMap() {
		return map.equals(type);
	}

	public boolean isArray() {
		return array.equals(type);
	}

	public boolean isRefer() {
		return refer.equals(type);
	}

	public boolean isFile() {
		return file.equals(type);
	}

	public boolean isDisk() {
		return disk.equals(type);
	}

	public boolean isJava() {
		return java.equals(type);
	}

	public boolean isEnv() {
		return env.equals(type);
	}

	public boolean isSpecial() {
		return SPECIAL.matcher(type).find();
	}

	public String toJson(JsonFormat format) {
		return Json.toJson(Obj2Map.renderVal(this), format);
	}

}
