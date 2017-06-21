package org.nutz.mvc.adaptor;

import org.nutz.lang.Lang;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.adaptor.injector.ArrayInjector;
import org.nutz.mvc.adaptor.injector.MapPairInjector;
import org.nutz.mvc.adaptor.injector.NameInjector;
import org.nutz.mvc.adaptor.injector.ObjectNavlPairInjector;
import org.nutz.mvc.adaptor.injector.ObjectPairInjector;
import org.nutz.mvc.annotation.Param;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * 将整个 HTTP 请求作为名值对来处理
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author juqkai(juqkai@gmail.com)
 */
public class PairAdaptor extends AbstractAdaptor {

    private static final Log log = Logs.get();

    protected ParamInjector evalInjectorBy(Type type, Param param) {
        // TODO 这里的实现感觉很丑, 感觉可以直接用type进行验证与传递
        // TODO 这里将Type的影响局限在了 github issue #30 中提到的局部范围
        Class<?> clazz = Lang.getTypeClass(type);
        if (clazz == null) {
            if (log.isWarnEnabled())
                log.warnf("!!Fail to get Type Class : type=%s , param=%s", type, param);
            return null;
        }

        Type[] paramTypes = null;
        if (type instanceof ParameterizedType)
            paramTypes = ((ParameterizedType) type).getActualTypeArguments();

        if (null == param)
            return null;// 让超类来处理吧,我不管了!!

        String defaultValue = null;
        if (param.df() != null && !Params.ParamDefaultTag.equals(param.df()))
            defaultValue = param.df();
        String pm = param.value();
        String datefmt = param.dfmt();
        // POJO
        if ("..".equals(pm)) {
            if (Map.class.isAssignableFrom(clazz)) {
                return new MapPairInjector(type);
            }
            return new ObjectPairInjector(null, type);
        }
        // POJO with prefix
        else if (pm.startsWith("::") && pm.length() > 2) {
            return new ObjectNavlPairInjector(pm.substring(2), type);
        }
        // POJO[]
        else if (clazz.isArray()) {
            return new ArrayInjector(pm,
                                     null,
                                     type,
                                     paramTypes,
                                     defaultValue,
                                     param.array_auto_split());
        }

        // Name-value
        return getNameInjector(pm, datefmt, type, paramTypes, defaultValue);
    }

    protected ParamInjector getNameInjector(String pm,
                                            String datefmt,
                                            Type type,
                                            Type[] paramTypes,
                                            String defaultValue) {
        return new NameInjector(pm, datefmt, type, paramTypes, defaultValue);
    }

}
