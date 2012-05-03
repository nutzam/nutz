package org.nutz.aop.v2.java;

import java.util.ArrayList;
import java.util.List;

import org.nutz.aop.v2.java.cp.CONSTANT_Class;

public class JavaClass extends JavaByteObject {

	public static byte[] magic = "CAFE".getBytes();
	public int minor_version = 0;
	public int major_version = 49;
	public CP cp = new CP();
	public int access_flag;
	public CONSTANT_Class thisClass;
	public CONSTANT_Class superClass;
	public List<CONSTANT_Class> interfaces = new ArrayList<CONSTANT_Class>();
	public List<JavaField> fields = new ArrayList<JavaField>();
	public List<JavaMethod> methods = new ArrayList<JavaMethod>();
	public List<JavaAttribute> attributes = new ArrayList<JavaAttribute>();
}
