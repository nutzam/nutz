package org.nutz.json;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.nutz.json.entity.JsonEntity;
import org.nutz.json.entity.JsonEntityField;
import org.nutz.lang.FailToGetValueException;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;

/**
 * @author zozoh(zozohtnt@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 * 
 */
public class JsonRendering {
	private static String NL = "\n";

	private HashMap<Object, Object> memo;

	private Writer writer;

	JsonRendering(Writer writer, JsonFormat format) {
		this.format = format;
		this.writer = writer;
		// TODO make a new faster collection
		// implementation
		memo = new HashMap<Object, Object>();
	}

	private JsonFormat format;

	private static boolean isCompact(JsonRendering render) {
		return render.format.isCompact();
	}

	private static final Pattern p = Pattern.compile("^[a-z_A-Z$]+[a-zA-Z_0-9$]*$");

	private void appendName(String name) throws IOException {
		if (format.isQuoteName() || !p.matcher(name).find())
			string2Json(name);
		else
			writer.append(name);
	}

	private void appendPairBegin() throws IOException {
		if (!isCompact(this))
			writer.append(NL).append(Strings.dup(format.getIndentBy(), format.getIndent()));
	}

	private void appendPairSep() throws IOException {
		writer.append(!isCompact(this) ? " :" : ":");
	}

	private void appendPair(String name, Object value) throws IOException {
		appendPairBegin();
		appendName(name);
		appendPairSep();
		render(value);
	}

	private boolean isIgnore(String name, Object value) {
		if (null == value && format.isIgnoreNull())
			return true;
		return format.ignore(name);
	}

	private void appendPairEnd() throws IOException {
		writer.append(',');
	}

	private void appendBraceBegin() throws IOException {
		writer.append('{');
	}

	private void appendBraceEnd() throws IOException {
		if (!isCompact(this))
			writer.append(NL).append(Strings.dup(format.getIndentBy(), format.getIndent()));
		writer.append('}');
	}

	static class Pair {

		public Pair(String name, Object value) {
			this.name = name;
			this.value = value;
		}

		String name;
		Object value;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private void map2Json(Map map) throws IOException {
		if (null == map)
			return;
		appendBraceBegin();
		increaseFormatIndent();
		ArrayList<Pair> list = new ArrayList<Pair>(map.size());
		Set<Entry<?, ?>> entrySet = map.entrySet();
		for (Entry entry : entrySet) {
			String name = null == entry.getKey() ? "null" : entry.getKey().toString();
			Object value = entry.getValue();
			if (!this.isIgnore(name, value))
				list.add(new Pair(name, value));
		}
		for (Iterator<Pair> it = list.iterator(); it.hasNext();) {
			Pair p = it.next();
			this.appendPair(p.name, p.value);
			if (it.hasNext())
				this.appendPairEnd();
		}
		decreaseFormatIndent();
		appendBraceEnd();
	}

