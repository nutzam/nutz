package org.nutz.dao.impl.jdbc.psql;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.nutz.dao.jdbc.ValueAdaptor;

public class PsqlArrayAdaptor implements ValueAdaptor {

    private String customDbType;

    public PsqlArrayAdaptor(String customDbType) {
        this.customDbType = customDbType;
    }

    @Override
    public Object get(ResultSet rs, String colName) throws SQLException {
        return rs.getObject(colName);
    }

    @Override
    public void set(PreparedStatement stat, Object obj, int index) throws SQLException {
        if (null == obj) {
            stat.setNull(index, Types.NULL);
        } else {
            String typeName = customDbType.substring(0, customDbType.length() - 2);
            Array array = stat.getConnection().createArrayOf(typeName, (Object[]) obj);
            stat.setObject(index, array, Types.ARRAY);
        }
    }
}
