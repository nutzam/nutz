package org.nutz.json2;

import java.io.Reader;
import java.lang.reflect.Type;

import org.nutz.json2.compile.StringCompile;
import org.nutz.lang.stream.StringReader;

public class JsonParsing2 {
	private Reader reader;
	private JsonCompile jsonCompile;
	
	public JsonParsing2(String json){
		this(new StringReader(json));
	}
	public JsonParsing2(Reader reader){
		this.reader = reader;
		jsonCompile = new StringCompile();
	}
	
	public Object parseFromJson(Type type) {
		JsonItem node = jsonCompile.Compile(reader);
		return node.parse(type);
	}
}
