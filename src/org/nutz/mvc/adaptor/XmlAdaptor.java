package org.nutz.mvc.adaptor;

import org.nutz.lang.Lang;
import org.nutz.lang.Xmls;
import org.nutz.mvc.adaptor.injector.VoidInjector;
import org.nutz.mvc.adaptor.injector.XmlInjector;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.impl.AdaptorErrorContext;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

/**
 * 假设，整个获得的输入流，是一个 XML 字符串
 *
 * @author howe(howechiang@gmail.com)
 */
public class XmlAdaptor extends PairAdaptor {

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
        // Read all as String
        try {
            //TODO URL传来的参数会丢失
            return Xmls.xmlToMap(getStringFromInputStream(req.getInputStream()));
        } catch (Exception e) {
            throw Lang.wrapThrow(e);
        }
    }

    private String getStringFromInputStream(InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }
}