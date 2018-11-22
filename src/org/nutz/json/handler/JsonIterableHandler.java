package org.nutz.json.handler;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import org.nutz.json.JsonFormat;
import org.nutz.json.JsonRender;
import org.nutz.json.JsonTypeHandler;
import org.nutz.lang.Mirror;

/**
 * 
 * @author wendal
 *
 */
public class JsonIterableHandler extends JsonTypeHandler {

    public boolean supportFromJson(Mirror<?> mirror, Object obj) {
        return false;
    }

    public boolean supportToJson(Mirror<?> mirror, Object obj, JsonFormat jf) {
        return obj instanceof Iterable;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void toJson(Mirror<?> mirror, Object currentObj, JsonRender r, JsonFormat jf) throws IOException {
        Writer writer = r.getWriter();
        Iterable iterable = (Iterable) currentObj;
        writer.append('[');
        for (Iterator<?> it = iterable.iterator(); it.hasNext();) {
            r.render(it.next());
            if (it.hasNext()) {
                r.appendPairEnd();
                writer.append(' ');
            } else
                break;
        }
        writer.append(']');
    }

    @Override
    public boolean shallCheckMemo() {
        return true;
    }
}
