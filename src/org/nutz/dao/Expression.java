package org.nutz.dao;

import org.nutz.dao.entity.Entity;

public interface Expression {

	void render(StringBuilder sb, Entity<?> en);

}
