package org.nutz.json.handler;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.json.JsonRender;
import org.nutz.json.JsonTypeHandler;
import org.nutz.json.entity.JsonCallback;
import org.nutz.json.entity.JsonEntity;
import org.nutz.json.entity.JsonEntityField;
import org.nutz.json.impl.JsonPair;
import org.nutz.lang.FailToGetValueException;
import org.nutz.lang.Mirror;

/**
 * 
 * @author wendal
 *
 */
public class JsonPojoHandler extends JsonTypeHandler {

    public boolean supportFromJson(Mirror<?> mirror, Object obj) {
        return false;
    }

    public boolean supportToJson(Mirror<?> mirror, Object obj, JsonFormat jf) {
        return true;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void toJson(Mirror<?> _mirror, Object obj, JsonRender r, JsonFormat format) throws IOException {
        if (null == obj)
            return;
        /*
         * Default
         */
        Class<?> type = obj.getClass();
        if (type == JsonPojoHandler.class) {
            return;
        }
        JsonEntity jen = Json.getEntity(Mirror.me(type));
        JsonCallback jsonCallback = jen.getJsonCallback();
        if (jsonCallback != null) {
            if (jsonCallback.toJson(obj, format, r.getWriter()))
                return;
        }
        List<JsonEntityField> fields = jen.getFields();
        r.appendBraceBegin();
        r.increaseFormatIndent();
        ArrayList<JsonPair> list = new ArrayList<JsonPair>(fields.size());
        for (JsonEntityField jef : fields) {
            if (jef.isIgnore())
                continue;
            String name = jef.getName();
            try {
                Object value = jef.getValue(obj);
                // 判断是否应该被忽略
                if (r.isIgnore(name, value))
                    continue;
                Mirror mirror = jef.getMirror();
                // 以前曾经输出过 ...
                if (null != value) {
                    // zozoh: 循环引用的默认行为，应该为 null，以便和其他语言交换数据
                    if (mirror.isPojo()) {
                        if (r.memoContains(value))
                            value = null;
                    }
                }
                if (null == value) {
                    // 处理各种类型的空值
                    if (mirror != null) {
                        if (mirror.isStringLike()) {
                            if (format.isNullStringAsEmpty())
                                value = "";
                        } else if (mirror.isNumber()) {
                            if (format.isNullNumberAsZero())
                                value = 0;
                        } else if (mirror.isCollection()) {
                            if (format.isNullListAsEmpty())
                                value = Collections.EMPTY_LIST;
                        } else if (jef.getGenericType() == Boolean.class) {
                            if (format.isNullBooleanAsFalse())
                                value = false;
                        }
                    }
                } else {
                    // 如果是强制输出为字符串的
                    if (jef.isForceString()) {
                        // 数组
                        if (value.getClass().isArray()) {
                            String[] ss = new String[Array.getLength(value)];
                            for (int i = 0; i < ss.length; i++) {
                                ss[i] = Array.get(value, i).toString();
                            }
                            value = ss;
                        }
                        // 集合
                        else if (value instanceof Collection) {
                            Collection col = (Collection) Mirror.me(value).born();
                            for (Object ele : (Collection) value) {
                                col.add(ele.toString());
                            }
                            value = col;
                        }
                        // 其他统统变字符串
                        else {
                            value = r.value2string(jef, value);
                        }
                    } else if (jef.hasDataFormat() && value instanceof Date) {
                        value = jef.getDataFormat().format(value);
                    } else if (jef.hasDataFormat() && (mirror != null && mirror.isNumber())) {
                        value = jef.getDataFormat().format(value);
                    }
                }

                // 加入输出列表 ...
                list.add(new JsonPair(name, value));
            }
            catch (FailToGetValueException e) {}
        }
        r.writeItem(list);
    }

    public Object fromJson(Object obj, Mirror<?> mirror) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean shallCheckMemo() {
        return true;
    }
}
