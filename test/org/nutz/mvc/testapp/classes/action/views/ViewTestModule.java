package org.nutz.mvc.testapp.classes.action.views;

import org.nutz.ioc.loader.annotation.IocBean;

import org.nutz.ioc.annotation.InjectName;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

@InjectName
@IocBean
@At("/views")
public class ViewTestModule {

	//---------------JSP View
	
	@At("/jsp")
	@Ok("jsp:jsp.views.jspView")
	public void jspView(){
	}
	
	@At("/jsp2")
	@Ok("jsp:jsp/views/jspView")
	public void jspView2(){
	}
	
	@At("/jsp3")
	@Ok("jsp:/WEB-INF/jsp/views/jspView")
	public void jspView3(){
	}
	
	@At("/jsp4")
	@Ok("jsp:/WEB-INF/jsp/views/jspView.jsp")
	public void jspView4(){
	}
	
	//-------------ServerRedirectView
	@At("/red")
	@Ok(">>:/${p.to}.jsp")
	public void serverRedirectView(){
	}

	@At("/red2")
	@Ok("RedirEct:/${p.to}.jsp")
	public void serverRedirectView2(){
	}

	@At("/red3")
	@Ok("redirect:/${p.to}.jsp")
	public void serverRedirectView3(){
	}
	
	//-------------ForwardView
	@At("/for")
	@Ok("->:/${p.to}.jsp")
	public void forwardView(){
	}


	@At("/for2")
	@Ok("fOrWard:/${p.to}.jsp")
	public void forwardView2(){
	}

	@At("/for3")
	@Ok("forward:/${p.to}.jsp")
	public void forwardView3(){
	}
}
