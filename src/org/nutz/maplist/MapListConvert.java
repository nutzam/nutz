package org.nutz.maplist;

/**
 * MapList转换器
 * @author juqkai(juqkai@gmail.com)
 */
public interface MaplistConvert {
    /**
     * 转换
     * @param obj MapList结构对象
     * @return 转换后的结果
     */
    public Object convert(Object obj);
}
