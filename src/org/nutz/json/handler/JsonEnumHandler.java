package org.nutz.json.handler;

import java.io.IOException;
import java.util.Map;

import org.nutz.json.JsonFormat;
import org.nutz.json.JsonRender;
import org.nutz.json.JsonShape;
import org.nutz.json.JsonTypeHandler;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.util.NutMap;

/**
 * 
 * @author wendal
 *
 */
public class JsonEnumHandler extends JsonTypeHandler {

	public boolean supportFromJson(Mirror<?> mirror, Object obj) {
		return mirror.isEnum();
	}

	public boolean supportToJson(Mirror<?> mirror, Object obj, JsonFormat jf) {
		return mirror.isEnum();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void toJson(Mirror<?> mirror, Object currentObj, JsonRender r, JsonFormat jf) throws IOException {
		Mirror mr = mirror;
		// 枚举
		if (mr.isEnum()) {
			JsonShape shape = Mirror.getAnnotationDeep(mr.getType(), JsonShape.class);
			if (shape == null || jf.isIgnoreJsonShape()) {
				r.string2Json(((Enum) currentObj).name());
			} else {
				NutMap map;
				switch (shape.value()) {
				case ORDINAL:
					r.writeRaw(String.valueOf(((Enum) currentObj).ordinal()));
					break;
				case OBJECT:
					map = Lang.obj2nutmap(currentObj);
					if (map.isEmpty()) {
						r.string2Json(((Enum) currentObj).name());
					} else {
						r.map2Json(map);
					}
					break;
				case OBJECTWITHNAME:
					map = Lang.obj2nutmap(currentObj);
					map.setv(shape.nameKey(), ((Enum) currentObj).name());
					r.map2Json(map);
					break;
				default:
					r.string2Json(((Enum) currentObj).name());
					break;
				}
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Object fromJson(Object obj, Mirror<?> mirror) throws Exception {
		String name;
		if (obj instanceof Map) {
			name = (String) ((Map) obj).get("name");
		} else
			name = String.valueOf(obj);
		return Enum.valueOf((Class) mirror.getType(), name);
	}
}
