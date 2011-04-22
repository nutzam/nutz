package org.nutz.json2.compile;

import java.io.IOException;
import java.io.Reader;

import org.nutz.json.JsonException;
import org.nutz.json2.JsonCompile;
import org.nutz.json2.JsonItem;
import org.nutz.json2.item.ArrayJsonItem;
import org.nutz.json2.item.PairJsonItem;
import org.nutz.json2.item.ObjectJsonItem;
import org.nutz.json2.item.SingleJsonItem;
import org.nutz.json2.item.StringJsonItem;

/**
 * 字符串顺序预编译
 * 
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class StringCompile implements JsonCompile{
	
	private int cursor;
	private Reader reader;
	private int col;
	private int row;
	
	public JsonItem Compile(Reader reader) {
		this.reader = reader;
		try {
			nextChar();
			skipCommentsAndBlank();
			if(cursor == 'v'){
				/*
				 * Meet the var ioc ={ maybe, try to find the '{' and break
				 */
				while (-1 != nextChar())
					if ('{' == cursor)
						break;
			}
			return compileLocation();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private JsonItem compileLocation() throws IOException{
		skipCommentsAndBlank();
		JsonItem ji = null;
		switch(cursor){
		case '{':
		case '[':
			ji = compileArray();
			break;
		case '"':
		case '\'':
		default:
			ji = compileString();
			break;
		}
		skipCommentsAndBlank();
		return ji;
	}
	
	private JsonItem compileString() throws IOException{
		
		if(cursor != '\'' && cursor != '"'){
			StringBuilder sb = new StringBuilder();
			while(cursor != -1 && cursor != ':' && cursor != ',' && cursor != ']' && cursor != '}'){
				sb.append((char)cursor);
				nextChar();
				skipCommentsAndBlank();
			}
			SingleJsonItem sji = new SingleJsonItem();
			sji.setValue(sb.toString());
			return sji;
		}
		StringJsonItem sji = new StringJsonItem();
		sji.setValue(readString().toString());
		return sji;
	}
	/**
	 * 编译数组,将所有'[]','{}'包裹的字符串理解成数组
	 * @return
	 * @throws IOException
	 */
	private JsonItem compileArray() throws IOException{
		boolean isObj = cursor == '{' ? true: false;
		nextChar();
		ArrayJsonItem aji = isObj ? new ObjectJsonItem() : new ArrayJsonItem();
		while(cursor != '}' && cursor != ']'){
			if(cursor == ','){
				nextChar();
				continue;
			}
			JsonItem name = compileLocation();
			
			if(cursor == ':'){
				aji.addItem(compilePair(name));
				continue;
			}
			//保存单值对象
			aji.addItem(name);
		}
		nextChar();
		return aji;
	}

	/**
	 * 保存键值对对象
	 * @param name
	 * @return
	 * @throws IOException
	 */
	private JsonItem compilePair(JsonItem name) throws IOException{
		PairJsonItem obj = new PairJsonItem();
		obj.setKey(name);
		nextChar();
		obj.setValue(compileLocation());
		return obj;
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
		while (nextChar() != -1 && cursor != '\n') {}
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
	private JsonException makeError(String message) {
		return new JsonException(row, col, (char) cursor, message);
	}
}
