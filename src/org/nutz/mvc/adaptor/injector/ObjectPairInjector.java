package org.nutz.mvc.adaptor.injector;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.lang.born.Borning;
import org.nutz.lang.inject.Injecting;
import org.nutz.mvc.adaptor.ParamConvertor;
import org.nutz.mvc.adaptor.ParamExtractor;
import org.nutz.mvc.adaptor.ParamInjector;
import org.nutz.mvc.adaptor.Params;
import org.nutz.mvc.annotation.Param;

/**
 * 根据 HTTP 参数表，生成一个 POJO 对象
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author juqkai(juqkai@gmail.com)
 */
public class ObjectPairInjector implements ParamInjector {

    protected Injecting[] injs;
    protected String[] names;
    protected Mirror<?> mirror;
    protected Field[] fields;
    protected ParamConvertor[] converters;
    protected Borning<?> borning;
    protected String[] defaultValues;

    public ObjectPairInjector(String prefix, Type type) {
        prefix = Strings.isBlank(prefix) ? "" : Strings.trim(prefix);
        this.mirror = Mirror.me(type);
        this.borning = mirror.getBorning();
        fields = mirror.getFields();
        this.injs = new Injecting[fields.length];
        this.names = new String[fields.length];
        this.converters = new ParamConvertor[fields.length];

        for (int i = 0; i < fields.length; i++) {
            Field f = fields[i];
            this.injs[i] = mirror.getInjecting(f.getName());
            Param param = f.getAnnotation(Param.class);
            String nm = null == param ? f.getName() : param.value();
            String datefmt = null == param ? null : param.dfmt();
            String locale = null == param ? null : param.locale();
            this.names[i] = prefix + nm;
            this.converters[i] = Params.makeParamConvertor(f.getType(), datefmt, locale);
            if (param != null && !Params.ParamDefaultTag.equals(param.df())) {
                if (defaultValues == null)
                    defaultValues = new String[fields.length];
                defaultValues[i] = param.df();
            }
        }
    }

    public Object get(ServletContext sc,
                      HttpServletRequest req,
                      HttpServletResponse resp,
                      Object refer) {
        ParamExtractor pe = Params.makeParamExtractor(req, refer);
        Object obj = borning.born();
        for (int i = 0; i < injs.length; i++) {
            Object param = converters[i].convert(pe.extractor(names[i]));
            if (param == null && defaultValues != null && defaultValues[i] != null)
                param = defaultValues[i];
            if (null != param)
                injs[i].inject(obj, param);
        }
        return obj;
    }

}
