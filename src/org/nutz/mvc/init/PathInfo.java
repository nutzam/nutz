package org.nutz.mvc.init;

public class PathInfo<T> {

	PathInfo(int i, String remain, T obj) {
		this.cursor = i;
		this.remain = remain;
		this.obj = obj;
	}

	private int cursor;
	private String remain;
	private T obj;

	public int getCursor() {
		return cursor;
	}

	public String getRemain() {
		return remain;
	}


	public T getObj() {
		return obj;
	}

}
