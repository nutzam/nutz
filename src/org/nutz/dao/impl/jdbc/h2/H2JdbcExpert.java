package org.nutz.dao.impl.jdbc.h2;

import org.nutz.dao.DB;
import org.nutz.dao.impl.jdbc.psql.PsqlJdbcExpert;
import org.nutz.dao.jdbc.JdbcExpertConfigFile;

public class H2JdbcExpert extends PsqlJdbcExpert {

	public H2JdbcExpert(JdbcExpertConfigFile conf) {
		super(conf);
	}

	@Override
	public String getDatabaseType() {
		return DB.H2.name();
	}

}
