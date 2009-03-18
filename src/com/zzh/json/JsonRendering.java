package com.zzh.json;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import com.zzh.castor.Castors;
import com.zzh.lang.FailToGetValueException;
import com.zzh.lang.Mirror;
import com.zzh.lang.Strings;

class JsonRendering {
	private static String NL = "\n";

	private Collection<Object> memo;

	private Writer writer;

	JsonRendering(Writer writer, JsonFormat format) {
		this.format = format;
		this.writer = writer;
		// TODO make a new faster collection implementation
		memo = new HashSet<Object>();
	}

	private JsonFormat format;

	private static boolean isCompact(JsonRendering render) {
		if (null == render.format)
			return true;
		return render.format.isCompact();
	}

	private void appendName(String name) throws IOException {
		if (!isCompact(this) && format.notNeedQuoteName)
			writer.append(name);
		else
			string2Json(name);
	}

	private void appendPairBegin() throws IOException {
		if (!isCompact(this))
			writer.append(NL).append(Strings.dup(format.indentBy, format.indent));
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
		if (null == value)
			if (null == format)
				return true;
			else if (format.ignoreNull)
				return true;
		if (null != format && format.ignore(name))
			return true;
		return false;
	}

	private void appendPairEnd() throws IOException {
		writer.append(',');
	}

	private void appendBraceBegin() throws IOException {
		writer.append("{");
	}

	private void appendBraceEnd() throws IOException {
		if (!isCompact(this))
			writer.append(NL).append(Strings.dup(format.indentBy, format.indent));
		writer.append("}");
	}

	static class Pair {

		public Pair(String name, Object value) {
			this.name = name;
			this.value = value;
		}

		String name;
		Object value;
	}

	@SuppressWarnings("unchecked")
	private void map2Json(Map map) throws IOException {
		if (null == map)
			return;
		appendBraceBegin();
		increaseFormatIndent();
		ArrayList<Pair> list = new ArrayList<Pair>(map.size());
		for (Iterator it = map.keySet().iterator(); it.hasNext();) {
			String name = it.next().toString();
			Object value = map.get(name);
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
		Mirror<?> me = Mirror.me(obj.getClass());
		Field[] fields = me.getFields();
		appendBraceBegin();
		increaseFormatIndent();
		ArrayList<Pair> list = new ArrayList<Pair>(fields.length);
		for (Field f : fields) {
			String name = f.getName();
			try {
				Object value = me.getValue(obj, f);
				if (!this.isIgnore(name, value))
					list.add(new Pair(name, value));
			} catch (FailToGetValueException e) {}
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
			format.indent--;
	}

	private void increaseFormatIndent() {
		if (!isCompact(this))
			format.indent++;
	}

	private void string2Json(String s) throws IOException {
		if (null == s)
			writer.append("null");
		else
			writer.append("\"").append(s.replaceAll("\"|\\\\", "\\\\$0")).append('"');
	}

	@SuppressWarnings("unchecked")
	void render(Object obj) throws IOException {
		if (null == obj) {
			writer.write("null");
		} else {
			Mirror mr = Mirror.me(obj.getClass());
			if (mr.isEnum()) {
				string2Json(((Enum) obj).name());
			} else if (mr.isNumber() || mr.isBoolean() || mr.isChar()) {
				writer.append(obj.toString());
			} else if (mr.isStringLike()) {
				string2Json(obj.toString());
			} else if (mr.isDateTimeLike()) {
				string2Json(Castors.me().castToString(obj));
			} else if (memo.contains(obj)) {
				writer.append("null");
			} else {
				memo.add(obj);
				if (obj instanceof Map)
					map2Json((Map) obj);
				else if (obj instanceof Collection)
					coll2Json((Collection) obj);
				else if (obj.getClass().isArray())
					array2Json(obj);
				else
					pojo2Json(obj);
			}
		}
	}

	private void array2Json(Object obj) throws IOException {
		writer.append('[');
		int len = Array.getLength(obj) - 1;
		int i;
		for (i = 0; i < len; i++) {
			render(Array.get(obj, i));
			writer.append(',').append(' ');
		}
		render(Array.get(obj, i));
		writer.append(']');
	}

	private void coll2Json(Collection<?> obj) throws IOException {
		writer.append('[');
		for (Iterator<?> it = obj.iterator(); it.hasNext();) {
			render(it.next());
			if (it.hasNext())
				writer.append(',').append(' ');
		}
		writer.append(']');
	}
}
