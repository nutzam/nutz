var ioc = {
		b : {
			factory : "org.nutz.ioc.json.issue1304.Issue1304A#make"
		},
		c : {
			factory : "$b#make"
		}
}