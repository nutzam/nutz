package org.nutz.mapl.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.nutz.lang.Lang;
import org.nutz.lang.Nums;
import org.nutz.lang.Strings;

/**
 * 构建新的MapList结构对象, 根据path重建MapList结构
 * 
 * @author juqkai(juqkai@gmail.com)
 */
public class MaplRebuild {
    enum Model {
        add, del, cell
    }

    private Model model = Model.add;
    private String[] keys;
    private Object val;
    // 数组索引
    private Integer arrayItem;
    // 数组栈列表
    protected LinkedList<Integer> arrayIndex = new LinkedList<Integer>();
    // 新MapList结构
    private Map<String, Object> newobj = new LinkedHashMap<String, Object>();

    private Object cellObj = null;

    public MaplRebuild() {
        newobj.put("obj", null);
    }

    public MaplRebuild(Object mapl) {
        newobj.put("obj", mapl);
    }

    /**
     * 添加属性
     * 
     * @param path
     *            路径
     * @param obj
     *            值
     */
    public void put(String path, Object obj) {
        init(path, obj);
        inject(newobj, 0);
    }

    /**
     * 添加属性
     * 
     * @param path
     *            路径
     * @param obj
     *            值
     * @param arrayIndex
     *            索引队列
     */
    public void put(String path, Object obj, LinkedList<Integer> arrayIndex) {
        this.arrayIndex = arrayIndex;
        put(path, obj);
    }

    /**
     * 删除结点
     * 
     * @param path
     */
    public void remove(String path) {
        model = Model.del;
        init(path, null);
        inject(newobj, 0);
    }

    /**
     * 访问结点
     * 
     * @param path
     *            路径
     */
    public Object cell(String path) {
        model = Model.cell;
        init(path, null);
        inject(newobj, 0);
        return cellObj;
    }

    /**
     * 提取重建后的MapList
     */
    public Object fetchNewobj() {
        return newobj.get("obj");
    }

    private void init(String keys, Object obj) {
        this.keys = Lang.arrayFirst("obj", Strings.split(keys, false, '.'));
        this.val = obj;
        this.arrayItem = 0;
    }

    /**
     * 注入
     * 
     * @param obj
     * @param i
     */
    @SuppressWarnings("unchecked")
    private Object inject(Object obj, int i) {
        String key = keys[i];
        // 根数组
        if (key.indexOf('[') == 0) {
            List<Object> list = new ArrayList<Object>();
            if (obj != null) {
                list = (List<Object>) obj;
            }
            injectList(list, i, fetchIndex(key));
            return list;
        }
        // 数组
        // if(key.endsWith("[]")){
        int pos = key.indexOf('[');
        if (pos > 0) {
            Map<String, Object> map = new LinkedHashMap<String, Object>();
            if (obj != null) {
                map = (Map<String, Object>) obj;
            }
            // 有Key的list
            String k = key.substring(0, pos);
            if (!map.containsKey(k)) {
                map.put(k, new ArrayList<Object>());
            }
            int[] index = fetchIndex(key.substring(key.indexOf('['), key.length()));
            injectList((List<Object>) map.get(k), i, index);
            return map;
        }
        // 键值：这里有个特殊考虑，如果当前对象是个列表，那么键值必然是一个下标
        if (obj instanceof List) {
            try {
                int index = Integer.parseInt(keys[i]);
                injectList((List<Object>) obj, i, Nums.array(index));
                return obj;
            }
            catch (Exception e) {
                throw new RuntimeException("路径格式不正确!");
            }
        }
        // 当做普通的 map 好了
        return injectMap(obj, i);
    }

    private int[] fetchIndex(String val) {
        // []格式的路径, 即索引放在arrayIndex里面的.
        if (val.indexOf(']') == 1) {
            if (arrayIndex.size() > arrayItem) {
                return Nums.array(arrayIndex.get(arrayItem++));
            }
            // 默认返回 0
            return Nums.array(0);
        }
        // [1]格式, 路径上自带索引，可以是多个，譬如[1][3][0]
        String[] ss = val.substring(1, val.length() - 1).split("\\]\\[");
        int[] re = new int[ss.length];
        for (int i = 0; i < ss.length; i++)
            re[i] = Integer.parseInt(ss[i]);
        return re;
    }

    /**
     * 注入MAP
     * 
     * @param obj
     * @param i
     */
    @SuppressWarnings("unchecked")
    private Object injectMap(Object obj, int i) {
        Map<String, Object> map;
        String key = keys[i];
        if (obj == null) {
            map = new LinkedHashMap<String, Object>();
        } else {
            map = (Map<String, Object>) obj;
        }

        if (model == Model.add) {
            if (i == keys.length - 1) {
                map.put(key, val);
                return map;
            }
            if (!map.containsKey(key) || map.get(key) == null) {
                map.put(key, inject(null, i + 1));
                return map;
            }
        } else if (model == Model.del) {
            if (i == keys.length - 1) {
                map.remove(key);
                return map;
            }
            if (!map.containsKey(key) || map.get(key) == null) {
                return map;
            }
        } else if (model == Model.cell) {
            if (i == keys.length - 1) {
                cellObj = map.get(key);
                return map;
            }
            if (!map.containsKey(key) || map.get(key) == null) {
                return map;
            }
        }

        if (map.containsKey(key) && map.get(key) != null) {
            inject(map.get(key), i + 1);
        }
        return map;
    }

    /**
     * 注入List
     * 
     * @param list
     *            列表
     * @param keyIndex
     *            当前 Key 路径的列表
     * @param eleIndexes
     *            注入的元素下标列表
     */
    @SuppressWarnings("unchecked")
    private void injectList(List<Object> list, int keyIndex, int[] eleIndexes) {
        // 下标列表如果是多个，那么预先处理一下列表
        int i_last = eleIndexes.length - 1;
        for (int i = 0; i < i_last; i++) {
            int index = eleIndexes[i];
            Object ele = list.get(index);
            // 是列表？嗯很好很好
            if (ele instanceof List) {
                list = (List<Object>) ele;
            }
            // 不是列表啊，不能忍
            else {
                throw Lang.makeThrow("invalid keyPath '%s' in key:%d eleIndex:%d",
                                     Strings.join(".", keys),
                                     keyIndex,
                                     i);
            }
        }

        // 得到要处理的下标
        int eleIndex = eleIndexes[i_last];

        // 添加模式
        if (model == Model.add) {
            if (keyIndex == keys.length - 1) {
                if (val instanceof Collection) {
                    list.addAll((Collection<? extends Object>) val);
                } else {
                    list.add(eleIndex, val);
                }
                return;
            }
            if (list.size() <= eleIndex) {
                list.add(eleIndex, new LinkedHashMap<String, Object>());
            }
        } else if (model == Model.del) {
            if (keyIndex == keys.length - 1) {
                if (list.size() > eleIndex) {
                    list.remove(eleIndex);
                }
                return;
            }
            if (list.size() <= eleIndex) {
                return;
            }
        } else if (model == Model.cell) {
            if (keyIndex == keys.length - 1) {
                if (list.size() > eleIndex) {
                    cellObj = list.get(eleIndex);
                }
                return;
            }
            if (list.size() <= eleIndex) {
                return;
            }
        }
        inject((Map<String, Object>) list.get(eleIndex), keyIndex + 1);
    }

}