package org.nutz.dao.impl.sql.pojo;

import org.nutz.dao.entity.Entity;

public class StaticPItem extends NoParamsPItem {

    private static final long serialVersionUID = 1L;

    private String str;
    private boolean tidy;

    public StaticPItem(String str) {
        this.str = str;
    }
    
    public StaticPItem(String str, boolean tidy) {
        this.str = str;
        this.tidy = tidy;
    }

    @Override
    public void joinSql(Entity<?> en, StringBuilder sb) {
        sb.append(str);
        if (!tidy) {
            sb.append(' ');
        }
    }

    @Override
    public String toString() {
        return str;
    }
}
