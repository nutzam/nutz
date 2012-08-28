package org.nutz.dao.impl.jdbc.h2;

import org.nutz.dao.DB;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.MappingField;
import org.nutz.dao.impl.entity.macro.SqlFieldMacro;
import org.nutz.dao.impl.jdbc.psql.PsqlJdbcExpert;
import org.nutz.dao.jdbc.JdbcExpertConfigFile;
import org.nutz.dao.sql.Pojo;

public class H2JdbcExpert extends PsqlJdbcExpert {

    public H2JdbcExpert(JdbcExpertConfigFile conf) {
        super(conf);
    }

    public String getDatabaseType() {
        return DB.H2.name();
    }

    public Pojo fetchPojoId(Entity<?> en, MappingField idField) {
        String autoSql = "SELECT IDENTITY() as $field from $view";
        Pojo autoInfo = new SqlFieldMacro(idField, autoSql);
        autoInfo.setEntity(en);
        return autoInfo;
    }
}
