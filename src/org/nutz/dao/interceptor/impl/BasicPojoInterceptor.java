package org.nutz.dao.interceptor.impl;

import org.nutz.dao.entity.Entity;
import org.nutz.dao.interceptor.PojoInterceptor;
import org.nutz.dao.jdbc.JdbcExpert;

public class BasicPojoInterceptor implements PojoInterceptor {

    public void onEvent(Object obj, Entity<?> en, String event, Object... args) {
    }

    public void setupEntity(Entity<?> en, JdbcExpert expert) {
    }

    public boolean isAvailable() {
        return true;
    }
}
