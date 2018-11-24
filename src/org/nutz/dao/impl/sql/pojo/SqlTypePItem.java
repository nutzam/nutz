package org.nutz.dao.impl.sql.pojo;

import org.nutz.dao.entity.Entity;

public class SqlTypePItem extends NoParamsPItem {

    private static final long serialVersionUID = 1L;

    public void joinSql(Entity<?> en, StringBuilder sb) {
        switch (getSqlType()) {
        case INSERT:
            sb.append("INSERT INTO ");
            break;
        case TRUNCATE:
            sb.append("TRUNCATE TABLE ");
            break;
        default:
            sb.append(getSqlType().name().toUpperCase()).append(' ');
        }
    }

}
