package org.nutz.dao.impl.jdbc.dm;

import org.nutz.dao.DB;
import org.nutz.dao.impl.jdbc.oracle.OracleJdbcExpert;
import org.nutz.dao.jdbc.JdbcExpertConfigFile;

public class DmJdbcExpert extends OracleJdbcExpert {

    public DmJdbcExpert(JdbcExpertConfigFile conf) {
        super(conf);
    }

    public String getDatabaseType() {
        return DB.DM.name();
    }
}
