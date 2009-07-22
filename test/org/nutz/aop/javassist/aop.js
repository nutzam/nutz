var ioc = {
	// ---------------------------------------------------------
	r : {},
	// ---------------------------------------------------------
	$aop : {
		items : [
		/*-----------------------------------------------------*/
		{
			type :"org.nutz.aop.javassist.lstn.MethodCounter",
			args : [ {
				java :"org.nutz.aop.javassist.NutIocAopTest.CC"
			} ],
			hooks : [ {
				regex :"r",
				/* mode :"OBJECT_NAME", */
				methods : [ {
					regex :".*"
				/* ,access :"ALL" */
				} ]
			} ]
		} ]
	}
// ---------------------------------------------------------
}