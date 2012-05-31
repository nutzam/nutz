package org.nutz.mapl;

/**
 * MapList转换器
 * @author juqkai(juqkai@gmail.com)
 */
public interface MaplConvert {
    /**
     * 转换
     * @param obj MapList结构对象
     * @return 转换后的结果
     */
    public Object convert(Object obj);
}
