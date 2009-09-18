var ioc = {
/*------------------------------------------------------------------*/
// Data source
	dataSource : {
		type :"org.apache.commons.dbcp.BasicDataSource",
		lifecycle : {
			depose :"close"
		},
		fields : {
			driverClassName : {
				java :"org.nutz.NutzUnitEnv.driver"
			},
			url : {
				java :"org.nutz.NutzUnitEnv.url"
			},
			username : {
				java :"org.nutz.NutzUnitEnv.userName"
			},
			password : {
				java :"org.nutz.NutzUnitEnv.password"
			}
		}
	},
/*------------------------------------------------------------------*/
// Dao
	dao : {
		type :"org.nutz.dao.impl.NutDao",
		args : [ {
			refer :"dataSource"
		} ]
	},
/*------------------------------------------------------------------*/
// Meta service
	metas : {
		type :"org.nutz.dao.test.meta.Pojos",
		args : [ {
			refer :"dao"
		} ]
	}
/*------------------------------------------------------------------*/
}