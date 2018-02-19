package org.nutz.json.handler;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

import org.nutz.json.JsonFormat;
import org.nutz.json.JsonRender;
import org.nutz.json.JsonTypeHandler;
import org.nutz.lang.Mirror;

public class JsonLocalDateLikeHandler implements JsonTypeHandler {

    @Override
    public boolean supportFromJson(Type type) {
        return false;
    }

    @Override
    public boolean supportToJson(Mirror<?> mirror, Object obj, JsonFormat jf) {
        return mirror.isLocalDateTimeLike();
    }

    @Override
    public void toJson(Mirror<?> mirror, Object currentObj, JsonRender r, JsonFormat jf) throws IOException {
        String df = jf.getDateFormatRaw();
        if (df == null)
            df = "yyyy-MM-dd HH:mm:ss.SSS";
        Locale locale = null;
        String tmp = jf.getLocale();
        if (tmp != null)
            locale = Locale.forLanguageTag(tmp);
        else
            locale = Locale.getDefault();
        r.string2Json(DateTimeFormatter.ofPattern(df, locale).format((TemporalAccessor) currentObj));
    }

    @Override
    public Object fromJson(Object data, Type type) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean shallCheckMemo() {
        // TODO Auto-generated method stub
        return false;
    }

}
