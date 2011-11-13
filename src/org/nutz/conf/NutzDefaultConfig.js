//Nutz默认配置
{
	"EL":{
		"custom":[
			"org.nutz.el.opt.custom.Max",
			"org.nutz.el.opt.custom.Min",
			"org.nutz.el.opt.custom.Trim"
		]}
	,"MVC":{
		"localization":{
			"type": "org.nutz.mvc.impl.NutMessageLoader",
			"args":[""]//这里的属性与原注解属性不一样, 但为了统一
		},
		"iocBy":{
			"type":"",
			"args":[]
		},
		"setupBy":{
			"type":"",//属性名与注解中名字不一样, 但是为了统一. 坑爹呀, 这里不能使用org.nutz.mvc.Setup为默认值, 因为它是接口, 不能被实例化的...
			"args":[]
		},
		"chainBy":{
			"type":"org.nutz.mvc.impl.NutActionChainMaker",
			"args":[]
		}
	}
}