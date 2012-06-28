package org.nutz.dao.impl.sql.pojo;

import java.util.List;

import org.nutz.dao.FieldMatcher;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.MappingField;
import org.nutz.lang.Lang;

public class QueryEntityFieldsPItem extends NoParamsPItem {

    public void joinSql(Entity<?> en, StringBuilder sb) {
        FieldMatcher fm = getFieldMatcher();
        if (null == fm) {
            sb.append("* ");
        } else {
            List<MappingField> efs = _en(en).getMappingFields();

            int old = sb.length();

            for (MappingField ef : efs) {
                if (fm.match(ef.getName()))
                    sb.append(ef.getColumnName()).append(',');
            }

            if (sb.length() == old)
                throw Lang.makeThrow("No columns be queryed: '%s'", _en(en));

            sb.setCharAt(sb.length() - 1, ' ');
        }
    }

}
