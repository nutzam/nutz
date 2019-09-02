package org.nutz.mvc.view;

import org.nutz.el.El;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.segment.CharSegment;
import org.nutz.lang.segment.Segment;
import org.nutz.lang.util.Context;
import org.nutz.mvc.*;
import org.nutz.mvc.impl.contextCollector.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.Map.Entry;

/**
 * @author mawm(ming300 @ gmail.com)
 * @author wendal(wendal1985 @ gmail.com)
 */
public abstract class AbstractPathView implements View {

    private Segment dest;

    private Map<String, El> exps;

    private static List<ViewContextCollector> defVcc = new ArrayList<ViewContextCollector>();

    static {
        defVcc.add(new SharedCollector());
        defVcc.add(new ServletContextCollector());
        defVcc.add(new AttrCollector());
        defVcc.add(new PathargsCollector());
        defVcc.add(new SessionCollector());
        defVcc.add(new ParamCollector());
        defVcc.add(new RefCollector());
        defVcc.add(new AtCollector());
        defVcc.add(new ReturnCollector());
    }

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
        if (exps.size() != 0) {
            Context expContext = createContext(req, obj);
            for (Entry<String, El> en : exps.entrySet())
                context.set(en.getKey(), en.getValue().eval(expContext));
        }
        // 生成解析后的路径
        return Strings.trim(this.dest.render(context).toString());
    }

    /**
     * 为一次 HTTP 请求，创建一个可以被表达式引擎接受的上下文对象
     *
     * @param req HTTP 请求对象
     * @param obj 入口函数的返回值
     * @return 上下文对象
     */
    public static Context createContext(HttpServletRequest req, Object obj) {
        Context context = Lang.context();
        for (int i = 0; i < defVcc.size(); i++) {
            ViewContextCollector vcc = defVcc.get(i);
            context.putAll(vcc.collect(req, obj));
        }
        return context;
    }
}
