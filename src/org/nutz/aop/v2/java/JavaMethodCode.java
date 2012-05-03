package org.nutz.aop.v2.java;

import java.util.ArrayList;

import org.nutz.repo.org.objectweb.asm.Opcodes;
import org.nutz.repo.org.objectweb.asm.Type;

public class JavaMethodCode extends JavaAttribute {

	public CP cp;
	
	public ArrayList<byte[]> items = new ArrayList<byte[]>();
	
	/**
	 * 载入方法参数
	 * @param beginIndex 静态方法为0,其他方法为1
	 * @param args 需要载入的参数的类型
	 */
	public void loadArgs(int beginIndex, Class<?>[] args) {
		for (int i = 0; i < args.length; i++)
			loadArg(i+beginIndex, args[i]);
	}
	
	public void loadArg(int index, Class<?> arg) {
		Type type = Type.getType(arg);
		int opCode = type.getOpcode(Opcodes.ILOAD);
	}
}
