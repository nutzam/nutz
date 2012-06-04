package org.nutz.json.impl;

import java.io.IOException;
import java.io.Reader;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.nutz.json.JsonException;
import org.nutz.json.JsonParser;
import org.nutz.lang.Lang;
import org.nutz.mapl.MaplCompile;

/**
 * 将json理解为Map+List
 * 
 * @author wendal
 *
 */
public class JsonCompileImpl implements JsonParser, MaplCompile<Reader> {
	
	private int cursor;
	private Reader reader;
	private int col;
	private int row;
	
	private static final int END = -1;
	
	private boolean skipOneChar = false;
	
	public Object parse(Reader reader) {
		if (reader == null)
			return null;
		this.reader = reader;
		try {
			
			//开始读取数据
			if(!tryNextChar())
				return null;
			skipCommentsAndBlank();
			if(cursor == 'v'){
				/*
				 * Meet the var ioc ={ maybe, try to find the '{' and break
				 */
				OUTER: while (true) {
					nextChar();//尝试找到{,以确定是否为"var ioc ={"格式
					switch (cursor) {
					case '{':
					//case '['://还真有人这样写
						break OUTER;
					}
				}
			}
			return parseFromHere();
		} catch (IOException e) {
			throw Lang.wrapThrow(e);
		}
	}
	
	protected Object parseFromHere() throws IOException{
		skipCommentsAndBlank();
		switch(cursor){
		case '{':
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			parseMap(map);
			return map;
		case '[':
			List<Object> list = new LinkedList<Object>();
			parseList(list);
			return list;
		case '"':
		case '\'':
			return parseString(cursor);//看来是个String
		default:
			return parseSimpleType();//其他基本数据类型
		}
	}
	
	/**
	 * 
	 * @param endTag 以什么作为结束符
	 */
	private String parseString(int endTag) throws IOException{
		//直至读取到相应的结束符!
		StringBuilder sb = new StringBuilder();
		while(true) {
			nextChar();
			if(cursor == endTag)
				break;
			if(cursor == '\\') {//转义字符?
				parseSp(sb);
			} else
				sb.append((char)cursor);
		}
		return sb.toString();
	}
	
	
	//读取转义字符
	private void parseSp(StringBuilder sb) throws IOException {
		nextChar();
		switch (cursor) {
		case 'n':
			sb.append('\n');
			break;
		case 'r':
			sb.append('\r');
			break;
		case 't':
			sb.append('\t');
			break;
		case '\\':
			sb.append('\\');
			break;
		case '\'':
			sb.append('\'');
			break;
		case '\"':
			sb.append('\"');
			break;
		case '/':
			sb.append('/');
			break;
		case 'u':
			char[] hex = new char[4];
			for (int i = 0; i < 4; i++)
				hex[i] = (char) nextChar();
			sb.append((char)Integer.valueOf(new String(hex), 16).intValue());
			break;
		case 'b': //这个支持一下又何妨?
			sb.append(' ');//空格
			break;
		case 'f':
			sb.append('\f');//这个支持一下又何妨?
			break;
		default:
			throw unexpectedChar();	//1.b.37及之前的版本,会忽略非法的转义字符
		}
	}
	
	/**
	 * 处理基本数据类型
	 * @return
	 */
	private Object parseSimpleType() throws IOException{
		StringBuilder sb = new StringBuilder();
		switch (cursor) {
		case 't':
			//看来是true
			if('r' == nextChar())
				if('u' == nextChar())
					if('e' == nextChar())
						return Boolean.TRUE;
			throw makeError("'true' is expected!");
		case 'f':
			//看来是false
			if('a' == nextChar())
				if('l' == nextChar())
					if('s' == nextChar())
						if('e' == nextChar())
							return Boolean.FALSE;
			throw makeError("'false' is expected!");
		case 'u':
			//看来是undefined
			if('n' == nextChar())
				if('d' == nextChar())
					if('e' == nextChar())
						if('f' == nextChar())
							if('i' == nextChar())
								if('n' == nextChar())
									if('e' == nextChar())
										if('d' == nextChar())
											return null;
			throw makeError("'undefined' is expected!");
		case 'n':
			//看来是null
			if('u' == nextChar())
				if('l' == nextChar())
					if('l' == nextChar())
						return null;
			throw makeError("'null' is expected!");
		
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
		case '-':
			//看来是数字
			sb.append((char)cursor);
			boolean hasPoint = cursor == '.';
			while(true) {
				if(!tryNextChar()) {//读完了? 处理一下
						if(hasPoint)
							return Double.parseDouble(sb.toString());
						else {
							Long p = Long.parseLong(sb.toString());
							if(Integer.MIN_VALUE < p.longValue() && p.longValue() < Integer.MAX_VALUE )
								return p.intValue();
							return p;
						}
					}
					switch (cursor) {
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
						sb.append((char)cursor);
						break;
					case '.':
						if(hasPoint)
							throw unexpectedChar();
						else {
							hasPoint = true;
							sb.append((char)cursor);
							break;
						}
					case 'L' :
					case 'l' :
						if(hasPoint)
							throw unexpectedChar();
						return Long.parseLong(sb.toString());
					case 'F':
					case 'f':
						return Float.parseFloat(sb.toString());
					default : {//越界读取了!!
						skipOneChar = true;
						if(hasPoint)
							return Double.parseDouble(sb.toString());
						else {
							Long p = Long.parseLong(sb.toString());
							if(Integer.MIN_VALUE < p.longValue() && p.longValue() < Integer.MAX_VALUE )
								return p.intValue();
							return p;
						}
					}
					}
			}
		default:
			throw unexpectedChar();//不是数值,不是布尔值,不是null和undefined? 玩野啊? 抛异常!!
		}
	}
	
