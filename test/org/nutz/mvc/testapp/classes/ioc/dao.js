var ioc = {
	dataSource : {
            type : "com.alibaba.druid.pool.DruidDataSource",
            events : {
                    depose : 'close'
            },
            fields : {
                    //driverClassName : 'org.h2.Driver',
                    url : 'jdbc:h2:mem:',
                    username : 'sa',
                    password : 'sa'
            }
    },
    dao : {
    		type : "org.nutz.dao.impl.NutDao",
    		args : [{refer:'dataSource'}]
    }
};