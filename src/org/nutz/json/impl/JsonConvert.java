package org.nutz.json.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.nutz.json.Json;
import org.nutz.json.JsonException;
import org.nutz.json.JsonFilter;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;

/**
 * Json转换.
 * <p> 将一种JSON结构转换成另外一种JSON结构.例:
 * <pre>
 *  {
 *      "age":"123",
 *      "name":"juqkai"
 *  }
 *  转换成:
 *  {
 *      "年龄":"123",
 *      "姓名":"juqkai"
 *  }
 * </pre>
 * <p>要进行这样的转换需要预先配置一个对应关系的配置, 具体的配置关系说明如下:
 * <ul>
 * <li>使用原JSON一样的结构
 * <li>有数组的, 只写第一个元素的结构
 * <li>原结构中的值, 以字符串或字符串数组做为目标结构的对应关系
 * <li>对应关系可以为数组
 * <li>有数组的, 目标结构以key[].abc来代替数组
 * <li>原结构数组层次强制限定一致, 目标结构中'[]'的索引按原结构中出现先后顺序进行匹配.
 * <li>如果原结果不存在, 那默认为0
 * <li>未在模板中申明的不做转换
 * </ul>
 * <p> 例:
 * <pre>
 * 例1:
 *  {
 *      "age":"user.年龄",
 *      "name":["user.name", "user.姓名"]
 *  }
 * 例2(原json:[{"name":"nutz"},{"name":"juqkai"}]):
 * [{
 *      "name":"[].姓名"
 * }]
 * 例3:
 * {
 *      users:[
 *          {
 *              "name":["people[].name", "users[].name"],
 *              "age":"users[].name"
 *          }
 *      ]
 * }
 * </pre>
 * @author juqkai(juqkai@gmail.com)
 */
public class JsonConvert implements JsonFilter{
    //路径
    private LinkedList<String> paths = new LinkedList<String>();
    private LinkedList<Integer> arrayIndex = new LinkedList<Integer>();
    //关系
    private Map<String, List<String>> relation = new HashMap<String, List<String>>();
    
    private Rebuild structure = new Rebuild();
    
    public JsonConvert(String model){
        initRelation(model);
    }
    
    /**
     * 转换
     * @param obj 目标对象
     * @param model 对应关系
     * @return 
     */
    public Object filter(Object obj) throws JsonException{
        convertObj(obj);
        return structure.fetchNewobj();
    }
    
    /**
     * 转换对象
     * @param obj
     */
    private void convertObj(Object obj) {
        if(obj instanceof Map){
            convertMap((Map<?, ?>) obj);
        } else if(obj instanceof List){
            convertList((List<?>) obj);
        }
    }
    /**
     * 转换map
     * @param obj
     */
    private void convertMap(Map<?, ?> obj) {
        for(Object key : obj.keySet()){
            paths.addLast(key.toString());
            renew(fetchPath(), obj.get(key));
            convertObj(obj.get(key));
            paths.removeLast();
        }
    }
    /**
     * 提取路径
     * @return
     */
    private String fetchPath(){
        StringBuffer sb = new StringBuffer();
        boolean first = true;
        for(String path : paths){
            if(!first){
                sb.append(".");
            }
            sb.append(path);
            first = false;
        }
        return sb.toString();
    }
    /**
     * 转换LIST
     * @param val
     */
    private void convertList(List<?> val){
        if(paths.size() <= 0){
            paths.add("[]");
        }else{
            paths.addLast(paths.removeLast() + "[]");
        }
        for(int i = 0; i < val.size(); i++){
            arrayIndex.add(i);
            convertObj(val.get(i));
            arrayIndex.remove();
        }
    }

    /**
     * 重建新对象
     * @param path
     * @param object
     */
    private void renew(String path, Object object) {
        if(relation.containsKey(path)){
            List<String> dests = relation.get(path);
            for(String dest : dests){
                if(dest.equals("")){
                    structure.put(path, object);
                    continue;
                } 
                structure.put(dest, object);
            }
        }
    }
    
    
   


    
    
    
    
