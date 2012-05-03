package org.nutz.aop.v2.java.cp;

public class CONSTANT_Base {

	public final static CONSTANT_Base emtry = new CONSTANT_Base(){};
	
	public byte[] toBytes() {
		return new byte[0];
	}
	
	public byte flag;
	public int index;
}
