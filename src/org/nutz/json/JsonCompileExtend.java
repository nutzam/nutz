package org.nutz.json;

import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 扩充 JsonCompile 实现, 以使它支持对JSON字符串的过滤处理
 * @author juqkai(juqkai@gmail.com)
 */
public class JsonCompileExtend extends JsonCompile{
    private List<String> mates;
    private LinkedList<String> path = new LinkedList<String>();
    /**
     * 过滤类型, true为包含, false为排除
     */
    private boolean type;
    
    public Object parse(Reader reader, List<String> mates, boolean type) {
        this.type = type;
        this.mates = mates;
        return super.parse(reader);
    }
    /**
     * 在Map解释添加过滤
     */
    protected void parseMapItem(Map<String, Object> map) throws IOException {
        String key = fetchKey();
        path.addLast(key);
        Object val = parseFromHere();
        if(include()){
            map.put(key, val);
        }
        path.removeLast();
    }
    /**
     * 包含
     * @return
     */
    private boolean include(){
        if(mates == null){
            return true;
        }
        String path = fetchPath();
        for(String s : mates){
            if(path.equals(s)){
                return type ? true : false;
            }
        }
        return type ? false : true;
    }
    /**
     * 获取路径
     * @return
     */
    private String fetchPath(){
        StringBuffer sb = new StringBuffer();
        for(String s : path){
            if(sb.length() > 0){
                sb.append(".");
            }
            sb.append(s);
        }
        return sb.toString();
    }
}
