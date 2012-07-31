package org.nutz.mvc.adaptor.injector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 对象路径节点转换.<br/>
 * 将URL中的字符串参数名转换成对结构, 然后通过 {@link Objs}转换成实体对象<br/>
 * URL规则:
 * <ul>
 *  <li>对象与属性之间使用"."做为连接符
 *  <li>数组,Collection对象, 使用"[]"或":"做为索引引用符. <b style='color:red'>索引只是一个参考字段, 不会根据其值设置索引</b>
 *  <li>Map使用"()"或"."分割key值
 * </ul>
 * 例:<br> 
 * <code>
 * Object:  node.str = str<br>
 * list:    node.list[1].str = abc;<br>
 *          node.list:2.str = 2<br>
 * set:     node.set[2].str = bbb<br>
 *          node.set:jk.str = jk<br>
 * Map:     node.map(key).str = bb;<br>
 *          node.map.key.name = map<br>
 * 
 * </code>
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class ObjectNaviNode {
    private static final char separator = '.';
    private static final char LIST_SEPARATOR = ':';
    private static final int TYPE_NONE = 0;
    private static final int TYPE_LIST = 1;
    //节点名
    private String name;
    //叶子节点的值
    private String[] value;
    //是否是叶子节点
    private boolean leaf = true;
    //子节点
    private Map<String, ObjectNaviNode> child = new HashMap<String, ObjectNaviNode>();
    //类型
    private int type = TYPE_NONE;

    /**
     * 初始化当前结点
     * 
     */
    public void put(String path, String[] value) {
        path = path.replace("[", ":");
        path = path.replace("]", "");
        path = path.replace("(", ".");
        path = path.replace(")", "");
        
        putPath(path, value);
    }
    
    private void putPath(String path, String[] value){
        init(path);
        String subPath = fetchSubPath(path); 
        if ("".equals(subPath) || path.equals(subPath)) {
            this.value = value;
            return;
        }
        leaf = false;
        addChild(subPath, value);
    }
    /**
     * 添加子结点
     * 
     */
    private void addChild(String path, String[] value) {
        String subname = fetchName(path);
        ObjectNaviNode onn = child.get(subname);
        if (onn == null) {
            onn = new ObjectNaviNode();
        }
        onn.putPath(path, value);
        child.put(subname, onn);
    }
    
    /**
     * 初始化name, type信息
     * @param path
     */
    private void init(String path){
        String key = fetchNode(path);
        if(isList(key)){
            type = TYPE_LIST;
            name = key.substring(0, key.indexOf(LIST_SEPARATOR));
            return;
        }
        name = key;
    }
    
    /**
     * 提取子路径
     * @param path
     */
    private String fetchSubPath(String path){
        if(isList(fetchNode(path))){
            return path.substring(path.indexOf(LIST_SEPARATOR) + 1);
        }
        return path.substring(path.indexOf(separator) + 1);
    }
    

    
    /**
     * 取得节点名
     * 
     */
    private String fetchNode(String path) {
        if (path.indexOf(separator) <= 0) {
            return path;
        }
        return path.substring(0, path.indexOf(separator));
    }
    
    
    /**
     * 取得节点的name信息
     */
    private String fetchName(String path){
        String key = fetchNode(path);
        if(isList(key)){
            return key.substring(0, key.indexOf(LIST_SEPARATOR));
        }
        return key;
    }
    
    /**
     * 提取出list,map结构
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Object get(){
        if(isLeaf()){
            return value == null ? null : value.length == 1 ? value[0] : value;
        }
        if(type == TYPE_LIST){
            List list = new ArrayList();
            for(String o : child.keySet()){
                list.add(child.get(o).get());
            }
            return list;
        }
        Map map = new HashMap();
        for(String o : child.keySet()){
            map.put(o, child.get(o).get());
        }
        return map;
    }
    
    /**
     * 是否是list节点
     * @param key
     */
    private boolean isList(String key){
        return key.indexOf(LIST_SEPARATOR) > 0;
    }

    public String getName() {
        return name;
    }

    public String[] getValue() {
        return value;
    }

    public boolean isLeaf() {
        return leaf;
    }
}
