package com.zzh.dao;

import com.zzh.dao.entity.Entity;

public interface Expression {

	void render(StringBuilder sb, Entity<?> en);

}
