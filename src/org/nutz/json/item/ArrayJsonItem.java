package org.nutz.json.item;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.nutz.castor.Castors;
import org.nutz.json.JsonItem;
import org.nutz.lang.Mirror;

public class ArrayJsonItem extends JsonItem{

	List<JsonItem> value = new ArrayList<JsonItem>();
	/**
	 * 解析
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object parse(Type type) {
		Type tt = null;
		Mirror<?> me = fetchMirror(type);
		boolean returnAsList = true;
		List list = null;
		if(null == me){
			list = new LinkedList();
		} else if(me.getType().isArray()){
			list = new LinkedList();
			returnAsList = false;
			tt = me.getType().getComponentType();
		} else if (List.class.isAssignableFrom(me.getType())){
			returnAsList = true;
			if(me.is(List.class)){
				list = new LinkedList();
			} else {
				list = (List) me.born();
			}
			ParameterizedType pt = fetchParameterizedType(type);
			if(type != null && pt!= null && pt.getActualTypeArguments() != null){
				tt = pt.getActualTypeArguments()[0];
			}
		} else {
			throw new RuntimeException("");
		}
		for(JsonItem ji : value){
			list.add(ji.parse(tt));
		}
		//返回list
		if(returnAsList){
			return list;
		}
		//返回数组
		Object ary = Array.newInstance((Class<?>)tt, list.size());
		int i = 0;
		for (Iterator it = list.iterator(); it.hasNext();)
			Array.set(ary, i++, Castors.me().castTo(it.next(), (Class<?>) tt));
		return ary;
	}
	
	public void addItem(JsonItem item){
		value.add(item);
	}

	public List<JsonItem> getValue() {
		return value;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append('[');
		String temp = "";
		for(JsonItem ji : value){
			sb.append(temp);
			sb.append(ji.toString());
			temp = ",";
		}
		sb.append(']');
		return sb.toString();
	}
}
