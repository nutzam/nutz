package org.nutz.aop.v2.java;

import java.util.ArrayList;
import java.util.List;

import org.nutz.aop.v2.java.cp.CONSTANT_Base;
import org.nutz.aop.v2.java.cp.CONSTANT_Class;
import org.nutz.aop.v2.java.cp.CONSTANT_Fieldref;
import org.nutz.aop.v2.java.cp.CONSTANT_Methodref;
import org.nutz.aop.v2.java.cp.CONSTANT_NameAndType;
import org.nutz.aop.v2.java.cp.CONSTANT_String;
import org.nutz.aop.v2.java.cp.CONSTANT_Utf8;

public class CP {

	public List<CONSTANT_Base> items = new ArrayList<CONSTANT_Base>();
	
	public CP() {
		items.add(CONSTANT_Base.emtry);
	}

	/**添加一个Class常量*/
	public CONSTANT_Class c_class(String name){
		CONSTANT_Utf8 c_utf8 = c_utf8(name);
		for (CONSTANT_Base item : items) {
			if (item instanceof CONSTANT_Class && ((CONSTANT_Class)item).name == c_utf8)
				return (CONSTANT_Class)item;
		}
		CONSTANT_Class c_class = new CONSTANT_Class();
		c_class.name = c_utf8;
		add(c_class);
		return c_class;
	}
	/**添加一个字段常量*/
	public CONSTANT_Fieldref c_field(String klass, String name, String descriptor){
		CONSTANT_Class c_class = c_class(klass);
		CONSTANT_NameAndType c_nameAndType = c_nameAndType(name, descriptor);
		for (CONSTANT_Base item : items) {
			if (item instanceof CONSTANT_Fieldref) {
				CONSTANT_Fieldref c_field = (CONSTANT_Fieldref)item;
				if (c_field.klass == c_class && c_field.nameAndType == c_nameAndType)
					return c_field;
			}
		}
		CONSTANT_Fieldref c_field = new CONSTANT_Fieldref();
		c_field.klass = c_class;
		c_field.nameAndType = c_nameAndType;
		add(c_field);
		return c_field;
	}
	/**添加一个方法常量*/
	public CONSTANT_Methodref c_method(String klass, String name, String descriptor){
		CONSTANT_Class c_class = c_class(klass);
		CONSTANT_NameAndType c_nameAndType = c_nameAndType(name, descriptor);
		for (CONSTANT_Base item : items) {
			if (item instanceof CONSTANT_Methodref) {
				CONSTANT_Methodref c_method = (CONSTANT_Methodref)item;
				if (c_method.klass == c_class && c_method.nameAndType == c_nameAndType)
					return c_method;
			}
		}
		CONSTANT_Methodref c_method = new CONSTANT_Methodref();
		c_method.klass = c_class;
		c_method.nameAndType = c_nameAndType;
		add(c_method);
		return c_method;
	}
	/**添加一个String常量,注意与UTF8常量的区别*/
	public CONSTANT_String c_string(Object value){
		throw new RuntimeException();//暂时用不上
	}
	/**添加一个UTF8字符串常量*/
	public CONSTANT_Utf8 c_utf8(String value){
		for (CONSTANT_Base item : items) {
			if (item instanceof CONSTANT_Utf8 && ((CONSTANT_Utf8)item).value.equals(value))
				return (CONSTANT_Utf8)item;
		}
		CONSTANT_Utf8 c_utf8 = new CONSTANT_Utf8();
		c_utf8.value = value;
		add(c_utf8);
		return c_utf8;
	}
	public CONSTANT_NameAndType c_nameAndType(String name, String descriptor){
		CONSTANT_Utf8 name_utf8 = c_utf8(name);
		CONSTANT_Utf8 descriptor_utf8 = c_utf8(descriptor);
		for (CONSTANT_Base item : items) {
			if (item instanceof CONSTANT_NameAndType) {
				CONSTANT_NameAndType c_nameAndType = (CONSTANT_NameAndType)item;
				if (name_utf8 == c_nameAndType.name && descriptor_utf8 == c_nameAndType.descriptor)
					return c_nameAndType;
			}
		}
		CONSTANT_NameAndType c_nameAndType = new CONSTANT_NameAndType();
		c_nameAndType.name = name_utf8;
		c_nameAndType.descriptor = descriptor_utf8;
		add(c_nameAndType);
		return c_nameAndType;
	}
	
	public void add(CONSTANT_Base cons) {
		if (cons != CONSTANT_Base.emtry)
			cons.index = items.size();
		items.add(cons);
	}
}
