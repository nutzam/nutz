package org.nutz.json.item;

import java.lang.reflect.Type;

import org.nutz.json.JsonItem;

/**
 * JSON结点
 * 主要记录'{:}'包裹分割的键值对结点
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class PairJsonItem extends JsonItem{
	private JsonItem key;
	private JsonItem value;
	/**
	 * 解析
	 */
	public Object parse(Type type) {
		return null;
	}

	public JsonItem getKey() {
		return key;
	}
	public void setKey(JsonItem key) {
		this.key = key;
	}
	public JsonItem getValue() {
		return value;
	}
	public void setValue(JsonItem value) {
		this.value = value;
	}
	
	public String toString() {
		return key.toString() + " : " + value.toString();
	}
	
}
