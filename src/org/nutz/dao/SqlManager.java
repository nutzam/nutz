package org.nutz.dao;

import java.util.List;

import org.nutz.dao.sql.Sql;

/**
 * 自定 SQL 的管理接口。 <br>
 * 通常，你可以通过 Dao 接口 sqls() 方法获得这个接口
 * <p>
 * 你可以通过这个接口的实现类，将你的自定义 SQL 存放在文件或者数据库中
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * 
 * @see org.nutz.dao.Dao
 */
public interface SqlManager {

    /**
     * 获取一段 Sql 的原始文本
     * 
     * @param key
     *            Sql 的键值
     * @return Sql 的原始字符串
     * @throws SqlNotFoundException
     */
    String get(String key) throws SqlNotFoundException;

    /**
     * 创建一个 Sql 对象
     * 
     * @param key
     *            Sql 的键值
     * @return Sql 对象
     * @throws SqlNotFoundException
     */
    Sql create(String key) throws SqlNotFoundException;

    /**
     * 根据一组 Sql 的键值，创建一个 Sql 列表
     * 
     * @param keys
     *            键值数组
     * @return Sql 列表
     */
    List<Sql> createCombo(String... keys);

    /**
     * @return 本接口下共管理了多少条 Sql 语句
     */
    int count();

    /**
     * @return 一个包括所有 Sql 语句键值的数组
     */
    String[] keys();

    /**
     * 刷新缓存
     */
    void refresh();

    /**
     * 增加一条 Sql
     * 
     * @param key
     *            键值
     * @param value
     *            Sql 原始字符串
     */
    void addSql(String key, String value);

    /**
     * 移除一条 Sql
     * 
     * @param key
     *            键值
     */
    void remove(String key);

}
