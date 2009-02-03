package com.zzh.json;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.zzh.castor.Castors;
import com.zzh.lang.Lang;
import com.zzh.lang.Mirror;
import com.zzh.lang.Strings;

class JsonParsing {
	private Castors castors;

	JsonParsing(Castors castors) {
		this.castors = castors;
	}

	private int cursor;

	/**
	 * A start of a json string could be '[' '{' '"' [0-9] t | f
	 * 
	 * 
	 * @param ins
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	<T> T parseFromJson(InputStream ins, Class<T> type) {
		Mirror<T> me = Mirror.me(type);
		try {
			cursor = ins.read();
			// skip the start blank
			while (cursor >= 0 && cursor <= 32)
				cursor = ins.read();
			switch (cursor) {
			/*
			 * Array, the T should indicate the inside type of arrays
			 */
			case '[':
				boolean reurnAsList = false;
				List list = null;
				if (null != type && List.class.isAssignableFrom(type)) {
					reurnAsList = true;
					if (me.is(List.class))
						list = new LinkedList();
					else
						list = (List) me.born();
				} else if (null != type && !type.isArray()) {
					throw new RuntimeException(String.format(
							"type can NOT '%s', it should be a Array or List!!!", type.getName()));
				} else {
					list = new LinkedList();
				}
				do {
					Object o = parseFromJson(ins, null);
					list.add(o);
					// find next element
					while (cursor != -1 && cursor != ',' && cursor != ']') {
						cursor = ins.read();
					}
				} while (cursor != -1 && cursor != ']');
				if (reurnAsList)
					return (T) list;
				Class<?> componentType = null == type ? Object.class : type.getComponentType();
				Object ary = Array.newInstance(componentType, list.size());
				int i = 0;
				for (Iterator it = list.iterator(); it.hasNext();)
					Array.set(ary, i++, castors.castTo(it.next(), componentType));
				return (T) ary;
				// Object or Map
			case '{':
				/*
				 * For Map
				 */
				if (null == me || Map.class.isAssignableFrom(type)) {
					Map<String, Object> map = null == me ? new HashMap<String, Object>()
							: (Map<String, Object>) me.born();
					do {
						String name = readFieldName(ins);
						Object value = parseFromJson(ins, null);
						map.put(name, value);
						// find next pair begin, if } break
						if (cursor == '}') {
							break;
						}
						if (cursor != ',')
							do {
								cursor = ins.read();
							} while (cursor != '}' && cursor != ',');
					} while (cursor != -1 && cursor != '}');
					cursor = ins.read();
					return (T) map;
				}
				/*
				 * For Object
				 */
				T obj = me.born();
				do {
					String name = readFieldName(ins);
					Field f = me.getField(name);
					Object value = parseFromJson(ins, f.getType());
					me.setValue(obj, f, value);
					// find next pair begin, if } break
					if (cursor == '}') {
						break;
					}
					if (cursor != ',')
						do {
							cursor = ins.read();
						} while (cursor != -1 && cursor != '}' && cursor != ',');
				} while (cursor != -1 && cursor != '}');
				cursor = ins.read();
				return obj;
				/*
				 * For String
				 */
			case 'n':
				if ('u' != (char) ins.read() && 'l' != (char) ins.read()
						&& 'l' != (char) ins.read())
					throw new RuntimeException("String must in quote or it must be <null>");
				return null;
			case '\'':
			case '"':
				StringBuilder vs = readString(ins);
				String value = vs.toString();
				if (null == me || me.is(String.class))
					return (T) value;
				return castors.castTo(value, me.getMyClass());
				/*
				 * For true or false
				 */
			case 't': // true
				if ('u' != (char) ins.read() && 'r' != (char) ins.read()
						&& 'e' != (char) ins.read())
					throw new RuntimeException("Expect boolean as input!");
				if (null != me && !Mirror.me(type).isBoolean())
					throw new RuntimeException("Expect boolean|Boolean as type!");
				return (T) Boolean.valueOf(true);
			case 'f': // false
				if ('a' != (char) ins.read() && 'l' != (char) ins.read()
						&& 's' != (char) ins.read() && 'e' != (char) ins.read())
					throw new RuntimeException("Expect boolean as input!");
				if (null != me && !Mirror.me(type).isBoolean())
					throw new RuntimeException("Expect boolean|Boolean as type!");
				return (T) Boolean.valueOf(false);
				/*
				 * For number
				 */
			case '.':
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
					cursor = ins.read();
				} while (cursor != ' ' && cursor != -1 && cursor != ',' && cursor != ']'
						&& cursor != '}');
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
					throw new RuntimeException("type must by one of int|long|float|dobule|byte");
				}
			default:
				throw new RuntimeException("Wrong syntax of JSON string!");
			}
		} catch (Throwable e) {
			if (e instanceof RuntimeException) {
				throw (RuntimeException) e;
			}
			throw Lang.wrapThrow(e);
		}
	}

	private String readFieldName(InputStream ins) throws IOException {
		StringBuilder sb = new StringBuilder();
		cursor = ins.read();
		while (cursor != -1 && cursor != ':') {
			sb.append((char) cursor);
			cursor = ins.read();
		}
		char[] name = Strings.trim(sb).toCharArray();
		int offset = 0;
		int count = name.length;
		if (name[0] == '\"') {
			if (name[--count] != '"')
				throw new RuntimeException(String.format("Error field name [%s]", new String(name)));
			offset++;
			count--;
		} else if (name[0] == '\'') {
			if (name[--count] != '\'')
				throw new RuntimeException(String.format("Error field name [%s]", new String(name)));
			offset++;
			count--;
		}
		return String.valueOf(name, offset, count);
	}

	private StringBuilder readString(InputStream ins) throws IOException {
		StringBuilder sb = new StringBuilder();
		int expEnd = cursor;
		cursor = ins.read();
		while (cursor != -1 && cursor != expEnd) {
			if (cursor == '\\') {
				cursor = ins.read();
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
						hex[i] = (char) ins.read();
					cursor = Integer.valueOf(new String(hex), 16);
					break;
				case 'b':
					throw new RuntimeException("don't support \\b");
				case 'f':
					throw new RuntimeException("don't support \\f");
				}
			}
			sb.append((char) cursor);
			cursor = ins.read();
		}
		return sb;
	}

}
