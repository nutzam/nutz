{
	"default" : {
		"ps" : [
		      "org.nutz.mvc.impl.processor.UpdateRequestAttributesProcessor",
		      "org.nutz.mvc.impl.processor.EncodingProcessor",
		      "org.nutz.mvc.impl.processor.ModuleProcessor",
		      "!org.nutz.integration.shiro.NutShiroProcessor",
		      "org.nutz.mvc.impl.processor.ActionFiltersProcessor",
		      "org.nutz.mvc.impl.processor.AdaptorProcessor",
		      "org.nutz.mvc.impl.processor.MethodInvokeProcessor",
		      "org.nutz.mvc.impl.processor.ViewProcessor"
		      ],
		"error" : 'org.nutz.mvc.impl.processor.FailProcessor'
	}
}