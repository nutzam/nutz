package org.nutz.dao.sql;

import org.nutz.dao.entity.Entity;
import org.nutz.dao.jdbc.ValueAdaptor;

/**
 * Pojo 语句的组成元素，比如字段，条件，等
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface PItem {

    /**
     * 设置所属的 POJO 对象
     * 
     * @param pojo
     *            所属的 POJO 对象
     */
    void setPojo(Pojo pojo);

    /**
     * @return 获得所属的 POJO 语句
     */
    Pojo getPojo();

    /**
     * 将当前的语句组成元素输出，以便组成 PreparedStatement 语句
     * 
     * @param en
     *            参考的实体，如果为 null，则取当前元素所在 POJO 的关联实体
     * @param sb
     *            文本缓冲
     */
    void joinSql(Entity<?> en, StringBuilder sb);

    /**
     * 根据自身的元素内容，为适配器数组填充适配器
     * 
     * @param en
     *            参考的实体，如果为 null，则取当前元素所在 POJO 的关联实体
     * @param adaptors
     *            待填充的适配器数组
     * @param off
     *            开始下标
     * @return 结束后，下一项开始的下标
     */
    int joinAdaptor(Entity<?> en, ValueAdaptor[] adaptors, int off);

    /**
     * 根据自身的元素内容，为参数数组填充参数
     * 
     * @param en
     *            参考的实体，如果为 null，则取当前元素所在 POJO 的关联实体
     * @param obj
     *            当前操作对象
     * @param params
     *            待填充的参数数组
     * @param off
     *            开始下标
     * @return 结束后，下一项开始的下标
     */
    int joinParams(Entity<?> en, Object obj, Object[] params, int off);

    /**
     * @param en
     *            参考的实体，如果为 null，则取当前元素所在 POJO 的关联实体
     * 
     * @return 本语句元素所包含的参数数目
     */
    int paramCount(Entity<?> en);

    /**
     * @return 当前语句组成元素的日志打印字符串
     */
    String toString();

}
