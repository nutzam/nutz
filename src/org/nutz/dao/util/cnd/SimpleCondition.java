package org.nutz.dao.util.cnd;

import org.nutz.dao.Condition;
import org.nutz.dao.entity.Entity;

/**
 * 简单的包裹一下 SQL 字符串
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class SimpleCondition implements Condition {

    private String content;

    public SimpleCondition(Object obj) {
        this.content = obj.toString();
    }

    public SimpleCondition(String format, Object... args) {
        this.content = String.format(format, args);
    }

    public String toSql(Entity<?> entity) {
        return content;
    }

}
