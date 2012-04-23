package org.nutz.json;

import java.util.LinkedList;
import java.util.List;

/**
 * <p>
 * 规则:
 * <ul> 
 * <li>1. 要定义过滤或是包含, 都直接以对象关联的方式写出, 如: user.name, 
 * <li>2. 不区分 Map, List 全部都使用 1 中的形式. 基本这里指的 Map, List 是指 JsonCompile 转换的中间对象, 而非 JAVA 属性中的 Map, List. 注意概念
 * <li>3. 包含还是排除, 以 type 属性做标识, true 为包含, false 为排除.
 * <li>4. 同一时间只支持一种关系.
 * </ul>
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
