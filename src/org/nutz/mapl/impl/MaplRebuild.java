package org.nutz.mapl.impl;

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
public class MaplRebuild{
    enum Model{add,del, cell}
    private Model model = Model.add;
    private String[] keys;
    private Object val;
    //数组索引
    private Integer arrayItem;
    //数组栈列表
    protected LinkedList<Integer> arrayIndex = new LinkedList<Integer>();
    //新MapList结构
    private Map<String, Object> newobj = new HashMap<String, Object>();
    
    private Object cellObj = null;
    
    public MaplRebuild(){
        newobj.put("obj", null);
    }
    
    public MaplRebuild(Object mapl){
        newobj.put("obj", mapl);
    }
    
    /**
     * 添加属性
     * @param path 路径
     * @param obj 值
     */
    public void put(String path, Object obj){
        init(path, obj);
        inject(newobj, 0);
    }
    /**
     * 添加属性
     * @param path 路径
     * @param obj 值
     * @param arrayIndex 索引队列
     */
    public void put(String path, Object obj, LinkedList<Integer> arrayIndex){
        this.arrayIndex = arrayIndex;
        put(path, obj);
    }
    
    /**
     * 删除结点
     * @param path
     */
    public void remove(String path) {
        model = Model.del;
        init(path, null);
        inject(newobj, 0);
    }
    
    /**
     * 访问结点
     * @param path 路径
     */
    public Object cell(String path){
        model = Model.cell;
        init(path, null);
        inject(newobj, 0);
        return cellObj;
    }
    /**
     * 提取重建后的MapList
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
     * 注入
     * @param obj
     * @param i
     */
    @SuppressWarnings("unchecked")
    private Object inject(Object obj, int i){
        String key = keys[i];
        //根数组
        if(key.indexOf('[') == 0){
            List<Object> list = new ArrayList<Object>();
            if(obj != null){
                list = (List<Object>) obj;
            }
            injectList(list, i, fetchIndex(key));
            return list;
        }
        //数组
//        if(key.endsWith("[]")){
        if(key.indexOf('[') >0){
            Map<String, Object> map = new HashMap<String, Object>();
            if(obj != null){
                map = (Map<String, Object>) obj;
            }
            //有Key的list
            String k = key.substring(0, key.indexOf('['));
            if(!map.containsKey(k)){
                map.put(k, new ArrayList<Object>());
            }
            int index = fetchIndex(key.substring(key.indexOf('['), key.length()));
            injectList((List<Object>) map.get(k), i, index);
            return map;
        }
        if(obj instanceof List){
            try{
                int index = Integer.parseInt(keys[i]); 
                injectList((List<Object>) obj, i, index);
                return obj;
            }catch (Exception e) {
                throw new RuntimeException("路径格式不正确!");
            }
        }
        return injectMap(obj, i);
    }
    
    private int fetchIndex(String val){
        int index = 0; 
        if(val.indexOf(']') == 1){
            //[]格式的路径, 即索引放在arrayIndex里面的.
            if(arrayIndex.size() > arrayItem){
                index = arrayIndex.get(arrayItem ++);
            }
        } else {
            //[1]格式, 路径上自带索引
            index = Integer.parseInt(val.substring(1, val.length() - 1));
        }
        return index;
    }
    
    /**
     * 注入MAP
     * @param obj
     * @param i
     */
    @SuppressWarnings("unchecked")
    private Object injectMap(Object obj, int i) {
        Map<String, Object> map = new HashMap<String, Object>();
        String key = keys[i];
        if(obj != null){
            map = (Map<String, Object>) obj;
        }
        
        if(model == Model.add){
            if(i == keys.length - 1){
                map.put(key, val);
                return map;
            }
            if(!map.containsKey(key) || map.get(key) == null){
                map.put(key, inject(null, i + 1));
            }
        } else if(model == Model.del){
            if(i == keys.length - 1){
                map.remove(key);
                return map;
            }
            if(!map.containsKey(key) || map.get(key) == null){
                return map;
            }
        } else if(model == Model.cell){
            if(i == keys.length - 1){
                cellObj = map.get(key);
                return map;
            }
            if(!map.containsKey(key) || map.get(key) == null){
                return map;
            }
        }
        
        if(map.containsKey(key) && map.get(key) != null){
            inject(map.get(key), i + 1);
        }
        return map;
    }

    /**
     * 注入List
     * @param list
     * @param i
     */
    @SuppressWarnings("unchecked")
    private void injectList(List<Object> list, int i, int index) {
        //添加模式
        if(model == Model.add){
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
        } else if(model == Model.del){
            if(i == keys.length - 1){
                if(list.size() > index){
                    list.remove(index);
                }
                return;
            }
            if(list.size() <= index){
                return;
            }
        } else if(model == Model.cell){
            if(i == keys.length - 1){
                if(list.size() > index){
                    cellObj = list.get(index);
                }
                return;
            }
            if(list.size() <= index){
                return;
            }
        }
        inject((Map<String, Object>) list.get(index), i + 1);
    }
    
}