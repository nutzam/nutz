package org.nutz.json;

import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author juqkai(juqkai@gmail.com)
 */
public class JsonFilter {
    private List<String> mates;
    private LinkedList<String> path = new LinkedList<String>();
    /**
     * 过滤类型, true为包含, false为排除
     */
    private boolean type;
    public JsonFilter(List<String> mates, boolean type) {
        this.mates = mates;
        this.type = type;
    }
    /**
     * 包含
     * @return
     */
    public boolean include() {
        if (mates == null) {
            return true;
        }
        String path = fetchPath();
        for (String s : mates) {
            if (type) {
                //包含
                if (s.startsWith(path)) {
                    return true;
                }
            } else {
                //排除
                if (s.equals(path)) {
                    return false;
                }
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
    /**
     * 压栈
     * @param path
     */
    public void pushPath(String path){
        this.path.addLast(path);
    }
    /**
     * 退栈
     */
    public void pollPath(){
        this.path.removeLast();
    }
}
