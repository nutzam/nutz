package org.nutz.mvc.view;

import org.nutz.ioc.Ioc;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.mvc.View;
import org.nutz.mvc.ViewMaker;

/**
 * 默认的的视图工厂类
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 */
public class DefaultViewMaker implements ViewMaker {

    public static final String VIEW_JSP = "jsp";
    public static final String VIEW_JSON = "json";
    public static final String VIEW_JSONP = "jsonp";
    public static final String VIEW_REDIRECT = "redirect";
    public static final String VIEW_REDIRECT2 = ">>";
    public static final String VIEW_VOID = "void";
    public static final String VIEW_IOC = "ioc";
    public static final String VIEW_HTTP = "http";
    public static final String VIEW_FORWARD = "forward";
    public static final String VIEW_FORWARD2 = "->";
    public static final String VIEW_RAW = "raw";

    public View make(Ioc ioc, String type, String value) {
        type = type.toLowerCase();
        if (VIEW_JSP.equals(type))
            return new JspView(value);

        if (VIEW_JSON.equals(type) || VIEW_JSONP.equals(type))
            if (Strings.isBlank(value))
                return VIEW_JSONP.equals(type) ? UTF8JsonView.JSONP : UTF8JsonView.COMPACT;
            else {
                // 除高级的json format定义之外,也支持简单的缩写
                if (value.charAt(0) == '{')
                    return new UTF8JsonView(Json.fromJson(JsonFormat.class,
                                                          value)).setJsonp(VIEW_JSONP.equals(type));
                else if ("nice".equals(value))
                    return new UTF8JsonView(JsonFormat.nice()).setJsonp(VIEW_JSONP.equals(type));
                else if ("forlook".equals(value))
                    return new UTF8JsonView(JsonFormat.forLook()).setJsonp(VIEW_JSONP.equals(type));
                else if ("full".equals(value))
                    return new UTF8JsonView(JsonFormat.full()).setJsonp(VIEW_JSONP.equals(type));
                else if ("compact".equals(value))
                    return new UTF8JsonView(JsonFormat.compact()).setJsonp(VIEW_JSONP.equals(type));
                else if ("tidy".equals(value))
                    return new UTF8JsonView(JsonFormat.tidy()).setJsonp(VIEW_JSONP.equals(type));
                else
                    throw new IllegalArgumentException("unkown json view format : " + value);
            }

        if (VIEW_REDIRECT.equals(type) || VIEW_REDIRECT2.equals(type))
            return new ServerRedirectView(value);
        if (VIEW_FORWARD.equals(type) || VIEW_FORWARD2.equals(type))
            return new ForwardView(value);
        if (VIEW_VOID.equals(type))
            return new VoidView();
        if (VIEW_IOC.equals(type))
            return ioc.get(View.class, value);
        if (VIEW_HTTP.equals(type)) {
            String val = Strings.sBlank(value, "500");
            try {
                return new HttpStatusView(Integer.parseInt(val));
            }
            catch (NumberFormatException e) {
                return new HttpStatusView(Lang.map(val));
            }
        }
        if (VIEW_RAW.equals(type))
            return new RawView(value);
        return null;
    }

}
