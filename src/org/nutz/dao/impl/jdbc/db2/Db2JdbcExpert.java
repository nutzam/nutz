package org.nutz.dao.impl.jdbc.db2;

import org.nutz.dao.DB;
import org.nutz.dao.Dao;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.impl.jdbc.AbstractJdbcExpert;
import org.nutz.dao.jdbc.JdbcExpertConfigFile;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Pojo;
import org.nutz.dao.util.Pojos;
import org.nutz.lang.Lang;

public class Db2JdbcExpert extends AbstractJdbcExpert {

	public Db2JdbcExpert(JdbcExpertConfigFile conf) {
		super(conf);
	}

	public String getDatabaseType() {
		return DB.DB2.name();
	}

	public boolean createEntity(Dao dao, Entity<?> en) {
		throw Lang.noImplement();
	}

	public void formatQuery(Pojo pojo) {
		Pager pager = pojo.getContext().getPager();
		// 需要进行分页
		if (null != pager) {
			// 之前插入
			pojo.insertFirst(Pojos.Items.wrap("SELECT * FROM ("
												+ "SELECT ROW_NUMBER() OVER() AS ROWNUM, "
												+ "T.* FROM ("));
			// 之后插入
			pojo.append(Pojos.Items.wrapf(	") T) AS A WHERE ROWNUM BETWEEN %d AND %d",
											pager.getOffset(),
											pager.getOffset() + pager.getPageSize() - 1));
		}
	}

}
