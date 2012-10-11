package org.nutz.dao.impl.jdbc.oracle;

import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import org.nutz.dao.impl.jdbc.ClobValueAdaptor;
import org.nutz.dao.jdbc.Jdbcs;
import org.nutz.filepool.FilePool;

public class OracleClobAdapter extends ClobValueAdaptor {
    
    OracleClobAdapter(FilePool pool) {
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
