package org.nutz.dao.impl.entity.macro;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.MappingField;
import org.nutz.dao.impl.jdbc.NutPojo;
import org.nutz.dao.jdbc.ValueAdaptor;
import org.nutz.dao.sql.Pojo;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlType;
import org.nutz.lang.Lang;

public class SqlFieldMacro extends NutPojo {

    private Sql sql;

    private MappingField entityField;

    private SqlFieldMacro() {
        super();
    }

    public SqlFieldMacro(MappingField field, String str) {
        super();
        this.entityField = field;
        this.sql = Sqls.create(str);
        this.setSqlType(this.sql.getSqlType());
        this.setEntity(field.getEntity());
    }

    @Override
    public Pojo setOperatingObject(Object obj) {
        super.setOperatingObject(obj);
        if (null != obj) {
            Entity<?> en = entityField.getEntity();
            if (!en.getType().isInstance(obj))
                throw Lang.makeThrow(    "Invalid operating object '%s' for field '%s'",
                                        obj.getClass().getName(),
                                        entityField.toString());

            // 填充占位符 ...
            for (String name : sql.varIndex().names())
                if (!name.equals("table") && !name.equals("view") && !name.equals("field"))
                    sql.vars().set(name, en.getField(name).getValue(obj));
            // 填充变量 ...
            for (String name : sql.paramIndex().names())
                sql.params().set(name, en.getField(name).getValue(obj));
        }
        return this;
    }

    public void onAfter(Connection conn, ResultSet rs) throws SQLException {
        if (rs.next()) {
            String colName = rs.getMetaData().getColumnName(1);
            Object obj = entityField.getAdaptor().get(rs, colName);
            entityField.setValue(this.getOperatingObject(), obj);
        }
    }

    public SqlType getSqlType() {
        return sql.getSqlType();
    }

    public ValueAdaptor[] getAdaptors() {
        return sql.getAdaptors();
    }

    public Object[][] getParamMatrix() {
        return sql.getParamMatrix();
    }

    public String toPreparedStatement() {
        return _parseSQL(sql.duplicate()).toPreparedStatement();
    }

    @Override
    public Pojo duplicate() {
        SqlFieldMacro re = new SqlFieldMacro();
        re.sql = sql.duplicate();
        re.entityField = entityField;
        re.setSqlType(sql.getSqlType());
        re.setEntity(entityField.getEntity());
        return re;
    }

    private Sql _parseSQL(Sql sql) {
        for (String name : sql.varIndex().names()) {
            if ("view".equals(name))
                sql.vars().set("view", getEntity().getViewName());
            else if ("table".equals(name))
                sql.vars().set("table", getEntity().getTableName());
            else if ("field".equals(name))
                sql.vars().set("field", entityField.getColumnName());
            else
                sql.vars().set(name, getEntity().getField(name).getValue(getOperatingObject()));
        }

        for (String name : sql.paramIndex().names()) {
            sql.params().set(name, getEntity().getField(name).getValue(getOperatingObject()));
        }

        return sql;
    }

}
