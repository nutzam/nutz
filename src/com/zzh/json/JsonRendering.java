package com.zzh.json;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;

import com.zzh.lang.Mirror;
import com.zzh.lang.Strings;
import com.zzh.lang.types.Castors;

class JsonRendering {
	private static String NL = "\n";

	JsonRendering(JsonFormat format, Castors castors) {
		if (null == castors) {
			throw new RuntimeException("You can not assign null as castors for JsonConverting!!!");
		}
		this.format = format;
		this.castors = castors;
	}

	private JsonFormat format;
	private Castors castors;

	private static boolean isCompact(JsonRendering render) {
		if (null == render.format)
			return true;
		return render.format.isCompact();
	}

	private void appendName(StringBuilder sb, String name) {
		if (!isCompact(this) && format.notNeedQuoteName)
			sb.append(name);
		else
			sb.append(string2Json(name));
	}

	@SuppressWarnings("unchecked")
	private void appendValue(StringBuilder sb, Object value) {
		if (null == value) {
			sb.append("null");
			return;
		}
		if (value instanceof Map) {
			increaseFormatIndent();
			sb.append(map2Json((Map) value));
			decreaseFormatIndent();
		} else {
			Mirror mr = Mirror.me(value.getClass());
			if (mr.isNumber()) {
				sb.append(value.toString());
			} else if (mr.isBoolean()) {
				sb.append(value.toString());
			} else if (mr.isStringLike()) {
				sb.append(string2Json(value.toString()));
			} else if (mr.isDateTimeLike()) {
				sb.append(string2Json(castors.castToString(value)));
			} else {
				increaseFormatIndent();
				sb.append(pojo2Json(value));
				decreaseFormatIndent();
			}
		}
	}

	private void appendPairBegin(StringBuilder sb) {
		if (!isCompact(this))
			sb.append(NL).append(Strings.dup(format.indentBy, format.indent));
	}

	private void appendPairSep(StringBuilder sb) {
		sb.append(!isCompact(this) ? " :" : ":");
	}

	private void appendPair(StringBuilder sb, String name, Object value) {
		if (null == value)
			if (null == format)
				return;
			else if (format.ignoreNull)
				return;
		if (null != format && format.ignore(name))
			return;

		appendPairBegin(sb);
		appendName(sb, name);
		appendPairSep(sb);
		appendValue(sb, value);
		appendPairEnd(sb);
	}

	private void appendPairEnd(StringBuilder sb) {
		sb.append(',');
	}

	private void appendBraceBegin(StringBuilder sb) {
		sb.append("{");
	}

	private void appendBraceEnd(StringBuilder sb) {
		if (!isCompact(this))
			sb.append(NL).append(Strings.dup(format.indentBy, format.indent));
		sb.append("}");
	}

	@SuppressWarnings("unchecked")
	private StringBuilder map2Json(Map map) {
		if (null == map)
			return null;
		StringBuilder sb = new StringBuilder();
		appendBraceBegin(sb);
		increaseFormatIndent();
		for (Iterator it = map.keySet().iterator(); it.hasNext();) {
			String name = it.next().toString();
			Object value = map.get(name);
			appendPair(sb, name, value);
		}
		sb.deleteCharAt(sb.length() - 1);
		decreaseFormatIndent();
		appendBraceEnd(sb);
		return sb;
	}

	private StringBuilder pojo2Json(Object obj) {
		if (null == obj)
			return null;
		Mirror<?> me = Mirror.me(obj.getClass());
		Field[] fields = me.getFields();
		StringBuilder sb = new StringBuilder();
		appendBraceBegin(sb);
		increaseFormatIndent();
		for (Field f : fields) {
			String name = f.getName();
			Object value = me.getValue(obj, f);
			appendPair(sb, name, value);
		}
		sb.deleteCharAt(sb.length() - 1);
		decreaseFormatIndent();
		appendBraceEnd(sb);
		return sb;
	}

	private void decreaseFormatIndent() {
		if (!isCompact(this))
			format.indent--;
	}

	private void increaseFormatIndent() {
		if (!isCompact(this))
			format.indent++;
	}

	private StringBuilder string2Json(String s) {
		if (null == s)
			return new StringBuilder("null");
		return new StringBuilder("\"").append(s.replaceAll("\"|\\\\", "\\\\$0")).append('"');
	}

	@SuppressWarnings("unchecked")
	CharSequence convert(Object obj) {
		if (obj instanceof Map)
			return map2Json((Map) obj);
		else if (CharSequence.class.isAssignableFrom(obj.getClass()))
			return string2Json(obj.toString());
		return pojo2Json(obj);
	}
}
