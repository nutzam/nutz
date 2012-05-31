package org.nutz.maplist;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;

import org.nutz.maplist.impl.MapListCell;
import org.nutz.maplist.impl.MapListMerge;
import org.nutz.maplist.impl.compile.ObjCompileImpl;
import org.nutz.maplist.impl.convert.FilterConvertImpl;
import org.nutz.maplist.impl.convert.ObjConvertImpl;
import org.nutz.maplist.impl.convert.StructureConvert;

/**
 * 集合了对象转换合并等高级操作
 * 
 * @author juqkai(juqkai@gmail.com)
 * 
 */
public class Maplist {
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
	public static Object maplistToObj(Object maplist, Type type) {
	    return new ObjConvertImpl(type).convert(maplist);
	}
	//------------------------------------------------------------------
	
    /**
     * 访问MAP, List结构的数据, 通过 uers[2].name 这种形式.
     */
    public static Object cell(Object maplist, String path){
        return MapListCell.cell(maplist, path);
    }
    
    //------------------------------------------------------------------

    /**
     * 转换器中间对象合并器<br/>
     * 合并 {@link Maplist} 中定义的中间结构.<br/>
     * 规则:<br>
     * <ul>
     * <li>普通对象, 保存为List, 但是要去掉重复.
     * <li>合并 map , 如果 key 值相同, 那么后一个值覆盖前面的值.递归合并
     * <li>list不做递归合并, 只做简单的合并, 清除重复的操作.
     * </ul>
     */
    public static Object merge(Object... maplist) {
        return MapListMerge.merge(maplist);
    }
    
    //------------------------------------------------------------------
    /**
     * MapList过滤器
     * @return 
     */
    public static Object includeFilter(Object maplist, List<String> paths){
        FilterConvertImpl filter = new FilterConvertImpl(paths);
        filter.useIncludeModel();
        return filter.convert(maplist);
    }
    /**
     * MapList过滤器
     * @return 
     */
    public static Object excludeFilter(Object maplist, List<String> paths){
        FilterConvertImpl filter = new FilterConvertImpl(paths);
        filter.useExcludeModel();
        return filter.convert(maplist);
    }
    
    //------------------------------------------------------------------
    
    /**
     * 结构转换, 详情参见: {@link StructureConvert}
     * @return
     */
    public static Object convert(Object maplist, Reader model){
        StructureConvert convert = new StructureConvert(model);
        return convert.convert(maplist);
    }
    /**
     * 结构转换, 详情参见: {@link StructureConvert}
     * @return
     */
    public static Object convert(Object maplist, Object model){
        StructureConvert convert = new StructureConvert(model);
        return convert.convert(maplist);
    }
    //------------------------------------------------------------------
    /**
     * 将对象转换成Maplist结构
     */
    public static Object toMaplist(Object obj){
        ObjCompileImpl convert = new ObjCompileImpl();
        return convert.parse(obj);
    }
}
