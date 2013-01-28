package org.nutz.json.impl;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.nutz.json.JsonException;
import org.nutz.json.JsonParser;
import org.nutz.lang.Lang;
import org.nutz.mapl.MaplCompile;

/**
 * 将json理解为Map+List,以Token的方式读取,避免回溯等操作
 * 
 * @author wendal(wendal1985@gmail.com)
 * 
 */
public class JsonCompileImplV2 implements JsonParser, MaplCompile<Reader> {

	public Object parse(Reader reader) {
		return new JsonTokenScan(reader).read();
	}
}

final class JsonTokenScan {

	// private static final Log log = Logs.get();

	Reader reader;

	JsonToken token = new JsonToken();
	JsonToken nextToken = null;
	JsonToken nextToken2 = new JsonToken();

	static final Object END = new Object();
	static final Object COMMA = new Object();

	public JsonTokenScan(Reader reader) {
		this.reader = reader;
	}

	protected void _nextToken() {
		// System.out.println("_nextToken > " + (char) token.type);
		switch (token.type) {
		case MapStart:
		case MapEnd:
		case ListStart:
		case ListEnd:
		case MapPair:
		case Comma:
			return;
		case '\'':
			token.type = SimpleString;
			token.value = readString('\'');
			return;
		case '\"':
			token.type = SimpleString;
			token.value = readString('"');
			return;
		case ' ':
		case '\t':
		case '\n':
		case '\r':
			char c = 0;
			while (true) {
				c = nextChar();
				switch (c) {
				case ' ':
				case '\t':
				case '\n':
				case '\r':
					continue;
				}
				break;
			}
			token.type = c;
			_nextToken();
			return;
		case '/':
			// 看来是注释哦
			skipComment();
			nextToken();
			return;
		default:
			StringBuilder sb = new StringBuilder();
			sb.append((char) token.type);
			// 看来只是尝试找到结束字符了
			OUT: while (true) {
				c = nextChar();
				switch (c) {
				case MapStart:
				case MapEnd:
				case ListStart:
				case ListEnd:
				case MapPair:
				case Comma:
					nextToken = nextToken2;
					nextToken.type = c;
					// log.debug("Break OtherString token : " + (char) c);
					// log.debug("OtherString token : " + (char) token.type);
					break OUT;
				case ' ':
				case '\t':
				case '\r':
				case '\n':
					break OUT;
				case '/':
					skipComment();
					break OUT;
				}
				sb.append(c);
			}
			token.type = OtherString;
			token.value = sb.toString();
			// log.debug("OtherString Token > " + token.value);
			return;
		}
	}

	protected void nextToken() {
		if (nextToken != null) {
			token.type = nextToken.type;
			token.value = nextToken.value;
			nextToken = null;
			return;
		}
		token.type = nextChar();
		_nextToken();
		// log.debug("token: " + token);
	}

	protected void skipComment() {
		char c = nextChar();
		switch (c) {
		case '/': // 单行注释
			while (nextChar() != '\n') {}
			// nextToken();
			return;
		case '*':
			char c2 = c;
			while (true) {
				while ((c = nextChar()) != '/') {
					c2 = c;
				}
				if (c2 == '*')
					return;
			}
		default:
			throw unexpectChar(c);
		}
	}

	protected String readString(char endEnd) {
		StringBuilder sb = new StringBuilder();
		char c = 0;
		while ((c = nextChar()) != endEnd) {
			switch (c) {
			case '\\':
				c = parseSp();
				break;
			}
			sb.append(c);
		}
		return sb.toString();
	}

	protected Map<String, Object> readMap() {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		boolean hasComma = false;
		OUT: while (true) {
			nextToken();
			switch (token.type) {
			case MapEnd:
				break OUT;
			case SimpleString:
			case OtherString:
				String key = token.value;
				// log.debug("key=" + key + "      " + token);
				nextToken();
				if (token.type != MapPair) {
					throw unexpectChar((char)token.type);
				}
				Object obj = readObject(MapEnd);
				if (obj == COMMA) {
					if (hasComma)
						throw unexpectChar((char)Comma);
					hasComma = true;
					continue;
				}
				if (obj == END)
					throw unexpectChar((char)token.type);
				map.put(key, obj);
				hasComma = false;
				break;
			case Comma:
				continue;
			default:
				throw unexpectChar((char)token.type);
			}
		}
		return map;
	}

