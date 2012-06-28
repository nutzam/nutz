package org.nutz.dao.impl.sql.pojo;

import org.nutz.dao.Chain;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.MappingField;
import org.nutz.dao.jdbc.Jdbcs;
import org.nutz.dao.jdbc.ValueAdaptor;
import org.nutz.lang.Lang;

public class UpdateFieldsByChainPItem extends AbstractPItem {

    private Chain chain;

    public UpdateFieldsByChainPItem(Chain chain) {
        this.chain = chain;
    }

    public void joinSql(Entity<?> en, StringBuilder sb) {
        if (chain.size() > 0) {
            sb.append(" SET ");
            Chain c = chain.head();
            while (c != null) {
                sb.append(this._fmtcolnm(en, c.name()));
                sb.append("=? ,");
                c = c.next();
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(' ');
        } else {
            throw Lang.makeThrow("Entity chain for UPDATE '%s'", en.getType().getName());
        }
    }

    public int joinAdaptor(Entity<?> en, ValueAdaptor[] adaptors, int off) {
        Chain c = chain.head();
        while (c != null) {
            MappingField mf = en.getField(c.name());
            // TODO 移除这种数组下标用++的写法!!!
            if (c.adaptor() == null)
                adaptors[off++] = (null == mf ? Jdbcs.getAdaptorBy(c.value()) : mf.getAdaptor());
            else
                adaptors[off++] = c.adaptor();
            c = c.next();
        }
        return off;
    }

    public int joinParams(Entity<?> en, Object obj, Object[] params, int off) {
        Chain c = chain.head();
        while (c != null) {
            params[off++] = c.value();
            c = c.next();
        }
        return off;
    }

    public int paramCount(Entity<?> en) {
        return chain.size();
    }

}
