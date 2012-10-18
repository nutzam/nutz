package org.nutz.dao.impl.jdbc;

import java.io.File;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.nutz.dao.util.blob.SimpleClob;
import org.nutz.filepool.FilePool;
import org.nutz.lang.Streams;

public class ClobValueAdaptor extends AbstractFileValueAdaptor {

    public ClobValueAdaptor(FilePool pool) {
        super(pool);
        suffix = ".clob";
    }

    public Object get(ResultSet rs, String colName) throws SQLException {
        File f = this.createTempFile();
        Clob clob = rs.getClob(colName);
        if (clob == null)
            return null;
        Streams.writeAndClose(Streams.fileOutw(f), clob.getCharacterStream());
        return new SimpleClob(f);
    }

    public void set(PreparedStatement stat, Object obj, int i) throws SQLException {
        if (null == obj) {
            stat.setNull(i, Types.CLOB);
        } else {
            Clob clob = (Clob) obj;
            stat.setClob(i, clob);
        }
    }

}
