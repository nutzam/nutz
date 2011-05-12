package org.nutz.json2.compile;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import org.nutz.json2.JsonItem;
import org.nutz.json2.item.ArrayJsonItem;
import org.nutz.json2.item.ObjectJsonItem;
import org.nutz.json2.item.PairJsonItem;
import org.nutz.json2.item.SingleJsonItem;

/**
 * 配对预编译.
 * 把所有符号先做配对,然后再进行转换. '',"",[],{}
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class QueueCompile extends AbstractCompile{
	private Queue<Integer> sign;
	private Queue<SingleJsonItem> items;
	
	public QueueCompile() {
		sign = new LinkedList<Integer>();
		items = new LinkedList<SingleJsonItem>();
	}
	
	/**
	 * 
	 */
	protected JsonItem compileLocation() throws IOException {
		skipCommentsAndBlank();
		boolean beforeItem = true;
		while(cursor != -1){
			switch(cursor){
			case '{':
			case '[':
			case ':':
				sign.add(cursor);
				nextChar();
				beforeItem = true;
				break;
			case ',':
			case '}' :
			case ']' :
				if(beforeItem){
					sign.add(cursor);
				} else {
					sign.add((int)'~');
				}
				nextChar();
				beforeItem = false;
				break;
			case '"':
			case '\'':
			default:
				items.add(readString());
				beforeItem = true;
				break;
			}
			skipCommentsAndBlank();
		}
		if(sign.isEmpty()){
			if(!items.isEmpty()){
				return items.poll();
			}
			return null;
		}
		return compileQueue(sign.poll());
	}
	
	private JsonItem compileQueue(int s){
		switch(s){
		case '{':
			ObjectJsonItem oji = new ObjectJsonItem();
			compileArray(oji);
			return oji;
		case '[':
			ArrayJsonItem aji = new ArrayJsonItem();
			compileArray(aji);
			return aji;
		case ':':
			if(sign.isEmpty() || items.isEmpty()){
				return null;
			}
			PairJsonItem pji = new PairJsonItem();
			pji.setKey(items.poll());
			pji.setValue(compileQueue(sign.poll()));
			return pji;
		case '}':
		case ']':
		case ',':
			return items.poll();
			
		}
		return null;
	}
	private void compileArray(ArrayJsonItem aji){
		while(!sign.isEmpty()){
			JsonItem ji = compileQueue(sign.poll());
			if(ji == null){
				continue;
			}
			aji.addItem(ji);
			if(sign.isEmpty() || '~' == sign.peek()){
				sign.poll();
				break;
			}
		}
	}
	

}