	private void pojo2Json(Object obj) throws IOException {
		if (null == obj)
			return;
		Class<? extends Object> type = obj.getClass();
		ToJson tj = type.getAnnotation(ToJson.class);
		String myMethodName = Strings.sNull(null == tj ? null : tj.value(), "toJson");
		try {
			/*
			 * toJson()
			 */
			try {
				Method myMethod = type.getMethod(myMethodName);
				if (!myMethod.isAccessible())
					myMethod.setAccessible(true);
				Object re = myMethod.invoke(obj);
				writer.append(String.valueOf(re));
				return;
			}
			/*
			 * toJson(JsonFormat fmt)
			 */
			catch (NoSuchMethodException e1) {
				try {
					Method myMethod = type.getMethod(myMethodName, JsonFormat.class);
					if (!myMethod.isAccessible())
						myMethod.setAccessible(true);
					Object re = myMethod.invoke(obj, format);
					writer.append(String.valueOf(re));
					return;
				}
				catch (NoSuchMethodException e) {}
			}
		}
		catch (IllegalArgumentException e) {
			throw Lang.wrapThrow(e);
		}
		catch (IllegalAccessException e) {
			throw Lang.wrapThrow(e);
		}
		catch (InvocationTargetException e) {
			throw Lang.wrapThrow(e);
		}
		/*
		 * Default
		 */
		JsonEntity jen = Json.getEntity(type);
		List<JsonEntityField> fields = jen.getFields();
		appendBraceBegin();
		increaseFormatIndent();
		ArrayList<Pair> list = new ArrayList<Pair>(fields.size());
		for (JsonEntityField jef : fields) {
			String name = jef.getName();
			try {
				Object value = jef.getValue(obj);
				if (!this.isIgnore(name, value))
					list.add(new Pair(name, value));
			}
			catch (FailToGetValueException e) {}
		}
		for (Iterator<Pair> it = list.iterator(); it.hasNext();) {
			Pair p = it.next();
			this.appendPair(p.name, p.value);
			if (it.hasNext())
				this.appendPairEnd();
		}
		decreaseFormatIndent();
		appendBraceEnd();
	}

	private void decreaseFormatIndent() {
		if (!isCompact(this))
			format.decreaseIndent();
	}

	private void increaseFormatIndent() {
		if (!isCompact(this))
			format.increaseIndent();
	}

	private void string2Json(String s) throws IOException {
		if (null == s)
			writer.append("null");
		else {
			char[] cs = s.toCharArray();
			writer.append(format.getSeparator());
			for (char c : cs) {
				switch (c) {
				case '"':
					writer.append("\\\"");
					break;
				case '\n':
					writer.append("\\n");
					break;
				case '\t':
					writer.append("\\t");
					break;
				case '\r':
					writer.append("\\r");
					break;
				case '\\':
					writer.append("\\\\");
					break;
				default:
					if (c >= 256 && format.isAutoUnicode())
						writer.append("\\u").append(Integer.toHexString(c).toUpperCase());
					else
						writer.append(c);
				}
			}
			writer.append(format.getSeparator());
		}
	}

	@SuppressWarnings({"rawtypes"})
	public void render(Object obj) throws IOException {
		if (null == obj) {
			writer.write("null");
		} else if (obj instanceof Class) {
			string2Json(((Class<?>) obj).getName());
		} else if (obj instanceof Mirror) {
			string2Json(((Mirror<?>) obj).getType().getName());
		} else {
			Mirror mr = Mirror.me(obj.getClass());
			if (mr.isEnum()) {
				string2Json(((Enum) obj).name());
			} else if (mr.isNumber() || mr.isBoolean()) {
				writer.append(obj.toString());
			} else if (mr.isStringLike() || mr.isChar()) {
				string2Json(obj.toString());
			} else if (mr.isDateTimeLike()) {
				string2Json(format.getCastors().castToString(obj));
			} else if (memo.containsKey(obj)) {
				writer.append("null");
			} else {
				memo.put(obj, null);
				if (obj instanceof Map)
					map2Json((Map) obj);
				else if (obj instanceof Collection)
					coll2Json((Collection) obj);
				else if (obj.getClass().isArray())
					array2Json(obj);
				else {
					pojo2Json(obj);
				}
				memo.remove(obj);
			}
		}
	}

	private void array2Json(Object obj) throws IOException {
		writer.append('[');
		int len = Array.getLength(obj) - 1;
		if (len > -1) {
			int i;
			for (i = 0; i < len; i++) {
				render(Array.get(obj, i));
				writer.append(',').append(' ');
			}
			render(Array.get(obj, i));
		}
		writer.append(']');
	}

	private void coll2Json(Collection<?> obj) throws IOException {
		writer.append('[');
		for (Iterator<?> it = obj.iterator(); it.hasNext();) {
			render(it.next());
			if (it.hasNext())
				writer.append(',').append(' ');
			else
				break;
		}
		writer.append(']');
	}
}
