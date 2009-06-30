var ioc = {
	// ---------------------------------------------------------
	srv : {
		type :"org.nutz.log.DemoService"
	},
	sb : {
		type :"java.lang.StringBuilder"
	},
	"$logFormat" : {
		showThread :false,
		pattern :null,
		width :0
	},
	// ---------------------------------------------------------
	"$logListener" : {
		type :"org.nutz.log.aop.LogListener",
		args : [ {
			type :"org.nutz.log.Log",
			fields : {
				level :8,
				format : {
					refer :"$logFormat"
				},
				output : {
					type :"org.nutz.log.StringBuilderLogOutput",
					args : [ {
						refer :"sb"
					} ]
				}
			}
		}, {
			java :"org.nutz.log.LogTest.threadDeep"
		} ]
	},
	// ---------------------------------------------------------
	$aop : {
		byname : {
			"srv[.]*" : [ {
				type :"org.nutz.ioc.impl.AopMethod",
				fields : {
					methods : {
						args : [ ".*Method" ]
					},
					listeners : [ {
						refer :"$logListener"
					} ]
				}
			} ],
			"dao" : [{
				type :"org.nutz.ioc.impl.AopMethod",
				fields : {
					methods : {
						args : [ "^execute$" ]
					},
					listeners : [ {
						refer :"$logListener"
					} ]
				}
			}]
		}
	}
}