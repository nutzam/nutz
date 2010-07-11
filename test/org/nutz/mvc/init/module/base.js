var ioc = {
	obj : {
		type : "java.lang.String"
	},
	baseModule : {
		type : "org.nutz.mvc.init.module.BaseModule",
		scope : "request",
		fields:{
		  nameX : "NutzX"
		 }
	},
	requestModule : {
		type : "org.nutz.mvc.init.module.RequestScopeModule",
		scope : "request",
		fields:{
		  request : {mvc : "$request"}
		 }
	}
}