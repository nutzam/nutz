package org.nutz.json;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.nutz.castor.Castors;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;

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
		} catch (Exception e) {
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

	<T> T parseFromJson(Class<T> type) {
		try {
			nextChar();
			skipCommentsAndBlank();
			return parseFromCurrentLocation(type);
		} catch (JsonException e) {
			throw e;
		} catch (Exception e) {
			throw makeError(e.getMessage());
		}
	}

	/**
	 * @param <T>
	 * @param type
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private <T> T parseFromCurrentLocation(Class<T> type) throws Exception {
		Mirror<T> me = Mirror.me(type);
		switch (cursor) {
		case -1:
			return null;
		case '[':
			Class<?> compType = null;
			boolean reurnAsList = true;
			List list = null;
			/*
			 * The type must be null, T[] or subclass of List
			 */
			if (null == type) {
				list = new LinkedList();
			} else if (type.isArray()) {
				list = new LinkedList();
				reurnAsList = false;
				compType = type.getComponentType();
			} else if (List.class.isAssignableFrom(type)) {
				reurnAsList = true;
				if (me.is(List.class))
					list = new LinkedList();
				else
					list = (List) me.born();
			} else {
				throw makeError(String.format("type can NO '%s', it should be a Array or List!!!",
						type.getName()));
			}
			nextChar();
			skipCommentsAndBlank();
			while (cursor != -1 && cursor != ']') {
				Object o = parseFromCurrentLocation(compType);
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
			Object ary = Array.newInstance(compType, list.size());
			int i = 0;
			for (Iterator it = list.iterator(); it.hasNext();)
				Array.set(ary, i++, Castors.me().castTo(it.next(), compType));
			return (T) ary;
		case '{':
			// It must be Object or Map
			nextChar();
			skipCommentsAndBlank();
			// If Map
			if (Map.class == type)
				me = (Mirror<T>) Mirror.me(HashMap.class);
			if (null == me || Map.class.isAssignableFrom(type)) {
				Map<String, Object> map = null == me ? new TreeMap<String, Object>()
						: (Map<String, Object>) me.born();
				while (cursor != -1 && cursor != '}') {
					String name = readFieldName();
					Object value = parseFromJson(null);
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
				Field f = me.getField(readFieldName());
				Object value = parseFromJson(f.getType());
				me.setValue(obj, f, value);
				if (!findNextNamePair())
					break;
			}
			nextChar();
			return obj;
		case 'u':
			// For undefined
			if ('n' != (char) nextChar() & 'd' != (char) nextChar() & 'e' != (char) nextChar()
					& 'f' != (char) nextChar() & 'i' != (char) nextChar()
					& 'n' != (char) nextChar() & 'e' != (char) nextChar()
					& 'd' != (char) nextChar())
				throw makeError("String must in quote or it must be <null>");
			nextChar();
			return null;
		case 'n':
			// For NULL
			if ('u' != (char) nextChar() & 'l' != (char) nextChar() & 'l' != (char) nextChar())
				throw makeError("String must in quote or it must be <null>");
			nextChar();
			return null;
		case '\'': // For String
		case '"':
			StringBuilder vs = readString();
			String value = vs.toString();
			if (null == me || me.is(String.class))
				return (T) value;
			return Castors.me().castTo(value, me.getType());
		case 't': // true
			if ('u' != (char) nextChar() & 'r' != (char) nextChar() & 'e' != (char) nextChar())
				throw makeError("Expect boolean as input!");
			if (null != type && !Mirror.me(type).isBoolean())
				throw makeError("Expect boolean|Boolean as type!");
			nextChar();
			return (T) Boolean.valueOf(true);
		case 'f': // false
			if ('a' != (char) nextChar() & 'l' != (char) nextChar() & 's' != (char) nextChar()
					& 'e' != (char) nextChar())
				throw makeError("Expect boolean as input!");
			if (null != type && !Mirror.me(type).isBoolean())
				throw makeError("Expect boolean|Boolean as type!");
			nextChar();
			return (T) Boolean.valueOf(false);
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
			StringBuilder sb = new StringBuilder();
			do {
				sb.append((char) cursor);
				nextChar();
			} while (cursor != ' ' && cursor != -1 && cursor != ',' && cursor != ']'
					&& cursor != '}' && cursor != '/');
			String numValue = Strings.trim(sb);

			if (null == me) { // guess the return type
				char lastChar = Character.toUpperCase(numValue.charAt(numValue.length() - 1));
				if (numValue.indexOf('.') >= 0) {
					if (lastChar == 'F')
						return (T) Float.valueOf(numValue.substring(0, numValue.length() - 1));
					else
						return (T) Double.valueOf(numValue);
				} else {
					if (lastChar == 'L')
						return (T) Long.valueOf(numValue.substring(0, numValue.length() - 1));
					else
						return (T) Integer.valueOf(numValue);
				}
			}

			// try actually return type
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
			} else {
				throw makeError("type must by one of int|long|float|dobule|byte");
			}
		case 'v':
			/*
			 * If meet the "var ioc = {", try to treat it as "{". For the reason
			 * I have to use some JS editor to write JSON file. If without
			 * "var ioc = ", JS editor "auto format" function will not work.
			 */
			if ('a' == nextChar() && 'r' == nextChar() && ' ' == nextChar() && 'i' == nextChar()
					&& 'o' == nextChar() && 'c' == nextChar() && ' ' == nextChar()
					&& '=' == nextChar() && ' ' == nextChar() && '{' == nextChar())
				return parseFromCurrentLocation(type);
		default:
			throw makeError("Don't know how to handle this char");
		}
	}

	private JsonException makeError(String message) {
		return new JsonException(row, col, (char) cursor, message);
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

}
