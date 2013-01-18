package org.nutz.mvc.filter;

import javax.servlet.http.HttpSession;

import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionFilter;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.View;
import org.nutz.mvc.view.ServerRedirectView;

/**
 * 检查当前 Session，如果存在某一属性，并且不为 null，则通过 <br>
 * 否则，返回一个 ServerRecirectView 到对应 path
 * <p>
 * 构造函数需要两个参数
 * <ul>
 * <li>第一个是， 需要检查的属性名称。如果 session 里存在这个属性，则表示通过检查
 * <li>第二个是，如果未通过检查，将当前请求转向何处。 一个类似 /yourpath/xxx.xx 的路径
 * </ul>
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class CheckSession implements ActionFilter {

    private String name;
    private String path;

    public CheckSession(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public View match(ActionContext context) {
    	HttpSession session = Mvcs.getHttpSession(false);
    	if (session == null)
    		return null;
        Object obj = session.getAttribute(name);
        if (null == obj)
            return new ServerRedirectView(path);
        return null;
    }

}
