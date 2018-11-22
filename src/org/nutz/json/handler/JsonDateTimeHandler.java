package org.nutz.json.handler;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import org.nutz.castor.Castors;
import org.nutz.json.JsonFormat;
import org.nutz.json.JsonRender;
import org.nutz.json.JsonTypeHandler;
import org.nutz.lang.Mirror;

/**
 * 
 * @author wendal
 *
 */
public class JsonDateTimeHandler extends JsonTypeHandler {

    public boolean supportFromJson(Mirror<?> mirror, Object obj) {
        return mirror.isDateTimeLike();
    }

    public boolean supportToJson(Mirror<?> mirror, Object obj, JsonFormat jf) {
        return mirror.isDateTimeLike();
    }

    @Override
    public void toJson(Mirror<?> mirror, Object currentObj, JsonRender r, JsonFormat jf) throws IOException {
        boolean flag = true;
        if (currentObj instanceof Date) {
            String _val = doDateFormat(jf, (Date) currentObj, null);
            if (_val != null) {
                r.string2Json(_val);
                flag = false;
            }
        }
        if (flag)
            r.string2Json(jf.getCastors().castToString(currentObj));
    }

    @Override
    public Object fromJson(Object obj, Mirror<?> mirror) throws Exception {
        return Castors.me().castTo(obj, mirror.getType());
    }

    protected String doDateFormat(JsonFormat format, Date date, DateFormat df) {
        if (df == null)
            df = format.getDateFormat();
        if (df != null) {
            if (format.getTimeZone() != null)
                df.setTimeZone(format.getTimeZone());
            return df.format(date);
        }
        return null;
    }
}
