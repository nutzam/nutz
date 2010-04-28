package org.nutz;

import org.nutz.dao.Dao;
import org.nutz.dao.tools.Tables;
import org.nutz.log.Log;

/**
 * 用于识别当前版本号和版权声明! <br/>
 * Nutz is Licensed under the Apache License, Version 2.0 (the "License")
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * @author Wendal(wendal1985@gmail.com)
 * 
 */
public final class Nutz {

	public static String version() {
		return "1.a.28";
	}

	public static final void defineTableIfNoExists(	Dao dao,
													String tableName,
													String dodName,
													Log log) {
		if (log.isInfoEnabled())
			log.info("check database." + tableName);
	
		if (!dao.exists(tableName)) {
			if (log.isInfoEnabled())
				log.info("define " + tableName + " and relative tables");
			Tables.define(dao, "dod/" + dodName + ".dod");
		}
	}

}
