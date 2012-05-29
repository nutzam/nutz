package org.nutz.lang.maplist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 构建新的MapList结构对象, 根据path重建MapList结构
 * @author juqkai(juqkai@gmail.com)
 */
public class RebuildMapList{
    private String[] keys;
    private Object val;
    //数组索引
    private Integer arrayItem;
    //数组栈列表
    protected LinkedList<Integer> arrayIndex = new LinkedList<Integer>();
    //新MapList结构
    private Map<String, Object> newobj = new HashMap<String, Object>();
    
    public RebuildMapList(){
        newobj.put("obj", null);
    }
    
    /**
     * 添加属性
     * @param keys
     * @param obj
     */
    public void put(String keys, Object obj){
        init(keys, obj);
        injectMap(newobj, 0);
    }
    /**
     * 添加属性
     * @param keys
     * @param obj
     * @param arrayIndex
     */
    public void put(String keys, Object obj, LinkedList<Integer> arrayIndex){
        this.arrayIndex = arrayIndex;
        put(keys, obj);
    }
    /**
     * 提取重建后的MapList
     * @return
     */
    public Object fetchNewobj(){
        return newobj.get("obj");
    }
    
    private void init(String keys, Object obj){
        keys = "obj." + keys;
        this.keys = keys.split("\\.");
        val = obj;
        arrayItem = 0;
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