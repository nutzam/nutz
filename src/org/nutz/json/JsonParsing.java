package org.nutz.json;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.nutz.castor.Castors;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;

@SuppressWarnings("unchecked")
class JsonParsing {

	JsonParsing(Reader reader) {
		this.reader = reader;
		col = 0;
		row = 1;
	}

	private int cursor;
	private Reader reader;
	private int col;
	private int row;

	private int nextChar() throws IOException {
		if (-1 == cursor)
			return -1;
		try {
			cursor = reader.read();
			if (cursor == '\n') {
				row++;
				col = 0;
			} else
				col++;
		}
		catch (Exception e) {
			cursor = -1;
		}
		return cursor;
	}

	private void skipBlank() throws IOException {
		while (cursor >= 0 && cursor <= 32)
			nextChar();
	}

	private void skipBlockComment() throws IOException {
		nextChar();
		while (cursor != -1) {
			if (cursor == '*') {
				if (nextChar() == '/')
					break;
			} else
				nextChar();
		}
	}

	private void skipInlineComment() throws IOException {
		while (nextChar() != -1 && cursor != '\n') {}
	}

	private void skipCommentsAndBlank() throws IOException {
		skipBlank();
		while (cursor == '/') {
			nextChar();
			if (cursor == '/') { // inline comment
				skipInlineComment();
				nextChar();
			} else if (cursor == '*') { // block comment
				skipBlockComment();
				nextChar();
			} else {
				throw makeError("Error comment syntax!");
			}
			skipBlank();
		}
	}

	private boolean findNextNamePair() throws IOException {
		skipCommentsAndBlank();
		if (cursor == '}')
			return false;
		if (cursor != ',')
			throw makeError("Wrong char between name-value pair!");
		nextChar();
		skipCommentsAndBlank();
		return true;
	}

	Object parseFromJson(Type type) {
		try {
			nextChar();
			skipCommentsAndBlank();
			return parseFromCurrentLocation(type);
		}
		catch (JsonException e) {
			throw e;
		}
		catch (Exception e) {
			throw makeError(e.getMessage(), e);
		}
	}

	private JsonException makeError(String message) {
		return new JsonException(row, col, (char) cursor, message);
	}

	private JsonException makeError(String message, Throwable tx) {
		return new JsonException(row, col, (char) cursor, message, tx);
	}

	private String readFieldName() throws IOException {
		if (cursor != '"' && cursor != '\'') {
			StringBuilder sb = new StringBuilder();
			while (cursor != -1 && cursor != ':') {
				sb.append((char) cursor);
				nextChar();
			}
			return Strings.trim(sb);
		}
		String re = readString().toString();
		while (cursor != -1 && cursor != ':')
			nextChar();
		return re;
	}

	private StringBuilder readString() throws IOException {
		StringBuilder sb = new StringBuilder();
		int expEnd = cursor;
		nextChar();
		while (cursor != -1 && cursor != expEnd) {
			if (cursor == '\\') {
				nextChar();
				switch (cursor) {
				case 'n':
					cursor = 10;
					break;
				case 'r':
					cursor = 13;
					break;
				case 't':
					cursor = 9;
					break;
				case 'u':
					char[] hex = new char[4];
					for (int i = 0; i < 4; i++)
						hex[i] = (char) nextChar();
					cursor = Integer.valueOf(new String(hex), 16);
					break;
				case 'b':
					throw makeError("don't support \\b");
				case 'f':
					throw makeError("don't support \\f");
				}
			}
			sb.append((char) cursor);
			nextChar();
		}
		if (cursor == -1)
			throw makeError("Unclose string");
		nextChar();
		return sb;
	}

	private Object parseFromCurrentLocation(Type type) throws Exception {
		Class<?> clazz = Lang.getTypeClass(type);
		ParameterizedType pt = null;
		if (type instanceof ParameterizedType) {
			pt = (ParameterizedType) type;
			clazz = (Class<?>) pt.getRawType();
		}
		Mirror<?> me = Mirror.me(clazz);

		switch (cursor) {
		case -1:
			return null;
		case '[':
			return parseArray(me, pt);
		case '{':
			return parseObj(me, pt);
		case 'u':
			return parseUndefined();
		case 'n':
			return parseNull();
		case '\'': // For String
		case '"':
			return parseString(me);
		case 't': // true
			return parseTrue(me);
		case 'f': // false
			return parseFalse(me);
		case '.': // For number
		case '-':
		case '0':
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		case '7':
		case '8':
		case '9':
			return parseNumber(me);
		case 'v':
			/*
			 * Meet the var ioc ={ maybe, try to find the '{' and break
			 */
			while (-1 != nextChar())
				if ('{' == cursor)
					return parseFromCurrentLocation(type);
		default:
			throw makeError("Don't know how to handle this char");
		}
	}

	@SuppressWarnings("rawtypes")
	private <T> T parseArray(Mirror<T> me, ParameterizedType type) throws Exception {
		Type tt = null;
		boolean reurnAsList = true;
		List list = null;
		/*
		 * The type must be null, T[] or subclass of List
		 */
		if (null == me) {
			list = new LinkedList();
		} else if (me.getType().isArray()) {
			list = new LinkedList();
			reurnAsList = false;
			tt = me.getType().getComponentType();

		} else if (List.class.isAssignableFrom(me.getType())) {
			reurnAsList = true;
			if (me.is(List.class))
				list = new LinkedList();
			else
				list = (List) me.born();
			if (type != null && type.getActualTypeArguments() != null)
				tt = type.getActualTypeArguments()[0];
		} else {
			throw makeError(String.format(	"Unexpect type '%s', it should be an Array or List!!!",
											me.getType().getName()));
		}
		nextChar();
		skipCommentsAndBlank();
		while (cursor != -1 && cursor != ']') {
			Object o = parseFromCurrentLocation(tt);
			list.add(o);
			skipCommentsAndBlank();
			if (cursor == ']')
				break;
			if (cursor != ',')
				throw makeError("Wrong char between elements!");
			nextChar();
			skipCommentsAndBlank();
		}
		nextChar();
		if (reurnAsList)
			return (T) list;
		Object ary = Array.newInstance((Class<?>) tt, list.size());
		int i = 0;
		for (Iterator it = list.iterator(); it.hasNext();)
			Array.set(ary, i++, Castors.me().castTo(it.next(), (Class<?>) tt));
		return (T) ary;
	}

