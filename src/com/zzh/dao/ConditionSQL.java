package com.zzh.dao;

import com.zzh.dao.Condition;
import com.zzh.dao.entity.Entity;
import com.zzh.lang.Strings;

/**
 * Preserver plug name "condition"
 * 
 * @author zozoh
 * 
 */
public abstract class ConditionSQL<T> extends AbstractSQL<T> {

	private T result;

	public T getResult() {
		return result;
	}

	public void setResult(T object) {
		this.result = object;
	}

	public <E> ConditionSQL<T> setCondition(Condition condition, Entity<E> entity,
			Class<E> classOfEntity) {
		if (null != condition) {
			String cond = Strings.trim(condition.toString(entity));
			if (Strings.isEmpty(cond))
				return this;
			if (!cond.toUpperCase().startsWith("WHERE")) {
				cond = "WHERE " + cond;
			}
			this.set("condition", " " + cond);
		}
		return this;
	}
}
