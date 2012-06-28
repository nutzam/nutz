package org.nutz.ioc;

/**
 * 对象编织器
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface ObjectWeaver {

    /**
     * 根据容器构造时，为一个对象填充字段
     * 
     * @param ing
     *            容器构造时
     * @param obj
     *            对象，要被填充字段
     * 
     * @return 被填充后的字段
     */
    <T> T fill(IocMaking ing, T obj);

    /**
     * 根据自身内容创建一个对象，并触发创建事件
     * 
     * @param ing
     *            容器构造时
     */
    Object born(IocMaking ing);
    
    /**
     * 为对象触发 CREATE 事件
     * 
     * @param obj
     *            对象
     */
    Object onCreate(Object obj);

}
