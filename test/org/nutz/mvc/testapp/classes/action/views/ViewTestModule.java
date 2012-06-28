package org.nutz.mvc.testapp.classes.action.views;

import java.io.InputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.nutz.ioc.annotation.InjectName;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.Streams;
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
    @Ok("forward:/${p.to == null ? 'base' : 'base'}.jsp")
    public void forwardView3(){
    }
    
    //--------------Raw view
    @At("/raw")
    @Ok("raw")
    public String raw(){
        return "ABC";
    }
    
    @At("/raw2")
    @Ok("raw")
    public InputStream raw2() throws Throwable{
        return Streams.fileIn("哈哈/abc.txt");
    }
    
    @At("/raw3")
    @Ok("raw")
    public Reader raw3() throws Throwable{
        return Streams.fileInr("哈哈/abc.txt");
    }
    
    @At("/raw4")
    @Ok("raw")
    public void raw4(){
    }
    
    @At("/raw5")
    @Ok("raw:json")
    public String raw5(){
        Map<String, String> map = new HashMap<String, String>();
        map.put("name", "wendal");
        return Json.toJson(map);
    }
}
