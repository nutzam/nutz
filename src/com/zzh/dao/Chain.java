package com.zzh.dao;

import com.zzh.dao.entity.Entity;
import com.zzh.dao.entity.EntityField;

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
		next = new Chain();
		next.name = name;
		next.value = value;
		next.head = head;
		return next;
	}

	String toString(Entity<?> entity) {
		StringBuilder sb = new StringBuilder();
		Chain c = this.head;
		while (c != null) {
			if (null != entity) {
				EntityField ef = entity.getField(name);
				sb.append(null == ef ? name : ef.getColumnName());
			} else {
				sb.append(name);
			}
			sb.append('=');
			sb.append(Sqls.formatFieldValue(c.value));
			c = c.next;
			if (null != c)
				sb.append(',');
		}
		return sb.toString();
	}
}
