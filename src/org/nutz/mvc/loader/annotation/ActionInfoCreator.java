package org.nutz.mvc.loader.annotation;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.*;

import org.nutz.ioc.annotation.InjectName;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.lang.util.ClassMeta;
import org.nutz.lang.util.ClassMetaReader;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ActionFilter;
import org.nutz.mvc.ActionInfo;
import org.nutz.mvc.HttpAdaptor;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.ObjectInfo;
import org.nutz.mvc.annotation.*;

public abstract class ActionInfoCreator {

    private static final Log log = Logs.get();

    public static ActionInfo createInfo(Class<?> type) {
        ActionInfo ai = new ActionInfo();
        evalEncoding(ai, Mirror.getAnnotationDeep(type, Encoding.class));
        evalHttpAdaptor(ai, Mirror.getAnnotationDeep(type, AdaptBy.class));
        evalActionFilters(ai, Mirror.getAnnotationDeep(type, Filters.class));
        evalPathMap(ai, Mirror.getAnnotationDeep(type, PathMap.class));
        evalOk(ai, Mirror.getAnnotationDeep(type, Ok.class));
        evalFail(ai, Mirror.getAnnotationDeep(type, Fail.class));
        evalAt(ai, Mirror.getAnnotationDeep(type, At.class), type.getSimpleName(), false);
        evalActionChainMaker(ai, Mirror.getAnnotationDeep(type, Chain.class));
        evalModule(ai, type);
        if (Mvcs.DISPLAY_METHOD_LINENUMBER) {
            InputStream ins = type.getClassLoader().getResourceAsStream(type.getName().replace(".", "/") + ".class");
            if (ins != null) {
                try {
                    ClassMeta meta = ClassMetaReader.build(ins);
                    ai.setMeta(meta);
                }
                catch (Exception e) {
                }
            }
        }
        return ai;
    }

    public static ActionInfo createInfo(Method method) {
        ActionInfo ai = new ActionInfo();
        evalEncoding(ai, Mirror.getAnnotationDeep(method, Encoding.class));
        evalHttpAdaptor(ai, Mirror.getAnnotationDeep(method, AdaptBy.class));
        evalActionFilters(ai, Mirror.getAnnotationDeep(method, Filters.class));
        evalOk(ai, Mirror.getAnnotationDeep(method, Ok.class));
        evalFail(ai, Mirror.getAnnotationDeep(method, Fail.class));
        evalHttpMethod(ai, method, Mirror.getAnnotationDeep(method, At.class));
        evalAt(ai, Mirror.getAnnotationDeep(method, At.class), method.getName(), true);
        evalActionChainMaker(ai, Mirror.getAnnotationDeep(method, Chain.class));
        ai.setMethod(method);
        return ai;
    }


    public static void evalHttpMethod(ActionInfo ai, Method method, At at) {
        if (Mirror.getAnnotationDeep(method, GET.class) != null)
            ai.getHttpMethods().add("GET");
        if (Mirror.getAnnotationDeep(method, POST.class) != null)
            ai.getHttpMethods().add("POST");
        if (Mirror.getAnnotationDeep(method, PUT.class) != null)
            ai.getHttpMethods().add("PUT");
        if (Mirror.getAnnotationDeep(method, DELETE.class) != null)
            ai.getHttpMethods().add("DELETE");
        if (Mirror.getAnnotationDeep(method, OPTIONS.class) != null)
            ai.getHttpMethods().add("OPTIONS");
        if (Mirror.getAnnotationDeep(method, PATCH.class) != null)
            ai.getHttpMethods().add("PATCH");
        if (at != null) {
            for (String m : at.methods())
                ai.getHttpMethods().add(m.toUpperCase());
        }
    }

    public static void evalActionChainMaker(ActionInfo ai, Chain cb) {
        if (null != cb) {
            ai.setChainName(cb.value());
        }
    }

    public static void evalAt(ActionInfo ai, At at, String def, boolean isMethod) {
        if (null != at) {
            if (null == at.value() || at.value().length == 0) {
                ai.setPaths(Lang.array("/" + def.toLowerCase()));
            } else {
                ai.setPaths(at.value());
            }

            if (!Strings.isBlank(at.key()))
                ai.setPathKey(at.key());
            if (at.top())
                ai.setPathTop(true);
        } else if (isMethod) {
            // 由于EntryDeterminer机制的存在，action方法上可能没有@At，这时候给一个默认的入口路径
            ai.setPaths(Lang.array("/" + def.toLowerCase()));
        }
    }

    @SuppressWarnings("unchecked")
    private static void evalPathMap(ActionInfo ai, PathMap pathMap) {
        if (pathMap != null) {
            ai.setPathMap(Json.fromJson(Map.class, pathMap.value()));
        }
    }

    public static void evalFail(ActionInfo ai, Fail fail) {
        if (null != fail) {
            ai.setFailView(fail.value());
        }
    }

    public static void evalOk(ActionInfo ai, Ok ok) {
        if (null != ok) {
            ai.setOkView(ok.value());
        }
    }

    public static void evalModule(ActionInfo ai, Class<?> type) {
        ai.setModuleType(type);
        String beanName = null;
        // 按照5.10.3章节的说明，优先使用IocBean.name的注解声明bean的名字 Modify By QinerG@gmai.com
        InjectName innm = Mirror.getAnnotationDeep(type,InjectName.class);
        IocBean iocBean = Mirror.getAnnotationDeep(type,IocBean.class);
        if (innm == null && iocBean == null) // TODO 再考虑考虑
            return;
        if (iocBean != null) {
            beanName = iocBean.name();
        }
        if (Strings.isBlank(beanName)) {
            if (innm != null && !Strings.isBlank(innm.value())) {
                beanName = innm.value();
            } else {
                beanName = Strings.lowerFirst(type.getSimpleName());
            }
        }
        ai.setInjectName(beanName);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void evalActionFilters(ActionInfo ai, Filters filters) {
        if (null != filters) {
            List<ObjectInfo<? extends ActionFilter>> list = new ArrayList<ObjectInfo<? extends ActionFilter>>(filters.value().length);
            for (By by : filters.value()) {
                list.add(new ObjectInfo(by.type(), by.args()));
            }
            ai.setFilterInfos(list.toArray(new ObjectInfo[list.size()]));
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void evalHttpAdaptor(ActionInfo ai, AdaptBy ab) {
        if (null != ab) {
            ai.setAdaptorInfo((ObjectInfo<? extends HttpAdaptor>) new ObjectInfo(ab.type(),
                                                                                 ab.args()));
        }
    }

    public static void evalEncoding(ActionInfo ai, Encoding encoding) {
        if (null == encoding) {
            ai.setInputEncoding(org.nutz.lang.Encoding.UTF8);
            ai.setOutputEncoding(org.nutz.lang.Encoding.UTF8);
        } else {
            ai.setInputEncoding(Strings.sNull(encoding.input(), org.nutz.lang.Encoding.UTF8));
            ai.setOutputEncoding(Strings.sNull(encoding.output(), org.nutz.lang.Encoding.UTF8));
        }
    }

}
