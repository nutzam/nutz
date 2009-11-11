var ioc = {
/*------------------------------------------------------------------*/
// Data source
	dataSource : {
		type :"org.apache.commons.dbcp.BasicDataSource",
		events : {
			depose :"close"
		},
		fields : {
			driverClassName : {
				java :"org.nutz.Nutzs.driver"
			},
			url : {
				java :"org.nutz.Nutzs.url"
			},
			username : {
				java :"org.nutz.Nutzs.userName"
			},
			password : {
				java :"org.nutz.Nutzs.password"
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