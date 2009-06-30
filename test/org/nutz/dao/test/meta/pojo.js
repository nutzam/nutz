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
				java :"org.nutz.Main.driver"
			},
			url : {
				java :"org.nutz.Main.url"
			},
			username : {
				java :"org.nutz.Main.userName"
			},
			password : {
				java :"org.nutz.Main.password"
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