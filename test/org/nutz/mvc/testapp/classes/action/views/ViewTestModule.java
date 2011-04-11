package org.nutz.mvc.testapp.classes.action.views;

import org.nutz.ioc.loader.annotation.IocBean;

import org.nutz.ioc.annotation.InjectName;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

@InjectName
@IocBean
@At("/views")
public class ViewTestModule {

	@At("/jsp")
	@Ok("jsp:jsp.views.jspView")
	public void jspView(){
		
	}
}
