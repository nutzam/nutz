package org.nutz.mvc.adaptor;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.Xmls;
import org.nutz.mvc.adaptor.injector.VoidInjector;
import org.nutz.mvc.adaptor.injector.XmlInjector;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.impl.AdaptorErrorContext;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 假设，整个获得的输入流，是一个 XML 字符串
 *
 * @author howe(howechiang@gmail.com)
 */
public class XmlAdaptor extends PairAdaptor {

    protected boolean lowerFirst;

    protected boolean dupAsList;

    protected List<String> alwaysAsList;

    public XmlAdaptor() {}

    public XmlAdaptor(boolean lowerFirst, boolean dupAsList, String alwaysAsList) {
        super();
        this.lowerFirst = lowerFirst;
        this.dupAsList = dupAsList;
        this.alwaysAsList = Arrays.asList(Strings.splitIgnoreBlank(alwaysAsList));
    }

    @Override
    protected ParamInjector evalInjector(Type type, Param param) {
        if (param == null || "..".equals(param.value())) {
            Class<?> clazz = Lang.getTypeClass(type);
            if (clazz != null && AdaptorErrorContext.class.isAssignableFrom(clazz)) {
                return new VoidInjector();
            }
            return new XmlInjector(type, null);
        }
        return super.evalInjector(type, param);
    }

    @Override
    public Object getReferObject(ServletContext sc,
                                 HttpServletRequest req,
                                 HttpServletResponse resp,
                                 String[] pathArgs) {
        try {
            return Xmls.xmlToMap(req.getInputStream(), lowerFirst, dupAsList, alwaysAsList);
        }
        catch (Exception e) {
            throw Lang.wrapThrow(e);
        }
    }
}
