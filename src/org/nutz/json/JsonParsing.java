package org.nutz.json;

import java.io.Reader;
import java.lang.reflect.Type;

import org.nutz.json.compile.StringCompile;
import org.nutz.lang.stream.StringReader;

public class JsonParsing {
	private Reader reader;
	private JsonCompile jsonCompile;
	
	public JsonParsing(String json){
		this(new StringReader(json));
	}
	public JsonParsing(Reader reader){
		this.reader = reader;
		jsonCompile = new StringCompile();
	}
	
	public Object parseFromJson(Type type) {
		JsonItem node = jsonCompile.Compile(reader);
		return node.parse(type);
	}
}
