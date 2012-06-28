package org.nutz.dao.impl.sql.pojo;

import org.nutz.dao.Chain;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.jdbc.ValueAdaptor;
import org.nutz.lang.Lang;

public class InsertByChainPItem extends AbstractPItem {

    private String[] names;
    private Object[] values;

    public InsertByChainPItem(Chain chain) {
        names = new String[chain.size()];
        values = new Object[chain.size()];

        // 将值链所有内容 copy 到两个数组中去，以便之后遍历
        Chain c = chain.head();
        int i = 0;
        while (null != c) {
            names[i] = c.name();
            values[i] = c.value();
            i++;
            c = c.next();
        }
        if (i == 0)
            throw Lang.makeThrow("Insert empty chain!");
    }

    public void joinSql(Entity<?> en, StringBuilder sb) {
        // 字段名部分
        sb.append(" (").append(_colname(en, 0));
        for (int i = 1; i < names.length; i++) {
            sb.append(',').append(_colname(en, i));
        }
        sb.append(") VALUES(?");
        // 占位符部分
        for (int i = 1; i < names.length; i++) {
            sb.append(",?");
        }
        sb.append(')');
    }

    public int joinAdaptor(Entity<?> en, ValueAdaptor[] adaptors, int off) {
        for (int i = 0; i < names.length; i++)
            adaptors[off++] = en.getColumn(_colname(en, i)).getAdaptor();
        return off;
    }

    public int joinParams(Entity<?> en, Object obj, Object[] params, int off) {
        for (int i = 0; i < values.length; i++)
            params[off++] = values[i];
        return off;
    }

    public int paramCount(Entity<?> en) {
        return values.length;
    }

    private String _colname(Entity<?> en, int index) {
        return en.getField(names[index]).getColumnName();
    }
}
