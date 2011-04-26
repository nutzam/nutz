package org.nutz.json.item;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.TreeMap;

import org.nutz.json.JsonItem;
import org.nutz.lang.Mirror;

public class ObjectJsonItem extends ArrayJsonItem{
	
	@SuppressWarnings("unchecked")
	public Object parse(Type type) {
		Mirror<?> me = fetchMirror(type);
		if(null != me && Map.class == me.getType()){
			me = Mirror.me(TreeMap.class);
		}
		if(null == me || Map.class == me.getType() || Map.class.isAssignableFrom(me.getType())){
			Map<String, Object> map = null == me ? new TreeMap<String, Object>()
												: (Map<String, Object>) me.born();
			for(JsonItem ji : value){
				if(!(ji instanceof PairJsonItem)){
					continue;
				}
				PairJsonItem pji = (PairJsonItem) ji;
				map.put(pji.getKey().toString(), pji.getValue().parse(null));
			}
			return map;
		}
		Object obj = me.born();
		for(JsonItem ji : value){
			PairJsonItem pji = (PairJsonItem) ji;
			Field f = null;
			Type ft = null;
			try{
				f = me.getField(pji.getKey().toString());
				ft = f.getGenericType();
			} catch (NoSuchFieldException e){}
			if(null != f){
				me.setValue(obj, f, pji.getValue().parse(ft));
			}
		}
		return obj;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		String temp = "" ;
		for(JsonItem ji : getValue()){
			sb.append(temp);
			sb.append(ji.toString());
			temp = ", ";
		}
		sb.append("}");
		return sb.toString();
	}
}
