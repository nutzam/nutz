package org.nutz.dao.util.cri;

import java.util.ArrayList;
import java.util.List;

import org.nutz.dao.entity.Entity;
import org.nutz.dao.impl.sql.pojo.NoParamsPItem;
import org.nutz.dao.sql.OrderBy;
import org.nutz.dao.sql.Pojo;

public class OrderBySet extends NoParamsPItem implements OrderBy {

    private List<OrderByItem> list;

    OrderBySet() {
        list = new ArrayList<OrderByItem>(3);
    }

    public void joinSql(Entity<?> en, StringBuilder sb) {
        if (!list.isEmpty()) {
            sb.append(" ORDER BY ");
            for (OrderByItem obi : list) {
                obi.joinSql(en, sb);
                sb.append(", ");
            }
            sb.setCharAt(sb.length() - 2, ' ');
        } else
            ;// OK,无需添加.
    }

    public String toSql(Entity<?> en) {
        StringBuilder sb = new StringBuilder();
        joinSql(en, sb);
        return sb.toString();
    }

    public OrderBy asc(String name) {
        OrderByItem asc = new OrderByItem(name, "ASC");
        asc.setPojo(pojo);
        list.add(asc);
        return this;
    }

    public OrderBy desc(String name) {
        OrderByItem desc = new OrderByItem(name, "DESC");
        desc.setPojo(pojo);
        list.add(desc);
        return this;
    }

    @Override
    public void setPojo(Pojo pojo) {
        super.setPojo(pojo);
        for (OrderByItem obi : list)
            obi.setPojo(pojo);
    }

    public List<OrderByItem> getItems() {
        return list;
    }
}
