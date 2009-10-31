package org.nutz.dao;

import org.nutz.dao.entity.Entity;

public interface Condition {

	String toSql(Entity<?> entity);
}
