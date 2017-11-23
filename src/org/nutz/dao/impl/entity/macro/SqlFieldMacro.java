package org.nutz.dao.impl.entity.macro;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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

    /**
	 * 
	 */
	private static final long serialVersionUID = -3404648162248580014L;

	private Sql sql;

    private MappingField entityField;
    
    private boolean shallDuplicate;

    private SqlFieldMacro() {
        super();
    }

    public SqlFieldMacro(MappingField field, String str) {
        super();
        this.entityField = field;
        this.sql = Sqls.create(str);
        this.setSqlType(this.sql.getSqlType());
        this.setEntity(field.getEntity());
        shallDuplicate = sql.varIndex().size() > 0 || sql.paramIndex().size() > 0;
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

            prepareVarParam(sql);
        }
        return this;
    }

    public void onAfter(Connection conn, ResultSet rs, Statement stmt) throws SQLException {
        if (rs != null && rs.next()) {
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
        return _parseSQL().getParamMatrix();
    }

    public String toPreparedStatement() {
        return _parseSQL().toPreparedStatement();
    }

    @Override
    public Pojo duplicate() {
        SqlFieldMacro re = new SqlFieldMacro();
        re.sql = sql.duplicate();
        re.entityField = entityField;
        re.setSqlType(sql.getSqlType());
        re.setEntity(entityField.getEntity());
        re.shallDuplicate = shallDuplicate;
        return re;
    }

    private Sql _parseSQL() {
        if (!shallDuplicate)
            return sql;
        Sql sql = this.sql.duplicate();
        prepareVarParam(sql);
        return sql;
    }

    protected void prepareVarParam(Sql sql) {
        for (String name : sql.varIndex().names()) {
            if ("view".equals(name))
                sql.vars().set("view", getEntity().getViewName());
            else if ("table".equals(name))
                sql.vars().set("table", getEntity().getTableName());
            else if ("field".equals(name))
                sql.vars().set("field", entityField.getColumnName());
            else {
                sql.vars().set(name, getFieldVale(name, getOperatingObject()));
            }
        }

        for (String name : sql.paramIndex().names()) {
            if ("view".equals(name))
                sql.params().set("view", getEntity().getViewName());
            else if ("table".equals(name))
                sql.params().set("table", getEntity().getTableName());
            else if ("field".equals(name))
                sql.params().set("field", entityField.getColumnName());
            else
                sql.params().set(name, getFieldVale(name, getOperatingObject()));
        }
    }
    
    protected Object getFieldVale(String name, Object obj) {
        MappingField mf = getEntity().getField(name);
        if (mf == null) {
            return getEntity().getMirror().getEjecting(name).eject(obj);
        } else {
            return mf.getValue(obj);
        }
    }
}
