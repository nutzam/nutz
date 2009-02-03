package com.zzh.json;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import com.zzh.castor.Castors;
import com.zzh.lang.Mirror;
import com.zzh.lang.Strings;

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
		sb.append(convert(value));
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
		if (null == obj)
			return "null";
		if (obj instanceof Map)
			return map2Json((Map) obj);
		else if (obj instanceof Collection)
			return coll2Json((Collection) obj);
		else if (obj.getClass().isArray())
			return array2Json(obj);
		else {
			Mirror mr = Mirror.me(obj.getClass());
			if (mr.isNumber() || mr.isBoolean()) {
				return obj.toString();
			} else if (mr.isStringLike()) {
				return string2Json(obj.toString());
			} else if (mr.isDateTimeLike()) {
				return string2Json(castors.castToString(obj));
			}
		}
		return pojo2Json(obj);
	}

	private CharSequence array2Json(Object obj) {
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		int len = Array.getLength(obj) - 1;
		int i;
		for (i = 0; i < len; i++) {
			sb.append(convert(Array.get(obj, i))).append(',').append(' ');
		}
		sb.append(convert(Array.get(obj, i)));
		sb.append(']');
		return sb;
	}

	private CharSequence coll2Json(Collection<?> obj) {
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		for (Iterator<?> it = obj.iterator(); it.hasNext();) {
			sb.append(convert(it.next()));
			if (it.hasNext())
				sb.append(',').append(' ');
		}
		sb.append(']');
		return sb;
	}
}
