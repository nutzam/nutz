/*
 * Nutz.Dao 的默认支持数据库的种类
 */
var ioc = {

	/*
	 * Experts 的映射列表
	 */
	experts : {
		"h2.*" : "org.nutz.dao.impl.jdbc.h2.H2JdbcExpert",
		"mysql.*" : "org.nutz.dao.impl.jdbc.mysql.MysqlJdbcExpert",
		"postgresql.*" : "org.nutz.dao.impl.jdbc.psql.PsqlJdbcExpert",
		"db2.*" : "org.nutz.dao.impl.jdbc.db2.Db2JdbcExpert",
		"oracle.*" : "org.nutz.dao.impl.jdbc.oracle.OracleJdbcExpert",
		// SqlServer2005 --> 9.0 , SqlServer2008 --> 10.0
		"microsoft sql server.*(9|10)[.].+" : "org.nutz.dao.impl.jdbc.sqlserver2005.Sqlserver2005JdbcExpert",
		"microsoft sql server.*(8)[.].+" : "org.nutz.dao.impl.jdbc.sqlserver2000.Sqlserver2000JdbcExpert",
		"microsoft sql server.*(11)[.].+" : "org.nutz.dao.impl.jdbc.sqlserver2012.Sqlserver2012JdbcExpert",
		"hsql.*" : "org.nutz.dao.impl.jdbc.hsqldb.HsqldbJdbcExpert",
		"sqlite" : "org.nutz.dao.impl.jdbc.sqlite.SQLiteJdbcExpert",
		".+derby.+" : "org.nutz.dao.impl.jdbc.derby.DerbyJdbcExpert"
	// ~ 映射结束
	},

	/*
	 * 所有 Expert 都能读到这个配置文件
	 */
	config : {
	// 默认的 Clob 以及 Blog 临时目录
	"pool-home" : "~/.nutz/tmp/dao/",
	// 临时目录大小，0 为不限大小
	"pool-max" : 2000,
	// Mysql 特殊配置
	"mysql-engine" : "InnoDB"
	// ～ 配置信息结束
	} };