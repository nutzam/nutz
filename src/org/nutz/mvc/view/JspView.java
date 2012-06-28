package org.nutz.mvc.view;


/**
 * 指向/WebRoot/下面的jsp视图<br>
 * 通过 request.getRequestDispatcher(path).forward(request, response)方式来处理页面<br>
 * 构造函数:<br>
 * <ul>
 * <li>new JspView("logon/wel");指向 /WebRoot/logon/wel.jsp页面!</li>
 * <li>new JspView("a/b/c/page");/a/b/c/page.jsp页面</li>
 * </ul>
 * 
 * 本类属于自定义视图,用于mvc的的方法中,指向到不同页面中去!<br>
 * <code>
 &#64;Ok("void")
 &#64;At
 public View welcome(@Param("username")
 String userName) {
 return new JspView("/logon/wel.jsp",false);
 //或者return new JspView("logon.wel");
 }
 * </code>
 * 
 */
/**
 * 根据传入的视图名，决定视图的路径：
 * <ul>
 * <li>如果视图名以 '/' 开头， 则被认为是一个 JSP 的全路径
 * <li>否则，将视图名中的 '.' 转换成 '/'，并加入前缀 "/WEB-INF/" 和后缀 ".jsp"
 * </ul>
 * 通过注解映射的例子：
 * <ul>
 * <li>'@Ok("jsp:abc.cbc")' => /WEB-INF/abc/cbc.jsp
 * <li>'@Ok("jsp:/abc/cbc")' => /abc/cbc.jsp
 * <li>'@Ok("jsp:/abc/cbc.jsp")' => /abc/cbc.jsp
 * </ul>
 * 
 * @author mawm(ming300@gmail.com)
 * @author zozoh(zozohtnt@gmail.com)
 */
public class JspView extends ForwardView {
    
    public JspView(String name) {
        super(name);
    }

    @Override
    protected String getExt() {
        return ".jsp";
    }
}
