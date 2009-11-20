var ioc = {
	animal : {
		events : {
			fetch : 'onFetch',
			create : 'onCreate',
			depose : 'onDepose'
		}
	},

	fox : {
		parent : 'animal',
		fields : {
			name : "XiaoQiang"
		}
	}
}