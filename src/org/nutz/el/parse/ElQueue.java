package org.nutz.el.parse;

/**
 * EL字符队列
 * @author juqkai(juqkai@gmail.com)
 *
 */
public interface ElQueue<T> {
	/**
	 * 添加
	 * @param item
	 */
	public void add(T item);
	/**
	 * 读取顶部元素,指针不做移动
	 * @return
	 */
	public T peek();
	/**
	 * 读取离顶部元素偏移量的元素,指针不做移动
	 * @return
	 */
	public T peek(int offet);
	/**
	 * 读取顶部元素,并移动指针
	 * @return
	 */
	public T poll();
	/**
	 * 是否读取完
	 * @return
	 */
	public boolean isEnd();
	
}
