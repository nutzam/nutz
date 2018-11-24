package org.nutz.dao.impl.jdbc.oracle;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.nutz.dao.jdbc.ValueAdaptor;

/**
 * 对 Oracle，Types.BOOLEAN 对于 setNull 是不工作的 其他的数据库都没有这个问题，<br>
 * 所以，只好把类型设成 INTEGER了
 */
public class OracleBooleanAdaptor implements ValueAdaptor {

    public Object get(ResultSet rs, String colName) throws SQLException {
        boolean re = rs.getBoolean(colName);
        return rs.wasNull() ? null : re;
    }

    public void set(PreparedStatement stat, Object obj, int i) throws SQLException {
        if (null == obj) {
            stat.setNull(i, Types.INTEGER);
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
            stat.setBoolean(i, v);
        }
    }

}
