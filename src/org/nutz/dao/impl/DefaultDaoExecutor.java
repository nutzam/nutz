package org.nutz.dao.impl;

import java.sql.Connection;

import javax.sql.DataSource;

import org.nutz.dao.ConnCallback;
import org.nutz.dao.DaoExecutor;
import org.nutz.dao.DaoRunner;
import org.nutz.dao.sql.Sql;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class DefaultDaoExecutor implements DaoExecutor {

	private static final Log log = Logs.getLog(DefaultDaoExecutor.class);

	public void execute(DataSource dataSource, DaoRunner runner, final Sql... sqls) {
		if (null != sqls) {
			runner.run(dataSource, new ConnCallback() {
				public void invoke(Connection conn) throws Exception {
					// Store the old auto commit setting
					boolean isAuto = conn.getAutoCommit();
					// If multiple SQL, change the auto commit
					if (isAuto && sqls.length > 1)
						conn.setAutoCommit(false);

					// 打印 LOG
					if (log.isDebugEnabled()) {
						for (int i = 0; i < sqls.length; i++) {
							if (null != sqls[i]) {
								log.debug(sqls[i].toString());
								sqls[i].execute(conn);
							}
						}
					}
					// 不打印
					else {
						for (int i = 0; i < sqls.length; i++)
							if (null != sqls[i])
								sqls[i].execute(conn);
					}
				}
			});
		}
	}

}
