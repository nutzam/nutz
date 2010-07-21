package org.nutz.dao;

import javax.sql.DataSource;

import org.nutz.dao.sql.Sql;

/**
 * 这是 NutDao 的另外一个扩展点，通过它，你可以在 Dao 执行 SQL 时做一些特殊的操作。
 * <p>
 * 比如将 SQL 记录到特别的地方便于性能分析等
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface DaoExecutor {

	/**
	 * 当调用到 dao.execute 时， NutDao 会调用这个接口，通过 dao.setExecutor 可以为其 设置一个自定义的
	 * DaoExecutor
	 * 
	 * @param dataSource
	 * @param runner
	 * @param sqls
	 */
	void execute(DataSource dataSource, DaoRunner runner, Sql... sqls);

}
