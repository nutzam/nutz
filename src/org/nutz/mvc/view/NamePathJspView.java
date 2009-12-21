package org.nutz.mvc.view;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.Strings;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.View;

/**
 * 
 * 执行/WEB-INF/下面的 jsp的视图. <br>
 * 通过 request.getRequestDispatcher(path).forward(request, response)方式来处理页面
 * 
 * <br>
 * getJspDir()子类设置不同的路径,以跳转到不同的路径下
 */
public class NamePathJspView implements View {

	private static final String OBJ_ATTR_NAME = "obj";

	/**
	 * 根据realPagePath来判断是否创建jsp的路径 <br>
	 * 如果:realPagePath=true,那么name应该形如: "/logon/welcome.jsp" 的方式 <br>
	 * 如果:realPagePath=false,那么name应该形如: "/logon/welcome"或者"/logon.welcome"
	 * 的方式,系统自动在名称后台拼加 .jsp 扩展名
	 * 
	 * @param name
	 *            页面的路径
	 * @param needGeneratePath
	 *            是否需要生成页面的路径
	 */
	public NamePathJspView(String name, boolean needGeneratePath) {
		if (needGeneratePath) {// 需要生成页面路径
			this.jspPath = generatePagePath(name);
		} else {// 采用 传人的路径
			this.jspPath = name;

		}
	}

	public NamePathJspView(String name) {
		this(name, true);// 默认处理页面路径
	}

	// edit by mawm 允许子类能够访问
	/**
	 * jsp文件的地址
	 */
	protected String jspPath;

	/**
	 * 
	 */
	public void render(HttpServletRequest req, HttpServletResponse resp, Object obj)
			throws Exception {
		if (null != obj)
			req.setAttribute(OBJ_ATTR_NAME, obj);
		RequestDispatcher rd;
		String path;
		if (Strings.isBlank(jspPath)) {// 取请求的路径
			// 如果第一个字符是 / 则删除
			String requestPath = Strings.removeFirst(Mvcs.getRequestPath(req), '/');

			StringBuilder s = new StringBuilder();
			s.append(getJspDir()).append(requestPath).append(".jsp");
			path = s.toString();
		} else
			path = jspPath;
		rd = req.getRequestDispatcher(path);
		if (rd == null)
			throw new Exception("Could not get RequestDispatcher for [" + jspPath
					+ "]: check up the file existed in your application please!the JspDir is ["
					+ getJspDir() + "]");
		rd.forward(req, resp);
	}

	/**
	 * 生成页面的相对路径
	 * 
	 * @param name
	 *            页面的描述: a/b/c/page 的形式
	 * @return 处理后的页面地址
	 */
	protected String generatePagePath(String name) {
		if (Strings.isBlank(name))
			return "";
		StringBuilder s = new StringBuilder(32);

		// 如果第一个字符是 / 则删除
		String tmp = Strings.removeFirst(name, '/');

		String dir = getJspDir();

		s.append(Strings.sNull(dir)).append(tmp.replace('.', '/')).append(".jsp");

		return s.toString();
	}

	/**
	 * 允许子类覆盖,可以设置 jsp的放置目录
	 * 
	 * @return jsp文件放置的目录,需要用 / 结尾
	 */
	protected String getJspDir() {
		return "/WEB-INF/";
	}

}