	protected List<Object> readList() {
		List<Object> list = new ArrayList<Object>();
		boolean hasComma = false;
		while (true) {
			Object obj = readObject(ListEnd);
			if (obj == END)
				break;
			if (obj == COMMA) {
				if (hasComma)
					throw unexpectChar((char)Comma);
				hasComma = true;
				continue;
			}
			list.add(obj);
			hasComma = false;
		}
		return list;
	}

	protected Object readObject(int endTag) {
		nextToken();
		// System.out.println(">>>> " + token.type + "    " + token);
		switch (token.type) {
		case MapStart:
			return readMap();
		case ListStart:
			return readList();
		case SimpleString:
			return token.value;
		case OtherString:
			String value = token.value;
			int len = value.length();
			if (len == 0)
				return "";
			switch (value.charAt(0)) {
			case 't':
				if ("true".equals(value))
					return true;
				break;
			case 'f':
				if ("false".equals(value))
					return false;
				break;
			case 'n':
				if ("null".endsWith(value))
					return null;
				break;
			case 'u':
				if ("undefined".endsWith(value))
					return null;
				break;
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
			case '.':
			case '-':
				// 看来是数字哦
				if (token.value.length() > 0) {
					switch (token.value.charAt(token.value.length() - 1)) {
					case 'l':
					case 'L':
						return Long.parseLong(token.value.substring(0, token.value.length() - 1));
					case 'f':
					case 'F':
						return Float.parseFloat(token.value.substring(0, token.value.length() - 1));
					default:
						if (token.value.contains("e") || token.value.contains("E")) {
							return new BigDecimal(token.value);
						}
						if (token.value.contains(".")) {
							return Double.parseDouble(token.value);
						}
					}
				}
				long n = Long.parseLong(token.value);
				if (Integer.MAX_VALUE >= n && n >= Integer.MIN_VALUE) {
					return (int) n;
				}
				return n;
			}
			throw new JsonException(row, col, value.charAt(0), "Unexpect String = " + value);
		default:
			if (token.type == endTag)
				return END;
			if (token.type == Comma)
				return COMMA;
			throw unexpectChar((char)token.type);
		}
	}

	public Object read() {
		int c = 0;
		boolean add = false;
		OUT: while (true) {
			c = readChar();
			switch (c) {
			case -1:
				return null;
			case ' ':
			case '\t':
			case '\n':
			case '\r':
				continue;
			case '/':
				skipComment();
				break;
			default:
				add = true;
				break OUT;
			}
		}

		switch (c) {
		case 'v':
			while (nextChar() != MapStart) {}
			return readMap();
		case MapStart:
			return readMap();
		case ListStart:
			return readList();
		case '\'':
		case '"':
			return readString((char) c);
		default:
			nextToken = nextToken2;
			nextToken.type = OtherString;
			if (add)
				nextToken.value = (char) c + Lang.readAll(reader);
			else
				nextToken.value = Lang.readAll(reader);
			//System.out.println("VVVVV>>>>>>>" + nextToken.value);
			return readObject(-1);
		}
	}

	char nextChar() {
		int c = readChar();
		// System.out.println("+++++++++++===>> " + (char) c);
		if (c == -1)
			throw new JsonException("Unexpect EOF");
		return (char) c;
	}

	protected char parseSp() {
		char c = nextChar();
		switch (c) {
		case 'n':
			return '\n';
		case 'r':
			return '\r';
		case 't':
			return '\t';
		case '\\':
			return '\\';
		case '\'':
			return '\'';
		case '\"':
			return '"';
		case '/':
			return '/';
		case 'u':
			char[] hex = new char[4];
			for (int i = 0; i < 4; i++)
				hex[i] = nextChar();
			return (char) Integer.valueOf(new String(hex), 16).intValue();
		case 'b': // 这个支持一下又何妨?
			return ' ';// 空格
		case 'f':
			return '\f';
		default:
			throw unexpectChar(c);
		}
	}

	int row = 1;
	int col = 0;

	private int readChar() {
		try {
			int c = reader.read();
			switch (c) {
			case -1:
				break;
			case '\n':
				row++;
				col = 0;
			default:
				col++;
				break;
			}
			return c;
		}
		catch (IOException e) {
			throw new JsonException(e);
		}
	}

	static final int MapStart = '{';
	static final int MapEnd = '}';
	static final int ListStart = '[';
	static final int ListEnd = ']';
	static final int MapPair = ':';
	static final int SimpleString = 0;
	static final int OtherString = 1;
	static final int Comma = ',';

	protected JsonException unexpectChar(char c) {
		return new JsonException(row, col, c, "Unexpect Char");
	}
}

class JsonToken {
	int type;
	String value;

	public String toString() {
		return "[" + (char) type + " " + value + "]" + hashCode();
	}
}
