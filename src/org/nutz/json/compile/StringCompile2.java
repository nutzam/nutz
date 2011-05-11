package org.nutz.json.compile;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.nutz.json.JsonException;
import org.nutz.lang.Lang;

/**
 * 将json理解为Map+List
 * 
 * @author wendal
 *
 */
public class StringCompile2 {
	
	private int cursor;
	private Reader reader;
	private int col;
	private int row;
	
	private static final int END = -1;
	
	public Object Compile(Reader reader) {
		this.reader = reader;
		try {
			//开始读取数据
			nextChar();
			skipCommentsAndBlank();
			if(cursor == 'v'){
				/*
				 * Meet the var ioc ={ maybe, try to find the '{' and break
				 */
				while (true) {
					if ('{' == cursor)//尝试找到{,以确定是否为"var ioc ={"格式
						break;
				}
			}
			return compileLocation();
		} catch (IOException e) {
			throw Lang.wrapThrow(e);
		}
	}
	
	private Object compileLocation() throws IOException{
		skipCommentsAndBlank();
		Object ji = null;
		switch(cursor){
		case '{':
			skipCommentsAndBlank();
			nextChar();
			ji = new LinkedHashMap<String, Object>();
			compileMap((Map<String, Object>)ji);
			break;//TODO 处理为Map
		case '[':
			ji = new LinkedList<Object>();
			compileList((List<Object>)ji);
			break;//TODO 处理为List
		case '"':
		case '\'':
			ji = compileString(cursor);//看来是个String
			break;
		default:
			ji = compileSimple();//其他基本数据类型
			break;
		}
		return ji;
	}
	
	/**
	 * 
	 * @param endTag 以什么作为结束符
	 */
	private String compileString(int endTag) throws IOException{
		//直至读取到相应的结束符!
		StringBuilder sb = new StringBuilder();
		while(true) {
			nextChar();
			if(cursor == endTag)
				break;
			if(cursor == '\\') {//转义字符?
				complieSp(sb);
			} else
				sb.append((char)cursor);
		}
		return sb.toString();
	}
	
	//读取转义字符
	private void complieSp(StringBuilder sb) throws IOException {
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
		case 'u':
			char[] hex = new char[4];
			for (int i = 0; i < 4; i++)
				hex[i] = (char) nextChar();
			sb.append((char)Integer.valueOf(new String(hex), 16).intValue());
			break;
		case 'b':
			throw makeError("don't support \\b");
		case 'f':
			throw makeError("don't support \\f");
		}
	}
	
	/**
	 * 处理基本数据类型
	 * @return
	 */
	private Object compileSimple() throws IOException{
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
			//看来是数字
			sb.append((char)cursor);
//			System.out.println("---->"+sb.toString());
			boolean hasPoint = false;
			while(true) {
				try {
					if(!tryNextChar())
						break;
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
//						System.out.println(sb.toString());
//						System.out.println("++++++" + (char)cursor);
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
					default :
						if(hasPoint)
							return Double.parseDouble(sb.toString());
						else
							return Long.parseLong(sb.toString());
					}
				} catch (IOException e) {//读取完了? OK,处理一下
					if(hasPoint)
						return Double.parseDouble(sb.toString());
					else
						return Long.parseLong(sb.toString());
				}
			}

		default:
			throw unexpectedChar();//不是数值,不是布尔值,不是null和undefined? 玩野啊? 抛异常!!
		}
	}
	
	/**
	 * 
	 */
	private void compileMap(Map<String, Object> map) throws IOException{
		//找key
		String key = null;
		switch (cursor) {
		case '}':	
			//看来已经结束了
			return;
		case '"':
		case '\'':
			key = compileString(cursor);
			break;
		default:
			//没办法,看来是无分隔符的字符串,找一下吧
			StringBuilder sb = new StringBuilder();
			sb.append((char)cursor);
			OUTER: while(true) {
				nextChar();
				switch (cursor) {
				case '\\'://特殊字符
					complieSp(sb);
					break;
				case ' ':
				case ':':
					break OUTER;
				default:
					sb.append((char)cursor);
				}
			}
			key = sb.toString();
		}
		//TODO 判断一下key是否合法
		//key找到了,开始找:
		while(cursor != ':') {
			nextChar();
		}
		//当前字符为: 跳过去
		System.out.println((char)cursor);
		nextChar();
		skipCommentsAndBlank();
		Object value = compileLocation();
		map.put(key, value);
		System.out.println("??+++>>>>>>>>    "+(char)cursor);
		if(value instanceof Map || value instanceof List)
			;
		else
			nextChar();
		skipCommentsAndBlank();
		switch (cursor) {
		case '}':
			return;
		case ','://看来还有其他key/value
			nextChar();
			skipCommentsAndBlank();
			compileMap(map);
			return;
		default:
			throw unexpectedChar();
		}
	}
	
	/**
	 * 处理List
	 * @param list
	 * @throws IOException
	 */ 
	private void compileList(List<Object> list) throws IOException{
		skipCommentsAndBlank();
		nextChar();
		list.add(compileLocation());
		skipCommentsAndBlank();
//		if(!tryNextChar())
//			return;
//		System.out.println(">-> " + (char)cursor);
//		System.out.println(">-> " + list);
		switch (cursor) {
		case ']':
			return;
		case ','://看来还有其他key/value
			compileList2(list);
			return;
		default:
			throw unexpectedChar();
		}
	}
	
	private void compileList2(List<Object> list) throws IOException{
		skipCommentsAndBlank();
		nextChar();
		list.add(compileLocation());
		skipCommentsAndBlank();
		if(!tryNextChar())
			return;
		switch (cursor) {
		case ']':
			return;
		case ','://看来还有其他key/value
			compileList(list);
			return;
		default:
			throw unexpectedChar();
		}
	}

	
	private int nextChar() throws IOException {
		if (-1 == cursor)
			return -1;
		cursor = reader.read();
		System.out.println("read --> " + (char)cursor);
		if (cursor == END)
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
		while (cursor != END && cursor == '/') {
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
		while (nextChar() != END && cursor != '\n') {}
	}
	
	private void skipBlank() throws IOException {
		while (cursor >= 0 && cursor <= 32)
			nextChar();
	}

	private void skipBlockComment() throws IOException {
		nextChar();
		while (cursor != END) {
			if (cursor == '*') {
				if (nextChar() == '/')
					break;
			} else
				nextChar();
		}
	}
	
	private boolean tryNextChar() throws IOException {
		cursor = reader.read();
		System.out.println("read --> " + (char)cursor);
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
	
	public static void main(String[] args) {
		StringReader sr = new StringReader("{abc      :'ccc',ppp      : 123 ,                xx : true            }");
		StringCompile2 sc2 = new StringCompile2();
		System.out.println(sc2.Compile(sr));
		System.out.println(new StringCompile2().Compile(new StringReader("{abc:{abc:123f}}")));
		System.out.println(new StringCompile2().Compile(new StringReader("[123,true]")));
		System.out.println(new StringCompile2().Compile(new StringReader("[123,456]")));
//		System.out.println(new StringCompile2().Compile(new StringReader("[123,{abc:456}]")));
//		System.out.println(new StringCompile2().Compile(new StringReader("[123,456]")));
	}
	
	
}
