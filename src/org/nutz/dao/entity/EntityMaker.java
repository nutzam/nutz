package org.nutz.dao.entity;

import org.nutz.dao.DatabaseMeta;

public interface EntityMaker {

	Entity<?> make(DatabaseMeta db, Class<?> type);

}
