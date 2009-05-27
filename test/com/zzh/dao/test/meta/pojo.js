{
/*------------------------------------------------------------------*/
// Data source
dataSource: { 
	type 	 : "org.apache.commons.dbcp.BasicDataSource",
	deposeby : "close",
	fields	: {
		driverClassName	: {java :"com.zzh.Main.driver"},
		url				: {java :"com.zzh.Main.url"},
		username		: {java :"com.zzh.Main.userName"},
		password		: {java :"com.zzh.Main.password"}
	}
},
/*------------------------------------------------------------------*/
// Dao
dao : {
	type	: "com.zzh.dao.impl.NutDao",
	args	: [{refer :"dataSource"}]
},
/*------------------------------------------------------------------*/
// Meta service
metas: {
	type : "com.zzh.dao.test.meta.Pojos",
	args	: [{refer :"dao"}]
}
/*------------------------------------------------------------------*/
}