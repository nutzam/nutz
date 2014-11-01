package org.nutz.dao.impl.jdbc.sybase;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.nutz.dao.DB;
import org.nutz.dao.Dao;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.MappingField;
import org.nutz.dao.impl.jdbc.AbstractJdbcExpert;
import org.nutz.dao.jdbc.JdbcExpertConfigFile;
import org.nutz.dao.sql.Pojo;
import org.nutz.lang.Lang;
import org.nutz.log.Log;
import org.nutz.log.Logs;

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
    private final static Log log = Logs.get();
    
	@Override
	protected int getColumnIndex(Statement stat, ResultSetMetaData meta, MappingField mf) throws SQLException {
		 if (meta == null)
	            return 0;
	        int columnCount = meta.getColumnCount();
	        String colName = mf.getColumnName();
	        for (int i = 1; i <= columnCount; i++)
	            if (meta.getColumnName(i).equalsIgnoreCase(colName))
	                return i;
	        // TODO 尝试一下meta.getColumnLabel?
	        log.infof("Can not find @Column(%s) in table/view (%s)", colName, meta.getTableName(1));
	        throw Lang.makeThrow(SQLException.class, "Can not find @Column(%s)", colName);
	}
}
