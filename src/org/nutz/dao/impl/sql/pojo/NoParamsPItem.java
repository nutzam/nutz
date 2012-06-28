package org.nutz.dao.impl.sql.pojo;

import org.nutz.dao.entity.Entity;
import org.nutz.dao.jdbc.ValueAdaptor;

public abstract class NoParamsPItem extends AbstractPItem {

    private static final String[] re = new String[0];

    public String[] getParamNames() {
        return re;
    }

    public int joinAdaptor(Entity<?> en, ValueAdaptor[] adaptors, int off) {
        return off;
    }

    public int joinParams(Entity<?> en, Object obj, Object[] params, int off) {
        return off;
    }

    public int paramCount(Entity<?> en) {
        return 0;
    }

}
