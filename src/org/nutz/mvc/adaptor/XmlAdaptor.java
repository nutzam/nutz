package org.nutz.mvc.adaptor;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.Xmls;
import org.nutz.mvc.adaptor.injector.VoidInjector;
import org.nutz.mvc.adaptor.injector.XmlInjector;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.impl.AdaptorErrorContext;

/**
 * 假设，整个获得的输入流，是一个 XML 字符串
 *
 * @author howe(howechiang@gmail.com)
 */
public class XmlAdaptor extends PairAdaptor {
    
    protected boolean lowerFirst;
    
    protected boolean dupAsList;
    
    protected List<String> alwaysAsList;
    
    public XmlAdaptor() {
    }
    
    public XmlAdaptor(boolean lowerFirst, boolean dupAsList, String alwaysAsList) {
        super();
        this.lowerFirst = lowerFirst;
        this.dupAsList = dupAsList;
        this.alwaysAsList = Arrays.asList(Strings.splitIgnoreBlank(alwaysAsList));
    }

    protected ParamInjector evalInjector(Type type, Param param) {
        if (param == null || "..".equals(param.value())) {
            Class<?> clazz = Lang.getTypeClass(type);
            if (clazz != null && AdaptorErrorContext.class.isAssignableFrom(clazz))
                return new VoidInjector();
            return new XmlInjector(type, null);
        }
        return super.evalInjector(type, param);
    }

    public Object getReferObject(ServletContext sc,
                                 HttpServletRequest req,
                                 HttpServletResponse resp, String[] pathArgs) {
        try {
            return Xmls.xmlToMap(req.getInputStream(), lowerFirst, dupAsList, alwaysAsList);
        } catch (Exception e) {
            throw Lang.wrapThrow(e);
        }
    }
}