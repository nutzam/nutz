package org.nutz.dao.impl.sql.pojo;

import java.util.List;

import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.MappingField;
import org.nutz.dao.util.Pojos;

public class InsertFieldsPItem extends NoParamsPItem {

    private static final long serialVersionUID = 1L;

    public void joinSql(Entity<?> en, StringBuilder sb) {
        List<MappingField> mfs = Pojos.getFieldsForInsert(_en(en), getFieldMatcher());

        sb.append('(');
        for (MappingField mf : mfs)
            sb.append(mf.getColumnNameInSql()).append(',');

        sb.setCharAt(sb.length() - 1, ')');
        sb.append(' ');
    }

}
