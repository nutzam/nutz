package org.nutz.dao.interceptor;

import org.nutz.dao.entity.Entity;
import org.nutz.dao.jdbc.JdbcExpert;

public interface PojoInterceptor {

    /**
     * 拦截并返回对象, 如无改变, 返回原对象就行
     */
    void onEvent(Object obj, Entity<?> en, String event, Object... args);

    void setupEntity(Entity<?> en, JdbcExpert expert);

    /**
         * 当前拦截器是否可用,用于避免多余的调用
     */
    boolean isAvailable();
}
