package org.nutz.dao.util.cri;

import org.nutz.dao.entity.Entity;
import org.nutz.dao.impl.sql.pojo.NoParamsPItem;
import org.nutz.dao.util.lambda.LambdaQuery;
import org.nutz.dao.util.lambda.PFun;

public class OrderByItem extends NoParamsPItem {

    private static final long serialVersionUID = 1L;

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
