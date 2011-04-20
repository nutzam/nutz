var ioc = {
/*------------------------------------------------------------------*/
		
	config : {
		type : "org.nutz.ioc.impl.PropertiesProxy",
		fields : {
			paths : ["nutz-test.properties"]
		}
	},
// Data source
	dataSource : {
		type :"org.apache.commons.dbcp.BasicDataSource",
		events : {
			depose :"close"
		},
		fields : {
			driverClassName : {
				java :"$config.get('driver')"
			},
			url : {
				java :"$config.get('url')"
			},
			username : {
				java :"$config.get('username')"
			},
			password : {
				java :"$config.get('password')"
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