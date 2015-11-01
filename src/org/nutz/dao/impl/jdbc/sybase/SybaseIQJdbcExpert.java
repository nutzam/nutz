package org.nutz.dao.impl.jdbc.sybase;

import org.nutz.dao.DB;
import org.nutz.dao.Dao;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.impl.jdbc.AbstractJdbcExpert;
import org.nutz.dao.jdbc.JdbcExpertConfigFile;
import org.nutz.dao.sql.Pojo;
import org.nutz.lang.Lang;

public class SybaseIQJdbcExpert extends AbstractJdbcExpert {

	public SybaseIQJdbcExpert(JdbcExpertConfigFile conf) {
		super(conf);
	}

	public String getDatabaseType() {
		return DB.SYBASE.name();
	}

	public boolean createEntity(Dao dao, Entity<?> en) {
		throw Lang.noImplement();
	}

	public void formatQuery(Pojo pojo) {
		throw Lang.noImplement();
	}

}
