package org.nutz.dao.impl.jdbc.derby;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.nutz.dao.jdbc.ValueAdaptor;

public class DerbyBooleanAdaptor implements ValueAdaptor {

    public Object get(ResultSet rs, String colName) throws SQLException {
        Object obj = rs.getObject(colName);
        if (obj == null)
            return false;
        return "T".equals(String.valueOf(obj));
    }

    public void set(PreparedStatement stat, Object obj, int index)
            throws SQLException {
        if (obj == null) {
            stat.setNull(index, Types.VARCHAR);
        } else {
            boolean v;
            if (obj instanceof Boolean)
                v = (Boolean) obj;
            else if (obj instanceof Number)
                v = ((Number) obj).intValue() > 0;
            else if (obj instanceof Character)
                v = Character.toUpperCase((Character) obj) == 'T';
            else
                v = Boolean.valueOf(obj.toString());
            if (v)
                stat.setString(index, "T");
            else
                stat.setString(index, "F");
        }
    }

}
