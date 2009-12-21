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
public class JspView extends NamePathJspView {

	/**
	 * @param name
	 * @param needGeneratePath
	 */
	public JspView(String name, boolean needGeneratePath) {
		super(name, needGeneratePath);

	}

	/**
	 * @param name
	 */
	public JspView(String name) {
		super(name);

	}

	/**
	 * 指向/WebRoot/下面的jsp视图
	 */
	@Override
	protected String getJspDir() {
		return "/";
	}

}
