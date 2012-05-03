package org.nutz.aop.v2.java;

import java.util.ArrayList;
import java.util.List;

import org.nutz.aop.v2.java.cp.CONSTANT_Utf8;

public class JavaMethod {

	public int access_flag;
	public CONSTANT_Utf8 name;
	public CONSTANT_Utf8 descriptor;
	public List<JavaAttribute> attributes = new ArrayList<JavaAttribute>();
}
