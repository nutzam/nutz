package org.nutz.dao.impl.sql.pojo;

import java.util.List;

import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.MappingField;
import org.nutz.dao.util.Pojos;

public class UpdateFieldsPItem extends InsertValuesPItem {

    /**
     * 参考对象，根据这个对象来决定是否忽略空值
     */
    private Object refer;

    public UpdateFieldsPItem(Object refer) {
        this.refer = refer;
    }

    protected List<MappingField> _mfs(Entity<?> en) {
        if (null == mfs)
            return Pojos.getFieldsForUpdate(_en(en), getFieldMatcher(), refer);
        return mfs;
    }

    public void joinSql(Entity<?> en, StringBuilder sb) {
        List<MappingField> mfs = _mfs(en);

        sb.append(" SET ");
        for (MappingField mf : mfs)
            sb.append(mf.getColumnName()).append("=?,");

        sb.setCharAt(sb.length() - 1, ' ');
    }

}
