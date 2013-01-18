package org.nutz.mvc.view;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.nutz.el.El;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.segment.CharSegment;
import org.nutz.lang.segment.Segment;
import org.nutz.lang.util.Context;
import org.nutz.mvc.Loading;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.View;
import org.nutz.mvc.config.AtMap;
import org.nutz.mvc.impl.processor.ViewProcessor;

/**
 * @author mawm(ming300@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 */
public abstract class AbstractPathView implements View {

    private Segment dest;

    private Map<String, El> exps;

    public AbstractPathView(String dest) {
        if (null != dest) {
            this.dest = new CharSegment(Strings.trim(dest));
            this.exps = new HashMap<String, El>();
            // 预先将每个占位符解析成表达式
            for (String key : this.dest.keys()) {
                this.exps.put(key, new El(key));
            }
        }
    }

    protected String evalPath(HttpServletRequest req, Object obj) {
        if (null == dest)
            return null;

        Context context = Lang.context();

        // 解析每个表达式
        Context expContext = createContext(req, obj);
        for (Entry<String, El> en : exps.entrySet())
            context.set(en.getKey(), en.getValue().eval(expContext));

        // 生成解析后的路径
        return Strings.trim(this.dest.render(context).toString());
    }

    /**
     * 为一次 HTTP 请求，创建一个可以被表达式引擎接受的上下文对象
     * 
     * @param req
     *            HTTP 请求对象
     * @param obj
     *            入口函数的返回值
     * @return 上下文对象
     */
    @SuppressWarnings("unchecked")
    public static Context createContext(HttpServletRequest req, Object obj) {
        Context context = Lang.context();
        // 复制全局的上下文对象
        Object globalContext = Mvcs.getServletContext()
                                    .getAttribute(Loading.CONTEXT_NAME);
        if (globalContext != null) {
            context.putAll((Context) globalContext);
        }

        // 请求对象的属性列表
        Map<String,Object> req_attr = new HashMap<String, Object>();
        for (Enumeration<String> en = req.getAttributeNames(); en.hasMoreElements();) {
            String tem = en.nextElement();
            req_attr.put(tem, req.getAttribute(tem));
        }
        context.set("a", req_attr);//兼容最初的写法
        context.set("req_attr", req_attr);
        
        HttpSession session = Mvcs.getHttpSession(false);
        if (session != null) {
        	Map<String,Object> session_attr = new HashMap<String, Object>();
            for (Enumeration<String> en = session.getAttributeNames(); en.hasMoreElements();) {
                String tem = en.nextElement();
                session_attr.put(tem, session.getAttribute(tem));
            }
            context.set("session_attr", session_attr);
        }
        // 请求的参数表,需要兼容之前的p.参数, Fix issue 418
        Map<String,String> p = new HashMap<String, String>();
        for (Object o : req.getParameterMap().keySet()) {
            String key = (String) o;
            String value = req.getParameter(key);
            p.put(key, value);
            context.set(key, value);//以支持直接获取请求参数
        }
        context.set("p", p);
        
        Map<String, String> u = new HashMap<String, String>();
        AtMap at = Mvcs.getAtMap();
        if (at != null) {
            for(Object o : at.keys()){
                String key = (String) o;
                u.put(key, at.get(key));
            }
            context.set("u", u);
        }
        
        // 加入返回对象
        if (null != obj)
            context.set(ViewProcessor.DEFAULT_ATTRIBUTE, obj);
        return context;
    }
}
