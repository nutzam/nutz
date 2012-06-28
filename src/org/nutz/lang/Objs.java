package org.nutz.lang;

import java.lang.reflect.Type;

import org.nutz.mapl.Mapl;

/**
 * 集合了对象转换合并等高级操作
 * 
 * 不再使用, 已将所有功能转移支 {@link Mapl}
 * @author juqkai(juqkai@gmail.com)
 * 
 */
@Deprecated
public class Objs {

    /**
     * 转换器中间对象合并器<br/>
     * 合并 {@link Objs} 中定义的中间结构.<br/>
     * 规则:<br>
     * <ul>
     * <li>普通对象, 保存为List, 但是要去掉重复.
     * <li>合并 map , 如果 key 值相同, 那么后一个值覆盖前面的值.递归合并
     * <li>list不做递归合并, 只做简单的合并, 清除重复的操作.
     * </ul>
     * * 请使用 {@link Mapl}.merge 方法
     */
    @Deprecated
    public static Object merge(Object... objs) {
        return Mapl.merge(objs);
    }

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
     * * 请使用 {@link Mapl}.maplistToObj 方法
     */
    @Deprecated
    public static Object convert(Object model, Type type) {
        return Mapl.maplistToObj(model, type);
    }
    
    //-------------------------------------提取数据-----------------------------------------
    /**
     * 访问MAP, List结构的数据, 通过 uers[2].name 这种形式.
     * 请使用 {@link Mapl}.cell 方法
     */
    @Deprecated
    public static Object cell(Object obj, String path){
        return Mapl.cell(obj, path);
    }
}