	private <T> T parseObj(Mirror<T> me, ParameterizedType type) throws IOException {
		// It must be Object or Map
		nextChar();
		skipCommentsAndBlank();
		// If Map
		if (me != null && Map.class == me.getType())
			me = (Mirror<T>) Mirror.me(TreeMap.class);
		if (null == me || Map.class == me.getType() || Map.class.isAssignableFrom(me.getType())) {
			Map<String, Object> map = null == me ? new TreeMap<String, Object>()
												: (Map<String, Object>) me.born();
			while (cursor != -1 && cursor != '}') {
				String name = readFieldName();
				Object value = parseFromJson(type == null ? null : type.getActualTypeArguments()[1]);
				map.put(name, value);
				if (!findNextNamePair())
					break;
			}
			nextChar();
			return (T) map;
		}
		// If Object
		T obj = me.born();
		while (cursor != -1 && cursor != '}') {
			Field f = null;
			Type ft = null;
			try {
				f = me.getField(readFieldName());
				ft = f.getGenericType();
			}
			catch (NoSuchFieldException e) {}
			Object val = parseFromJson(ft);
			if (null != f) {
				me.setValue(obj, f, val);
			}
			if (!findNextNamePair())
				break;
		}
		nextChar();
		return obj;
	}

	private Object parseUndefined() throws IOException {
		// For undefined
		if ('n' != (char) nextChar()
			& 'd' != (char) nextChar()
			& 'e' != (char) nextChar()
			& 'f' != (char) nextChar()
			& 'i' != (char) nextChar()
			& 'n' != (char) nextChar()
			& 'e' != (char) nextChar()
			& 'd' != (char) nextChar())
			throw makeError("String must in quote or it must be <undefined>");
		nextChar();
		return null;
	}

	private <T> T parseNull() throws IOException {
		// For NULL
		if ('u' != (char) nextChar() & 'l' != (char) nextChar() & 'l' != (char) nextChar())
			throw makeError("String must in quote or it must be <null>");
		nextChar();
		return null;
	}

	private <T> T parseString(Mirror<T> me) throws IOException {
		StringBuilder vs = readString();
		String value = vs.toString();
		if (null == me || me.is(String.class))
			return (T) value;
		return Castors.me().castTo(value, me.getType());
	}

	private <T> T parseTrue(Mirror<T> me) throws IOException {
		if ('r' != (char) nextChar() | 'u' != (char) nextChar() | 'e' != (char) nextChar())
			throw makeError("Expect boolean as input!");
		if (null != me && !me.isBoolean())
			throw makeError("Expect boolean|Boolean as type!");
		nextChar();
		return (T) Boolean.valueOf(true);
	}

	private <T> T parseFalse(Mirror<T> me) throws IOException {
		if ('a' != (char) nextChar()
			| 'l' != (char) nextChar()
			| 's' != (char) nextChar()
			| 'e' != (char) nextChar())
			throw makeError("Expect boolean as input!");
		if (null != me && !me.isBoolean())
			throw makeError("Expect boolean|Boolean as type!");
		nextChar();
		return (T) Boolean.valueOf(false);
	}

	private <T> T parseNumber(Mirror<T> me) throws IOException {
		StringBuilder sb = new StringBuilder();
		do {
			sb.append((char) cursor);
			nextChar();
		} while (cursor != ' '
					&& cursor != -1
					&& cursor != ','
					&& cursor != ']'
					&& cursor != '}'
					&& cursor != '/');
		String numValue = Strings.trim(sb);

		// try actually return type
		if (null != me) {
			if (me.isInt()) {
				return (T) Integer.valueOf(numValue);
			} else if (me.isLong()) {
				return (T) Long.valueOf(numValue);
			} else if (me.isFloat()) {
				return (T) Float.valueOf(numValue);
			} else if (me.isDouble()) {
				return (T) Double.valueOf(numValue);
			} else if (me.isByte()) {
				return (T) Byte.valueOf(numValue);
			}
		}
		// guess the return type
		if (null == me || me.isNumber() || me.is(Object.class)) {
			char lastChar = Character.toUpperCase(numValue.charAt(numValue.length() - 1));
			if (numValue.indexOf('.') >= 0) {
				if (lastChar == 'F')
					return (T) Float.valueOf(numValue.substring(0, numValue.length() - 1));
				else{
					return (T)Double.valueOf(numValue);
				}
			} else {
				if (lastChar == 'L')
					return (T) Long.valueOf(numValue.substring(0, numValue.length() - 1));
				else{
					Long value = Long.valueOf(numValue);
					if (Integer.MIN_VALUE < value && value < Integer.MAX_VALUE)
						return (T) Integer.valueOf(value.intValue());
					else
						return (T) value;
					}
			}
		}
		// Unknown case...
		throw makeError("type must by one of int|long|float|dobule|byte");
	}

}
