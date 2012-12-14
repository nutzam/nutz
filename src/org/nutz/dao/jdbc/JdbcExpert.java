package org.nutz.dao.jdbc;

import java.sql.Connection;
import java.util.Map;

import org.nutz.dao.Dao;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.MappingField;
import org.nutz.dao.sql.DaoStatement;
import org.nutz.dao.sql.Pojo;
import org.nutz.dao.sql.SqlType;

/**
 * 封装各个数据库 JDBC 驱动的不同
 * <p>
 * 这个接口的实现类的实例会被 Nutz.Dao 长期持有，所有请保证其线程安全
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface JdbcExpert {

    /**
     * @return 当前的配置信息
     */
    Map<String, Object> getConf();

    /**
     * @return 当前数据库类型，名称参见 DB 这个枚举类型
     * 
     * @see org.nutz.dao.DB
     */
    String getDatabaseType();

    /**
     * 根据类型创建一个 SQL 语句的实例
     * 
     * @param type
     *            POJO 语句的 SQL 类型
     * 
     * @return 创建本数据库特有的 POJO 语句实现类
     * 
     * @see org.nutz.dao.sql.SqlType
     */
    Pojo createPojo(SqlType type);

    /**
     * 根据实体信息，返回某实体的建表语句
     * 
     * @param en
     *            实体
     * @return 是否创建成功
     */
    boolean createEntity(Dao dao, Entity<?> en);

    /**
     * 根据实体信息，返回某实体的删表语句
     * 
     * @param en
     *            实体
     * @return 是否删除成功
     */
    boolean dropEntity(Dao dao, Entity<?> en);

    /**
     * 根据字段类型为其获取一个字段适配器
     * 
     * @param ef
     *            实体数据库映射字段
     * 
     * @return ValueAdaptor 工厂类的实例
     */
    ValueAdaptor getAdaptor(MappingField ef);

    /**
     * 通过访问数据库，为实体的映射字段设置约束
     * <p>
     * 实体类在解析的时候会用到这个函数
     * 
     * @param conn
     *            数据库连接
     * @param en
     *            实体
     */
    void setupEntityField(Connection conn, Entity<?> en);

    /**
     * 根据 Dao 查询语句，以及其翻页信息，对其进行格式化
     * 
     * @param daoStatement
     *            Dao 语句
     */
    void formatQuery(DaoStatement daoStatement);

    Pojo fetchPojoId(Entity<?> en ,MappingField idField);
    
    boolean isSupportAutoIncrement();
}
