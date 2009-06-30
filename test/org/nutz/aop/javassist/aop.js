var ioc = {
	// ---------------------------------------------------------
	r : {},
	// ---------------------------------------------------------
	mc : {
		type :"org.nutz.aop.javassist.lstn.MethodCounter",
		args : [ {
			java :"org.nutz.aop.javassist.NutIocAopTest.CC"
		} ]
	},
	// ---------------------------------------------------------
	$aop : {
		byname : {
			'r' : [ {
				type :"org.nutz.ioc.impl.AopMethod",
				fields : {
					methods : {
						args : [ ".*" ]
					},
					listeners : [ {
						refer :"mc"
					} ]
				}
			} ]
		}
	}
// ---------------------------------------------------------
}