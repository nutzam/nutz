package org.nutz.mvc.adaptor.injector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectNaviNode2 {
    private static final char separator = '.';
    private static final int TYPE_NONE = 0;
    private static final int TYPE_LIST = 1;
    private static final int TYPE_MAP = 2;
    //节点名
    private String name;
    //叶子节点的值
    private String[] value;
    //是否是叶子节点
    private boolean leaf = true;
    //子节点
    private Map<String, ObjectNaviNode2> child = new HashMap<String, ObjectNaviNode2>();
    //类型
    private int type = TYPE_NONE;

    /**
     * 初始化当前结点
     * 
     */
    public void put(String path, String[] value) {
//        path = path.replace('[', '.');
        path = path.replace("]", "");
        path = path.replace(")", "");
        
        init(path);
        String subPath = fetchSubPath(path); 
        if ("".equals(subPath) || path.equals(subPath)) {
            this.value = value;
            return;
        }
        leaf = false;
        addChild(subPath, value);
    }
    
    private String fetchSubPath(String path){
        String key = fetchNode(path);
        if(key.indexOf('[') > 0){
            return path.substring(path.indexOf('[') + 1);
        }
        if(key.indexOf('(') > 0){
            return path.substring(path.indexOf('(') + 1);
        }
        return path.substring(path.indexOf(separator) + 1);
    }
    
    private void init(String path){
        String key = fetchNode(path);
        if(key.indexOf('[') > 0){
            type = TYPE_LIST;
            name = key.substring(0, key.indexOf('['));
            return;
        }
        if(key.indexOf('(') > 0){
            type = TYPE_MAP;
            name = key.substring(0, key.indexOf('('));
            return;
        }
        type = TYPE_NONE;
        name = key;
    }

    /**
     * 添加子结点
     * 
     */
    private void addChild(String path, String[] value) {
        String subname = fetchName(fetchNode(path));
        ObjectNaviNode2 onn = child.get(subname);
        if (onn == null) {
            onn = new ObjectNaviNode2();
        }
        onn.put(path, value);
        child.put(subname, onn);
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
    
    private String fetchName(String key){
        if(key.indexOf('[') > 0){
            return key.substring(0, key.indexOf('['));
        }
        if(key.indexOf('(') > 0){
            return key.substring(0, key.indexOf('('));
        }
        return key;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Object get(){
        if(isLeaf()){
            return value.length == 1 ? value[0] : value;
        }
        if(type == TYPE_LIST){
            List list = new ArrayList();
            for(String o : child.keySet()){
                list.add(child.get(o).get());
            }
            return list;
        }
        if(type == TYPE_MAP){
            Map map = new HashMap();
            map.size();
        }
        Map map = new HashMap();
        for(String o : child.keySet()){
            map.put(o, child.get(o).get());
        }
        return map;
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
