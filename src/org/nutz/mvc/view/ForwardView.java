package org.nutz.mvc.view;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.mvc.Mvcs;

/**
 * 内部重定向视图
 * <p/>
 * 根据传入的视图名，决定视图的路径：
 * <ul>
 * <li>如果视图名以 '/' 开头， 则被认为是一个 全路径
 * <li>否则，将视图名中的 '.' 转换成 '/'，并加入前缀 "/WEB-INF/"
 * </ul>
 * 通过注解映射的例子：
 * <ul>
 * <li>'@Ok("forward:abc.cbc")' => /WEB-INF/abc/cbc
 * <li>'@Ok("forward:/abc/cbc")' => /abc/cbc
 * <li>'@Ok("forward:/abc/cbc.jsp")' => /abc/cbc.jsp
 * </ul>
 * 
 * @author mawm(ming300@gmail.com)
 * @author zozoh(zozohtnt@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 */
public class ForwardView extends AbstractPathView {

    public ForwardView(String dest) {
        super(dest == null ? null : dest.replace('\\', '/'));
    }

    public void render(HttpServletRequest req, HttpServletResponse resp, Object obj)
            throws Exception {
        String path = evalPath(req, obj);
        String args = "";
        if (path != null && path.contains("?")) { //将参数部分分解出来
            args = path.substring(path.indexOf('?'));
            path = path.substring(0, path.indexOf('?'));
        }

        String ext = getExt();        
        // 空路径，采用默认规则
        if (Strings.isBlank(path)) {
            path = Mvcs.getRequestPath(req);
            path = "/WEB-INF"
                    + (path.startsWith("/") ? "" : "/")
                    + Files.renameSuffix(path, ext);
        }
        // 绝对路径 : 以 '/' 开头的路径不增加 '/WEB-INF'
        else if (path.charAt(0) == '/') {
            if (!path.toLowerCase().endsWith(ext))
                path += ext;
        }
        // 包名形式的路径
        else {
            path = "/WEB-INF/" + path.replace('.', '/') + ext;
        }

        // 执行 Forward
        path = path + args;
        RequestDispatcher rd = req.getRequestDispatcher(path);
        if (rd == null)
            throw Lang.makeThrow("Fail to find Forward '%s'", path);
        // Do rendering
        rd.forward(req, resp);
    }

    /**
     * 子类可以覆盖这个方法，给出自己特殊的后缀,必须小写哦
     * 
     * @return 后缀
     */
    protected String getExt() {
        return "";
    }

}
