package org.nutz.dao;

import org.nutz.dao.entity.Entity;

/**
 * 获得 WHERE 条件。其中也可以包括 ORDER BY 和 GROUP BY 甚至任何你认为你的数据库可以支持的东西。 Nutz.Dao
 * 只会老老实实的将你返回的字符串拼接在 WHERE 后面
 * <p>
 * Nutz.Dao 默认提供给你 Cnd 类，便于你快速构建你的条件语句。
 * <p>
 * 这个接口也提供另外一种可能: <br>
 * 比如你的 Web 应用，可以通过 Request，根据用户提交的数据 生成 一个这个接口的实例。这个过程你可以写的很通用。
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * 
 * @see org.nutz.dao.Cnd
 */
public interface Condition {

    /**
     * 根据一个实体，你可以生成一个条件，这个条件就是 SQL 'WHERE' 关键字后面的那部分。
     * <p>
     * 当然你返回的字符串如果不是以 WHERE 或者 ORDER BY 开头，将会被 Nutz.Dao 加上 WHERE。
     * <p>
     * 你的字符串前后的空白会被截除
     * 
     * @param entity
     *            实体
     * @return 条件字符串
     */
    String toSql(Entity<?> entity);

}
