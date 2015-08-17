package org.nutz.dao.impl.jdbc.psql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.nutz.dao.jdbc.ValueAdaptor;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;

public class PsqlJsonAdaptor implements ValueAdaptor {

    @Override
    public Object get(ResultSet rs, String colName) throws SQLException {
        return rs.getObject(colName);
    }

    @Override
    public void set(PreparedStatement stat, Object obj, int index) throws SQLException {
        if (null == obj) {
            stat.setNull(index, Types.NULL);
        } else {
            stat.setObject(index, Json.toJson(obj, JsonFormat.tidy()), Types.OTHER);
        }
    }
}
