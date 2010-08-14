package org.nutz.yaml.impl.simple;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

/**
 * 基于行的Yaml解析器
 * <p/>支持 - : # 
 * <p/>不支持<code>---</code>
 * <p/>不支持<code>!</code>
 * <p/>不支持<code>|</code>
 * <p/>不支持<code>></code>
 * <p/>不支持<code>&</code>
 * <p/>不支持<code><</code>
 * @author wendal
 *
 */
public class YamlParsing {
	
	private BufferedReader br;
	
	/**
	 * 当前行数
	 */
	private int row;
	
	/**
	 * 深度与前置空格数量的对应关系
	 */
	private List<Integer> indentRecorder = new ArrayList<Integer>();
	
	/**
	 * 深度与集合对象的关系
	 */
	private List<Object> indentHolder = new ArrayList<Object>();
	
	/**
	 * 当前深度是否允许添加下层
	 */
	private boolean canAddIndent = true;
	
	private String preKey;

	
	public Object parse(Reader reader) throws IOException{
		br = new BufferedReader(reader);
		while (br.ready()) {
			String str = br.readLine();
			if (str == null)
				break;
			parseCurrentLine(str);
			row++;
		}
		if (indentHolder.size() > 0)
			return indentHolder.get(0);
		return null;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public void parseCurrentLine(String line){
		int commentIndex = line.indexOf(YamlKeyword.Comment);
		//移除注释
		if (commentIndex > -1) {
			if (commentIndex ==0)
				return;
			line = line.substring(0,commentIndex);
		}
		if(Strings.isBlank(line))
			return;
		int backspaceLength = 0;
		char[] cs = line.toCharArray();
		for (int i = 0; i < cs.length; i++) {
			if (cs[i] != ' '){
				backspaceLength = i;
				break;
			}
		}
		int myIndent = -1;
		for (int i = 0; i < indentRecorder.size(); i++) {
			if (indentRecorder.get(i) == backspaceLength){
				myIndent = i;
				break;
			}
		}
		boolean newIndent = false;
		if (myIndent == -1 ){
			if (indentRecorder.size() == 0 || backspaceLength > indentRecorder.get(indentRecorder.size() - 1)) {
				indentRecorder.add(backspaceLength);
				myIndent = indentRecorder.size() - 1;
				newIndent = true;
			} else
				throw Lang.makeThrow("Unknow indent level at line %d",row);
		}
		
		if (newIndent && (!canAddIndent))
			throw Lang.makeThrow("No allow to add - here , line %d",row);
		if (! newIndent)
			indentHolder = indentHolder.subList(0, myIndent+1);
		
		String str = line.trim().intern(); //移除前后空格
		canAddIndent = str.endsWith(YamlKeyword.Map.trim());
		
		if(str.startsWith(YamlKeyword.List)){ //这一层的对象为List元素
			List<Object> list = null;
			if (newIndent && indentHolder.size() < myIndent + 1){
				list = new ArrayList<Object>();
				indentHolder.add(list);
				if (preKey != null) {
					((Map<String,Object>)indentHolder.get(myIndent - 1)).put(preKey, list);
					preKey = null;
				}
			} else {
				Object tmp = indentHolder.get(myIndent);
				if (tmp instanceof List)
					list = (List)tmp;
				else
					throw Lang.makeThrow("- isn't allow in line %d", row);
			}
			String info = str.substring(YamlKeyword.List.length());
			if (info.indexOf(YamlKeyword.Map) > 0) { //必定是 xxx: yyy 形式
				String key = info.substring(0,info.indexOf(YamlKeyword.Map)).trim().intern();
				String value = info.substring(info.indexOf(YamlKeyword.Map) + YamlKeyword.Map.length()).trim().intern();
				Map<String, Object> map = new HashMap<String, Object>();
				map.put(key, value);
				list.add(map);
				indentHolder.add(map);
				indentRecorder.add(line.indexOf(key, backspaceLength+1));
			} else if (info.endsWith(YamlKeyword.Map.trim())){ //必定是 something:
				preKey = str.substring(0,str.length()-1).trim().intern();
			} else
				list.add(info.trim());
		}else {
		
			Map<String, Object> map = null;
			if (newIndent){
				map = new HashMap<String, Object>();
				indentHolder.add(map);
				if (preKey != null) {
					((Map<String,Object>)indentHolder.get(myIndent - 1)).put(preKey, map);
					preKey = null;
				}
			} else
				map = (Map<String, Object>) indentHolder.get(myIndent);
			if (str.indexOf(YamlKeyword.Map) > 0) { //必定是 xxx: yyy 形式
				String key = str.substring(0,str.indexOf(YamlKeyword.Map)).trim().intern();
				String value = str.substring(str.indexOf(YamlKeyword.Map) + YamlKeyword.Map.length()).trim().intern();
				map.put(key, value);
			} else if (str.endsWith(YamlKeyword.Map.trim())){ //必定是 something:
				preKey = str.substring(0,str.length()-1).trim().intern();
			} else
				throw Lang.makeThrow("No a map value in line %d", row);
		}
	}
}
