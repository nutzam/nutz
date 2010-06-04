package org.nutz.mvc.upload.util;

/**
 * 根据给给定的 bytes[] 计算所有的查找回溯点
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class RemountBytes {

	public static RemountBytes create(byte[] bs) {
		int[] ps = new int[bs.length];
		for (int i = 2; i < bs.length; i++) {
			// 在之前寻找相同字符
		}
		RemountBytes re = new RemountBytes();
		re.bytes = bs;
		re.pos = ps;
		return re;
	}

	public byte[] bytes;

	public int[] pos;

}
