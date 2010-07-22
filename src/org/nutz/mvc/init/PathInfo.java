package org.nutz.mvc.init;

public class PathInfo<T> {

	PathInfo(int i, String path, T obj) {
		this.path = path;
		this.cursor = i;
		// 全匹配
		if (i == -1) {
			this.known = path;
			this.remain = null;
		}
		// 没有匹配
		else if (i == 0) {
			this.known = null;
			this.remain = path;
		}
		// 匹配到 *
		else {
			this.known = path.substring(0, i);
			this.remain = path.substring(i);
		}
		this.obj = obj;
	}

	private int cursor;

	private String path;
	
	private String known;
	
	private String remain;
	
	private T obj;

	/**
	 * @return 匹配的步长
	 *         <ul>
	 *         <li>-1 - 表示这个路径全部被匹配了
	 *         <li>0 - 表示这个路径没有被匹配
	 *         <li>>0 - 表示这个路径被一个 * 匹配
	 *         </ul>
	 */
	public int getCursor() {
		return cursor;
	}

	/**
	 * @return 请求的全路径 （去掉后缀）
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @return 路径中已匹配的部分
	 */
	public String getKnown() {
		return known;
	}

	/**
	 * @return 路径中未匹配的部分
	 */
	public String getRemain() {
		return remain;
	}

	public T getObj() {
		return obj;
	}

}
