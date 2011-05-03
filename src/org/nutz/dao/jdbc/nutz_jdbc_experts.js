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
		"postgresql.*" : "org.nutz.dao.impl.jdbc.psql.PsqlJdbcExpert"
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