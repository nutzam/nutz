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
			factoryType :"org.nutz.log.aop.LogListenerFactory",
			/*
			 * Init params
			 */
			init : {
				deep :6,
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
				regex :"srv.*",
				mode :"OBJECT_NAME",
				config : {
					file :""
				},
				methods : [ {
					regex :".*",
					access :"PUBLIC",
					config : {
						args : [],
						re :false
					}
				} ]
			}, {
				/* print by type */
				regex :"com.dt.ps.func.hb.*",
				mode :"OBJECT_TYPE",
				methods : [ {
					regex :".*",
					access :"PROTECTED",
					config : {
						file :"",
						args : [],
						re :false
					}
				} ]
			} ]
		} /* ~ end AOP Logging */
		]
	// ~ end Items
	}
}