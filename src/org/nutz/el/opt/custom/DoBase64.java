package org.nutz.el.opt.custom;

import java.util.List;

import org.nutz.el.opt.RunMethod;
import org.nutz.lang.Encoding;
import org.nutz.plugin.Plugin;
import org.nutz.repo.Base64;

/**
 * 用法   ${base64('abc')}  ${base64('decode', 'sfasdfsadfsa')}   若传入参数为null,则返回null
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class DoBase64 implements RunMethod, Plugin {

    public boolean canWork() {
        return true;
    }

    public Object run(List<Object> fetchParam) {
        if (fetchParam.isEmpty())
            return null;
        if (fetchParam.size() == 1) {
            return encode(fetchParam.get(0));
        }
        Object obj = fetchParam.get(1);
        if (obj == null)
            return null;
        if ("decode".equals(fetchParam.get(0))) {
            return new String(Base64.decode(String.valueOf(obj).getBytes(Encoding.CHARSET_UTF8)), Encoding.CHARSET_UTF8);
        } else {
            return encode(obj);
        }
    }
    
    public String encode(Object obj) {
        if (obj == null)
            return null;
        return Base64.encodeToString(String.valueOf(obj).getBytes(Encoding.CHARSET_UTF8), false);
    }

    public String fetchSelf() {
        return "base64";
    }

}