    /**
     * 初始化关系
     * @param model
     */
    private void initRelation(String model){
        Object obj = Json.fromJson(Streams.fileInr(model));
        loadRelation(obj, "");
    }
    /**
     * 解析配置信息
     * @param obj
     * @param path
     */
    private void loadRelation(Object obj, String path) {
        if(obj instanceof Map){
            loadMapRelation((Map<?, ?>) obj, path);
        } else if(obj instanceof List){
            loadListRelation((List<?>) obj, path);
        } else {
            throw new RuntimeException("无法识别的类型!");
        }
    }
    /**
     * 解析List配置信息
     * @param obj
     * @param path
     */
    @SuppressWarnings("unchecked")
    private void loadListRelation(List<?> obj, String path) {
        if(obj.size() <= 0){
            return;
        }
        if(obj.get(0) instanceof String){
            relation.put(path, (List<String>) obj);
            return;
        }
        loadRelation(obj.get(0), path + "[]");
    }
    /**
     * 解析MAP配置信息
     * @param obj
     * @param path
     */
    private void loadMapRelation(Map<?, ?> obj, String path) {
        for(Object key : obj.keySet()){
            Object val = obj.get(key);
            if(val instanceof String){
                relation.put(path + space(path) + key.toString(), Lang.list(val.toString()));
                continue;
            }
            loadRelation(obj.get(key), path + space(path) + key.toString());
        }
    }
    
    private String space(String path){
        return path == "" ? "" : ".";
    }
    
    
    
    /**
     * 构建新的对象
     * @author juqkai(juqkai@gmail.com)
     */
    class Rebuild{
        private String[] keys;
        private Object val;
        private Integer arrayItem;
        private Map<String, Object> newobj = new HashMap<String, Object>();
        
        private Rebuild(){
            newobj.put("obj", null);
        }
        
        public void put(String keys, Object obj){
            init(keys, obj);
            injectMap(newobj, 0);
        }
        private void init(String keys, Object obj){
            keys = "obj." + keys;
            this.keys = keys.split("\\.");
            val = obj;
            arrayItem = 0;
        }
        
        public Object fetchNewobj(){
            return newobj.get("obj");
        }
        
        /**
         * 注入MAP
         * @param obj
         * @param i
         * @return 
         */
        @SuppressWarnings("unchecked")
        private Object injectMap(Object obj, int i) {
            String key = keys[i];
            if(key.equals("[]")){
                List<Object> list = new ArrayList<Object>();
                if(obj != null){
                    list = (List<Object>) obj;
                }
                injectList(list, i);
                return list;
            }
            Map<String, Object> map = new HashMap<String, Object>();
            if(obj != null){
                map = (Map<String, Object>) obj;
            }
            //有Key的list
            if(key.endsWith("[]")){
                String k = key.substring(0, key.indexOf('['));
                if(!map.containsKey(k)){
                    map.put(k, new ArrayList<Object>());
                }
                injectList((List<Object>) map.get(k), i);
                return map;
            }
            
            if(i == keys.length - 1){
                map.put(key, val);
                return map;
            }
            if(map.containsKey(key) && map.get(key) != null){
                injectMap(map.get(key), i + 1);
            } else {
                map.put(key, injectMap(null, i + 1));
            }
            return map;
        }

        /**
         * 注入List
         * @param list
         * @param i
         */
        @SuppressWarnings("unchecked")
        private void injectList(List<Object> list, int i) {
            int index = 0; 
            if(arrayIndex.size() > arrayItem){
                index = arrayIndex.get(arrayItem ++);
            }
            if(i == keys.length - 1){
                if(val instanceof Collection){
                    list.addAll((Collection<? extends Object>) val);
                } else {
                    list.add(index, val);
                }
                return;
            }
            if(list.size() <= index){
                list.add(index, new HashMap<String, Object>());
            }
            injectMap((Map<String, Object>) list.get(index), i + 1);
        }
        
    }
    
    
}
