var ioc = {
	log : {
		type :'org.nutz.aop.interceptor.LoggingMethodInterceptor'
	},
	myMI : {
		type : 'org.nutz.ioc.aop.config.impl.MyMI'
	},
	pet2 : {
		type : "org.nutz.ioc.aop.config.impl.Pet2"
	},

	$aop : {
		type : 'org.nutz.ioc.aop.config.impl.JsonAopConfigration',
		fields : {
			itemList : [
				['.+','toString','ioc:log'],
				['.+','.+','ioc:myMI']
			]
		}
	}
}