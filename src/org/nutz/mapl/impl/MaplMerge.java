package org.nutz.mapl.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.mapl.Mapl;

/**
 * MapList合并
 * @author juqkai(juqkai@gmail.com)
 */
public class MaplMerge {
    public static Object merge(Object... objs){
        return new MaplMerge().mergeItems(objs);
    }
    /**
     * 转换器中间对象合并器<br/>
     * 合并 {@link Mapl} 中定义的中间结构.<br/>
     * 规则:<br>
     * <ul>
     * <li>普通对象, 保存为List, 但是要去掉重复.
     * <li>合并 map , 如果 key 值相同, 那么后一个值覆盖前面的值.递归合并
     * <li>list不做递归合并, 只做简单的合并, 清除重复的操作.
     * </ul>
     */
    public Object mergeItems(Object... objs) {
        if (objs == null || objs.length == 0) {
            return null;
        }
        if (objs.length == 1) {
            return objs[0];
        }
        // @ TODO 这里要不要判断是否兼容呢?
        if (objs[0] instanceof Map) {
            return mergeMap(objs);
        }
        if (objs[0] instanceof List) {
            return mergeList(objs);
        }
        return mergeObj(objs);
    }

    /**
     * 对象合并
     * 
     * @param objs
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private Object mergeObj(Object[] objs) {
        List list = new ArrayList();
        for (Object obj : objs) {
            if (list.contains(obj)) {
                continue;
            }
            list.add(obj);
        }
        return list;
    }

    /**
     * list合并
     * 
     * @param objs
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private Object mergeList(Object... objs) {
        List list = new ArrayList();
        for (Object li : objs) {
            List src = (List) li;
            for (Object obj : src) {
                if (!list.contains(obj)) {
                    list.add(obj);
                }
            }
        }
        return list;
    }

    /**
     * map合并
     * 
     * @param objs
     * @return
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private Object mergeMap(Object... objs) {
        Map obj = new HashMap();
        for (int i = 0; i < objs.length; i++) {
            Map map = (Map) objs[i];
            for (Object key : map.keySet()) {
                Object objval = obj.get(key);
                Object val = map.get(key);
                if (objval != null && (val instanceof List || val instanceof Map)) {
                    val = merge(objval, val);
                }
                obj.put(key, val);
            }
        }
        return obj;
    }
    
    
}
