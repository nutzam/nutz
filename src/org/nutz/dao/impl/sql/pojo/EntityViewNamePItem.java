package org.nutz.dao.impl.sql.pojo;

import org.nutz.dao.entity.Entity;

public class EntityViewNamePItem extends NoParamsPItem {

    private static final long serialVersionUID = 1L;

    public void joinSql(Entity<?> en, StringBuilder sb) {
        sb.append(_en(en).getViewName()).append(' ');
    }

}
