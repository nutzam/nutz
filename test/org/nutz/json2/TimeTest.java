package org.nutz.json2;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.junit.Test;
import org.nutz.json.Json;
import org.nutz.lang.Stopwatch;
import org.nutz.lang.stream.StringReader;

public class TimeTest {

	Reader reader;
	StringBuilder json = new StringBuilder();
	@org.junit.Before
	public void before() throws IOException{
		reader = getFileAsInputStreamReader("org/nutz/json2/allType.txt");
		int i = reader.read();
		while(i != -1){
			json.append((char)i);
			i = reader.read();
		}
	}
	
	@Test
	public void JsonTest() throws IOException{
		System.out.println("json开始解析");
		for(int i = 1; i <= 100000; i = i * 10)
		{
			reader = getFileAsInputStreamReader("org/nutz/json2/allType.txt");
			Stopwatch sw = Stopwatch.begin();
			for(int j = 1; j < i; j ++){
				AllType at = Json.fromJson(AllType.class, new StringReader(json));
			}
			sw.stop();
			System.out.println(i + "次解析的时间:" + sw.getDuration());
		}
	}
	@Test
	public void Json2Test(){
		System.out.println("json2开始解析");
		for(int i = 1; i <= 100000; i = i * 10)
		{
			reader = getFileAsInputStreamReader("org/nutz/json2/allType.txt");
			Stopwatch sw = Stopwatch.begin();
			for(int j = 1; j < i; j ++){
				AllType at = Json2.fromJson(AllType.class, new StringReader(json));
			}
			sw.stop();
			System.out.println(i + "次解析的时间:" + sw.getDuration());
		}
	}
	
	private InputStreamReader getFileAsInputStreamReader(String fileName) {
		if (!fileName.startsWith("/"))
			fileName = "/" + fileName;
		return new InputStreamReader(getClass().getResourceAsStream(fileName));
	}
}
