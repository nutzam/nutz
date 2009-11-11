var ioc = {
	// ---------------------------------------------
	fox : {
		name : 'Fox'
	},
	sb : {
		type : 'java.lang.StringBuilder'
	},
	// ---------------------------------------------
	lst : {
		type : 'org.nutz.ioc.json.pojo.AnimalListener',
		args : [ {
			refer : 'sb'
		} ]
	}
}
