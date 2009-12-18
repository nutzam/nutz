package org.nutz.dao;

import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.EntityField;

/**
 * 名值链。
 * <p>
 * 通过 add 方法，建立一条名值对的链表
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author Wendal(wendal1985@gmail.com)
 */
public class Chain {

	/**
	 * 建立一条名值链开始的一环
	 * 
	 * @param name
	 *            名称
	 * @param value
	 *            值
	 * @return 链头
	 */
	public static Chain make(String name, Object value) {
		return new Chain(name,value,null,null);
	}

	private Chain(String name,Object value,Chain head,Chain next) {
		this.name = name;
		this.value = value;
		if(head == null)
			this.head = this;
		else
			this.head = head;
		this.next = next;
	}

	private Chain head;

	private String name;

	private Object value;

	private Chain next;

	/**
	 * 改变当前节点的名称
	 * 
	 * @param name
	 *            新名称
	 * @return 当前节点
	 */
	public Chain name(String name) {
		this.name = name;
		return this;
	}

	/**
	 * 改变当前节点的值
	 * 
	 * @param value
	 *            新值
	 * @return 当前节点
	 */
	public Chain value(Object value) {
		this.value = value;
		return this;
	}

	/**
	 * 将一个名值对，添加为本链节点的下一环
	 * 
	 * @param name
	 *            名
	 * @param value
	 *            值
	 * @return 新增加的节点
	 */
	public Chain add(String name, Object value) {
		Chain oldNext = next;
		next = new Chain(name,value,this.head,oldNext);
		return next;
	}

	/**
	 * @return 当前节点的名称
	 */
	public String name() {
		return name;
	}

	/**
	 * @return 当前节点的值
	 */
	public Object value() {
		return value;
	}

	/**
	 * @return 下一个节点
	 */
	public Chain next() {
		return next;
	}

	/**
	 * @return 整个链的第一环（头节点）
	 */
	public Chain head() {
		return head;
	}

	/**
	 * 根据 Entity 里的设定，更新整个链所有节点的名称。
	 * <p>
	 * 如果节点的名称是 Entity 的一个字段，则采用数据库字段的名称
	 * 
	 * @param entity
	 *            实体
	 * @return 链头节点
	 */
	public Chain updateBy(Entity<?> entity) {
		Chain c = head;
		while (c != null) {
			EntityField ef = entity.getField(c.name);
			if (null != ef) {
				c.name(ef.getColumnName());
			}
			c = c.next;
		}
		return head;
	}

}
