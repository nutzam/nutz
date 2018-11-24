package org.nutz.dao.impl.jdbc;

import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import org.nutz.dao.jdbc.Jdbcs;
import org.nutz.filepool.FilePool;

public class ClobValueAdapter2 extends ClobValueAdaptor {
    
    public ClobValueAdapter2(FilePool pool) {
        super(pool);
    }

    public void set(PreparedStatement stat, Object obj, int index) throws SQLException {
        if (null == obj) {
            stat.setNull(index, Types.CLOB);
        } else {
            Clob clob = (Clob) obj;
            Jdbcs.setCharacterStream(index, clob.getCharacterStream(), stat);
        }
    }

}
