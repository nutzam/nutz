package org.nutz.json;

public interface JsonFilter {

	/**
	 * 过滤对象,并返回过滤后的对象
	 */
	Object filter(Object obj) throws JsonException ;
	
}
