package org.nutz.mapl.impl.convert;

import java.io.IOException;
import java.io.Writer;

import org.nutz.json.JsonException;
import org.nutz.json.JsonFormat;
import org.nutz.json.JsonRender;
import org.nutz.json.impl.JsonRenderImpl;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.stream.StringWriter;
import org.nutz.mapl.MaplConvert;

/**
 * 将MapList转换成Json
 * 
 * @author juqkai(juqkai@gmail.com)
 */
public class JsonConvertImpl implements MaplConvert {
    private static Class<? extends JsonRender> jsonRenderCls;

    public static Class<? extends JsonRender> getJsonRenderCls() {
        return jsonRenderCls;
    }

    public static void setJsonRenderCls(Class<? extends JsonRender> cls) {
        jsonRenderCls = cls;
    }

    private JsonFormat format = null;

    public JsonConvertImpl() {
        format = new JsonFormat();
    }

    public JsonConvertImpl(JsonFormat format) {
        this.format = format;
    }

    public Object convert(Object obj) {
        StringBuilder sb = new StringBuilder();
        Writer writer = new StringWriter(sb);
        try {
            Class<? extends JsonRender> jrCls = getJsonRenderCls();
            if (jrCls == null)
                jrCls = JsonRenderImpl.class;

            JsonRender jr = Mirror.me(jrCls).born();
            jr.setWriter(writer);
            jr.setFormat(format);
            jr.render(obj);

            writer.flush();
            return sb.toString();
        }
        catch (IOException e) {
            throw Lang.wrapThrow(e, JsonException.class);
        }
    }
}
