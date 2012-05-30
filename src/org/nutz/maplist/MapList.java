package org.nutz.maplist;

import java.lang.reflect.Type;

import org.nutz.maplist.impl.MapListCell;
import org.nutz.maplist.impl.MapListMerge;
import org.nutz.maplist.impl.convert.ObjConvertImpl;

/**
 * 集合了对象转换合并等高级操作
 * 
 * @author juqkai(juqkai@gmail.com)
 * 
 */
public class MapList {
	/**
	 * 这个实现, 主要将 List, Map 的对象结构转换成真实的对象.
	 * <p>
	 * 规则:
	 * <ul>
	 * <li>对象以Map存储, key为属性名, value为属性值
	 * <li>数组以List存储
	 * <li>Map直接存储为Map
	 * <li>List直接存储为List
	 * <li>只要不是List, Map 存储的, 都认为是可以直接写入对象的. TODO 这点可以调整一下.
	 * </ul>
	 */
	public static Object convert(Object model, Type type) {
	    return new ObjConvertImpl(type).convert(model);
	}
	
    /**
     * 访问MAP, List结构的数据, 通过 uers[2].name 这种形式.
     */
    public static Object cell(Object obj, String path){
        return MapListCell.cell(obj, path);
    }
    

    /**
     * 转换器中间对象合并器<br/>
     * 合并 {@link MapList} 中定义的中间结构.<br/>
     * 规则:<br>
     * <ul>
     * <li>普通对象, 保存为List, 但是要去掉重复.
     * <li>合并 map , 如果 key 值相同, 那么后一个值覆盖前面的值.递归合并
     * <li>list不做递归合并, 只做简单的合并, 清除重复的操作.
     * </ul>
     */
    public static Object merge(Object... objs) {
        return MapListMerge.merge(objs);
    }
    
}
