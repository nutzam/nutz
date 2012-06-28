package org.nutz.dao.util.cri;

import org.nutz.dao.entity.Entity;
import org.nutz.dao.impl.sql.pojo.NoParamsPItem;

public class OrderByItem extends NoParamsPItem {

    private String name;

    private String by;

    public OrderByItem(String name, String by) {
        this.name = name;
        this.by = by;
    }

    public void joinSql(Entity<?> en, StringBuilder sb) {
        sb.append(_fmtcolnm(en, name)).append(' ').append(by);
    }
}
