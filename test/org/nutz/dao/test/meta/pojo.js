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
	
	/*
	dataSource : {
        type : "com.jolbox.bonecp.BoneCPDataSource",
        events : {
            depose : 'close'
        },
        fields : {
            driverClass : 'org.h2.Driver',
            jdbcUrl : 'jdbc:h2:mem:nutzunit',
            username : 'sa',
            password : 'sa'
        }
	},*/
/*------------------------------------------------------------------*/
// Dao
	dao : {
		type :"org.nutz.dao.impl.NutDao",
		args : [ {refer :"dataSource"}, {refer :"sqls"} ]
	},
/*------------------------------------------------------------------*/
// Sqls
	sqls : {
		type : 'org.nutz.dao.impl.FileSqlManager',
		args : ['org/nutz/dao/test/sqls/dir'],
		fields : {
			regex : '^.*[.]sqls$'
		}
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