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
				['.+','.+','ioc:myMI'],
				['com\\.service\\..+','.+','ioc:log'],
				['com\\.service\\.auth\\..+','.+','ioc:txSERIALIZABLE'],
				['com\\.service\\.blog\\..+','(get|save|update|delete).+','ioc:txREPEATABLE_READ'],
				['com\\.service\\.news\\..+','(get|set).+','ioc:txREAD_COMMITTED'],
				['com\\.service\\.media\\..+','(get|set).+','ioc:txREAD_UNCOMMITTED'],
				['com\\.service\\.status\\..+','(get|set).+','ioc:txNONE']
			]
		}
	}
}