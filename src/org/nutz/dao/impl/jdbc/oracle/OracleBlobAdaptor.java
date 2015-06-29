package org.nutz.dao.impl.jdbc.oracle;

import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import org.nutz.dao.impl.jdbc.BlobValueAdaptor;
import org.nutz.filepool.FilePool;

public class OracleBlobAdaptor extends BlobValueAdaptor {

    public OracleBlobAdaptor(FilePool pool) {
        super(pool);
    }

    public void set(PreparedStatement stat, Object obj, int i) throws SQLException {
        if (null == obj) {
            stat.setNull(i, Types.BLOB);
        } else {
            Blob blob = (Blob) obj;
            stat.setBinaryStream(i, blob.getBinaryStream(), blob.length());
        }
    }

}
