package org.nutz.dao.impl.sql.pojo;

import org.nutz.dao.entity.Entity;

public class StaticPItem extends NoParamsPItem {

    private String str;

    public StaticPItem(String str) {
        this.str = str;
    }

    public void joinSql(Entity<?> en, StringBuilder sb) {
        sb.append(str).append(' ');
    }

}
