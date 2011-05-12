package org.nutz.json2.compile;

import java.io.IOException;
import org.nutz.json2.JsonItem;
import org.nutz.json2.item.ArrayJsonItem;
import org.nutz.json2.item.PairJsonItem;
import org.nutz.json2.item.ObjectJsonItem;

/**
 * 字符串顺序预编译
 * 
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class StringCompile1 extends AbstractCompile{
	
	protected JsonItem compileLocation() throws IOException{
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
			ji = readString();
			break;
		}
		skipCommentsAndBlank();
		return ji;
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
	
}
