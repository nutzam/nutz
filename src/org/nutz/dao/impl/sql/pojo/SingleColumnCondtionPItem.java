package org.nutz.dao.impl.sql.pojo;

import org.nutz.castor.Castors;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.MappingField;
import org.nutz.dao.jdbc.ValueAdaptor;
import org.nutz.lang.Lang;

public class SingleColumnCondtionPItem extends AbstractPItem {

    private ValueAdaptor va;

    private Object def;

    private String colName;

    private Class<?> colType;

    private MappingField mf;

    private boolean casesensitive;

    public SingleColumnCondtionPItem(MappingField mf, Object def) {
        this.mf = mf;
        this.va = mf.getAdaptor();
        this.colName = mf.getColumnName();
        this.colType = mf.getTypeClass();
        this.def = def;
    }

    public SingleColumnCondtionPItem(String colName, Class<?> colType, ValueAdaptor va, Object def) {
        this.colName = colName;
        this.colType = colType;
        this.va = va;
        this.def = def;
    }

    public int joinParams(Entity<?> en, Object obj, Object[] params, int off) {
        // 默认值可以直接使用
        if (def == obj && null != obj) {
            params[off++] = def;
        }
        // 进行更精细的判断...
        else {
            en = _en(en);
            // 是个实体对象，试图直接取值
            if (null != obj && null != mf && mf.getEntity() == en && en.getType().isInstance(obj))
                params[off++] = mf.getValue(obj);
            // 采用默认值
            else if (null != def)
                params[off++] = def;
            // 试图转换传入的对象
            else if (null != obj) {
                // TODO 这是啥规则?!!! 完全搞不懂!!!
                params[off++] = Castors.me().castTo(obj, colType);
            }
            // 逼急了，老子抛异常了!
            else
                throw Lang.impossible();
        }
        return off;
    }

    public void joinSql(Entity<?> en, StringBuilder sb) {
        if (null != mf && !casesensitive)
            switch (mf.getColumnType()) {
            case CHAR:
            case VARCHAR:
            case TEXT:
                sb.append(" WHERE LOWER(").append(colName).append(")=LOWER(?)");
                return;
            default :
                break;
            }

        sb.append(" WHERE ").append(colName).append("=?");
    }

    public int joinAdaptor(Entity<?> en, ValueAdaptor[] adaptors, int off) {
        adaptors[off++] = va;
        return off;
    }

    public int paramCount(Entity<?> en) {
        return 1;
    }

    public SingleColumnCondtionPItem setCasesensitive(boolean casesensitive) {
        this.casesensitive = casesensitive;
        return this;
    }

}