	/**
	 * 
	 */
	private void parseMap(Map<String, Object> map) throws IOException{
		nextChar();
		skipCommentsAndBlank();
		if(cursor == '}')
			return;
		while(true) {
			parseMapItem(map);
			nextChar();
			skipCommentsAndBlank();
			switch (cursor) {
			case '}':
				return;
			case ',':
				nextChar();
				skipCommentsAndBlank();
				continue;
			default:
				throw unexpectedChar();
			}
		}
	}
	
	/**
	 * 生成MAP对象
	 * @param map
	 * @throws IOException
	 */
	protected void parseMapItem(Map<String, Object> map) throws IOException {
		map.put(fetchKey(), parseFromHere());
	}
	
	/**
	 * 找KEY
	 */
	protected String fetchKey() throws IOException{
	  //找key
        String key = null;
        switch (cursor) {
        case '"':
        case '\'':
            key = parseString(cursor);
            nextChar();
            skipCommentsAndBlank();
            break;
        default:
            //没办法,看来是无分隔符的字符串,找一下吧
            StringBuilder sb = new StringBuilder();
            sb.append((char)cursor);
            OUTER: while(true) {
                nextChar();
                switch (cursor) {
                case '\\'://特殊字符
                    parseSp(sb);
                    break;
                case ' ':
                case '/':
                    skipCommentsAndBlank();
                    if(cursor == ':') {
                        key = sb.toString().trim().intern();
                        break OUTER;
                    } else
                        throw unexpectedChar();
                case ':':
                    key = sb.toString().trim().intern();
                    break OUTER;
                default:
                    sb.append((char)cursor);
                }
            }
        }
        // TODO 判断一下key是否合法
        // 当前字符为: 跳过去
        nextChar();
        skipCommentsAndBlank();
        return key;
	}
	
	
	/**
	 * 处理List
	 * @param list
	 * @throws IOException
	 */ 
	private void parseList(List<Object> list) throws IOException{
		nextChar();
		skipCommentsAndBlank();
		if(cursor == ']')
			return;
		while(true) {
			list.add(parseFromHere());
			nextChar();
			skipCommentsAndBlank();
			switch (cursor) {
			case ']':
				return;
			case ','://看来还有元素
				nextChar();
				skipCommentsAndBlank();
				continue;
			default:
				throw unexpectedChar();
			}
		}
		
	}
	
	private int nextChar() throws IOException {
		if (!tryNextChar())
			throw unexpectedEnd();
		if (cursor == '\n') {
			row++;
			col = 0;
		} else
			col++;
		return cursor;
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
	private void skipInlineComment() throws IOException {
		while (nextChar() != '\n') {}
	}
	
	private void skipBlank() throws IOException {
		while (cursor >= 0 && cursor <= 32)
			nextChar();
	}

	private void skipBlockComment() throws IOException {
		nextChar();
		while (true) {
			if (cursor == '*') {
				if (nextChar() == '/')
					break;
			} else
				nextChar();
		}
	}
	
	private boolean tryNextChar() throws IOException {
		if(skipOneChar) {
			skipOneChar = false;
			return cursor != END;
		}
		cursor = reader.read();
		return cursor != END;
	}
	
	private JsonException makeError(String message) {
		return new JsonException(row, col, (char) cursor, message);
	}
	
	private JsonException unexpectedEnd() {
		return new JsonException(row, col, (char) cursor, "Unexpected End");
	}
	private JsonException unexpectedChar() {
		return new JsonException(row, col, (char) cursor, "Unexpected char");
	}
	
//	public static void main(String[] args) {
//		StringReader sr = new StringReader("{abc      :'ccc',ppp      : 123 ,                xx : true            }");
//		StringCompile2 sc2 = new StringCompile2();
//		System.out.println(sc2.parse(sr));
//		System.out.println(new StringCompile2().parse(new StringReader("{abc:{abc:123f}}")));
//		System.out.println(new StringCompile2().parse(new StringReader("{abc:{       abc:123f}}")));
//		System.out.println(new StringCompile2().parse(new StringReader("{abc:{abc:      123f}}")));
//		System.out.println(new StringCompile2().parse(new StringReader("[123,true]")));
//		System.out.println(new StringCompile2().parse(new StringReader("[123,456]")));
//		System.out.println(new StringCompile2().parse(new StringReader("[123,{abc:456}]")));
//		System.out.println(new StringCompile2().parse(new StringReader("[123,456L]")));
//		System.out.println(new StringCompile2().parse(new StringReader("123456789L")));
//		System.out.println(new StringCompile2().parse(new StringReader("2.3")));
//		System.out.println(new StringCompile2().parse(new StringReader("0.0f")));
//		System.out.println(new StringCompile2().parse(new StringReader("2.9999")));
//		System.out.println(new StringCompile2().parse(new StringReader("true")));
//		System.out.println(new StringCompile2().parse(new StringReader("false")));
//		System.out.println(new StringCompile2().parse(new StringReader("null")));
//		System.out.println(new StringCompile2().parse(new StringReader("undefined")));
//		System.out.println(new StringCompile2().parse(new StringReader("\"abc\"")));
//		System.out.println(new StringCompile2().parse(new StringReader("\"a\'bc\"")));
//		System.out.println(new StringCompile2().parse(new StringReader("\"\'a\\\"bc\"")));
//		
//		System.out.println(new StringCompile2().parse(new StringReader("var ioc = {id:6};")));
//	}
}
