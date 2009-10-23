package org.nutz.dao;

import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.EntityField;

public class Chain {

	public static Chain make(String name, Object value) {
		Chain head = new Chain();
		head.name = name;
		head.value = value;
		return head;
	}

	private Chain() {
		head = this;
	}

	private Chain head;

	private String name;

	private Object value;

	private Chain next;

	public Chain name(String name) {
		this.name = name;
		return this;
	}

	public Chain value(Object value) {
		this.value = value;
		return this;
	}

	public Chain add(String name, Object value) {
		Chain oldNext = next;
		next = new Chain();
		next.name = name;
		next.value = value;
		next.head = head;
		next.next = oldNext;
		return next;
	}

	public String name() {
		return name;
	}

	public Object value() {
		return value;
	}

	public Chain next() {
		return next;
	}

	public Chain head() {
		return head;
	}

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
