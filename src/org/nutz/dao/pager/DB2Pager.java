package org.nutz.dao.pager;

import org.nutz.dao.entity.Entity;
import org.nutz.lang.Lang;

public class DB2Pager extends AbstractPager {

	public String toSql(Entity<?> entity, String fields, String cnd) {
		throw Lang.makeThrow("Dont's support yet!");
	}

}
