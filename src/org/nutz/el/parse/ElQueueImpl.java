package org.nutz.el.parse;

import java.util.ArrayList;
import java.util.List;

import org.nutz.el.ElException;

/**
 * El队列实现
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class ElQueueImpl<T> implements ElQueue<T>{
	private List<T> list = new ArrayList<T>();
	private int index = 0;

	public void add(T item) {
		list.add(item);
	}

	public T peek() {
		return peek(0);
	}

	public T peek(int offet) {
		checking(index + offet);
		return list.get(index + offet);
	}

	public T poll() {
		checking(index);
		return list.get(index++);
	}
	
	/**
	 * 验证是否越界
	 * @param index
	 */
	private void checking(int index){
		if(isEmpty()){
			throw new ElException("已读取到最后一个元素了,不能继续读取!");
		}
	}

	public boolean isEmpty() {
		return index >= list.size();
	}

}
