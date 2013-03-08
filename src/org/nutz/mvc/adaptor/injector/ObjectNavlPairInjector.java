package org.nutz.mvc.adaptor.injector;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.mapl.Mapl;
import org.nutz.mvc.adaptor.ParamExtractor;
import org.nutz.mvc.adaptor.ParamInjector;
import org.nutz.mvc.adaptor.Params;

/**
 * 对象导航注入器 默认情况下只有使用 @Param("::") 的情况下才调用这个注入器
 * <p/>
 * 毕竟它在接收到请求时进行注入,会有一定的性能损伤
 *
 * @author juqkai(juqkai@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class ObjectNavlPairInjector implements ParamInjector {
    protected Mirror<?> mirror;
    private String prefix;
    private Type type;

    public ObjectNavlPairInjector(String prefix, Type type) {
        prefix = Strings.isBlank(prefix) ? "" : Strings.trim(prefix);
        this.prefix = prefix;
        this.mirror = Mirror.me(type);
        this.type = type;
    }

    public Object get(    ServletContext sc,
                          HttpServletRequest req,
                          HttpServletResponse resp,
                          Object refer) {
        String pre = "";
        if ("".equals(prefix))
            pre = "node.";
        ParamExtractor pe = Params.makeParamExtractor(req, refer);
        Map<String , Object> map = new HashMap<String, Object>();
        for (Object name : pe.keys()) {
            String na = (String) name;
            if (na.startsWith(prefix)) {
            	String[] vals = pe.extractor(na);
            	na = na.replace(":", ".");
            	na = na.replace(")", "");
            	na = na.replace("(", ".");
            	if(vals.length == 1){
            		Mapl.put(map, pre + na, vals[0]);
            		continue;
            	}
            	if(na.indexOf('[') < 0){
            		for(int i = 0; i < vals.length; i ++){
            			Mapl.put(map, pre + na + "["+i+"]", vals[i]);
            		}
            		continue;
            	}
            	for(int i = 0; i < vals.length; i ++){
            		String t = na.replace("[]", "["+i+"]");
        			Mapl.put(map, pre + t, vals[i]);
        		}
            }
        }
        if ("".equals(prefix)){
            return Mapl.maplistToObj(map.get("node"), type);
        } else {
            return Mapl.maplistToObj(map.get(prefix.substring(0, prefix.indexOf('.') == -1 ? prefix.length() : prefix.indexOf('.'))), type);
        }
    }

}
