var ioc = {
	// ---------------------------------------------------------
	srv : {
		type :"org.nutz.log.DemoService"
	},
	// ---------------------------------------------------------
	$aop : {
		items : [
		/*----------------------- AOP Logging -----*/
		{
			factoryType :"org.nutz.ioc.aop.LogHookingFactory",
			/*
			 * Init params
			 */
			init : {
				deep :6,
				output :"org.nutz.log.file.FileLogOutput",
				file : {
					java :"org.nutz.log.LogTest.logFilePath"
				},
				format : {
					showThread :true,
					pattern :"yy-MM-dd hh-mm-ss.SSS",
					width :80
				}
			},
			/*
			 * Hook each items
			 */
			hooks : [ {
				/* print all service */
				regex :"metas",
				mode :"OBJECT_NAME",
				config : {
					re :true
				},
				methods : [ {
					regex :".*",
					ignore :"dao",
					config : {
						re :true
					}
				}]
			} ]
		} /* ~ end AOP Logging */
		]
	// ~ end Items
	}
}