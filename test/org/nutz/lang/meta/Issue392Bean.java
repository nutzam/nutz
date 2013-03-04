package org.nutz.lang.meta;

public class Issue392Bean {

	private int len;
	
	//基本数据类型的变参构造方法
	public Issue392Bean(byte...abc) {
		if (abc != null)
			len = abc.length;
	}
	
	public int getLen() {
		return len;
	}
}
