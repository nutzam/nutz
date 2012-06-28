package org.nutz.dao.impl.sql.pojo;

import java.util.Iterator;
import java.util.List;

import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.MappingField;
import org.nutz.dao.jdbc.ValueAdaptor;
import org.nutz.dao.util.Pojos;
import org.nutz.lang.Lang;

public class InsertValuesPItem extends AbstractPItem {

    /**
     * 缓存要操作的字段
     */
    protected List<MappingField> mfs;

    protected List<MappingField> _mfs(Entity<?> en) {
        if (null == mfs)
            return Pojos.getFieldsForInsert(_en(en), getFieldMatcher());
        return mfs;
    }

    public void joinSql(Entity<?> en, StringBuilder sb) {
        List<MappingField> mfs = _mfs(en);
        if (mfs.isEmpty())
            throw Lang.makeThrow("No fields be insert nearby \"%s\"", sb);

        Iterator<MappingField> it = mfs.iterator();
        it.next();
        sb.append("VALUES(?");
        while (it.hasNext()) {
            sb.append(",?");
            it.next();
        }
        sb.append(") ");
    }

    public int joinAdaptor(Entity<?> en, ValueAdaptor[] adaptors, int off) {
        List<MappingField> mfs = _mfs(en);
        for (MappingField mf : mfs)
            adaptors[off++] = mf.getAdaptor();
        return off;
    }

    public int joinParams(Entity<?> en, Object obj, Object[] params, int off) {
        List<MappingField> mfs = _mfs(en);
        for (MappingField mf : mfs) {
            Object v = mf.getValue(obj);
            params[off++] = null == v ? mf.getDefaultValue(obj) : v;
        }
        return off;
    }

    public int paramCount(Entity<?> en) {
        return _mfs(en).size();
    }

}
