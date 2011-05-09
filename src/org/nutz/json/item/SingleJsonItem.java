package org.nutz.json.item;


import java.lang.reflect.Type;

import org.nutz.json.JsonException;
import org.nutz.json.JsonItem;
import org.nutz.lang.Mirror;

/**
 * 没有引号包裹的项
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class SingleJsonItem extends JsonItem{
	protected String value;

	public Object parse(Type type) {
		if(value.equals("null") || value.equals("undefined")){
			return null;
		}
		Mirror<?> me = fetchMirror(type);
		if(me != null){
			if(me.isBoolean()){
				if("true".equals(value)){
					return true;
				}
				if("false".equals(value)){
					return false;
				}
				throw new JsonException(0,0,'0',"Expect boolean as input!");
			}
			if(me.isInt()){
				return Integer.valueOf(value);
			}
			if(me.isLong()){
				return Long.valueOf(value);
			}
			if(me.isFloat()){
				return Float.valueOf(value);
			}
			if(me.isDouble()){
				return Double.valueOf(value);
			}
			if(me.isByte()){
				return Byte.valueOf(value);
			}
		}
		if("".equals(value)){
			return null;
		}
		if("true".equals(value)){
			return true;
		}
		if("false".equals(value)){
			return false;
		}
		// guess the return type
		try{
			char lastChar = Character.toUpperCase(value.charAt(value.length() - 1));
			if (value.indexOf('.') >= 0) {
				if (lastChar == 'F')
					return Float.valueOf(value.substring(0, value.length() - 1));
				else
					return Double.valueOf(value);
			} else {
				if (lastChar == 'L')
					return Long.valueOf(value.substring(0, value.length() - 1));
				else{
					Long lv = Long.valueOf(value);
					if (Integer.MIN_VALUE < lv && lv < Integer.MAX_VALUE)
						return Integer.valueOf(lv.intValue());
					else
						return lv;
					}
			}
		}catch(Exception e){
			throw new RuntimeException("SingleJsonItem 类型转换错误",e);
		}
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public String toString() {
		return value;
	}

}
