package org.nutz.json2;

import java.io.IOException;
import java.io.Reader;

/**
 * 预编译
 * @author juqkai(juqkai@gmail.com)
 *
 */
public interface JsonCompile {
	/**
	 * 编译
	 * @param reader
	 * @return
	 * @throws IOException 
	 */
	public JsonItem Compile(Reader reader);
}
