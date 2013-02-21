package org.nutz.dao.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.nutz.dao.entity.Entity;
import org.nutz.dao.jdbc.ValueAdaptor;
import org.nutz.dao.pager.Pager;

/**
 * 抽象 Dao 语句
 * <p>
 * 向调用者隔离了 Pojo 和 自定义 Sql 两种方式的差异
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface DaoStatement {

    /**
     * @return 当前语句是否是一个 SELECT 语句
     */
    boolean isSelect();

    /**
     * @return 当前语句是否是一个 UPDATE 语句
     */
    boolean isUpdate();

    /**
     * @return 当前语句是否是一个 DELETE 语句
     */
    boolean isDelete();

    /**
     * @return 当前语句是否是一个 INSERT 语句
     */
    boolean isInsert();

    /**
     * @return 当前语句是否是一个 CREATE 语句
     */
    boolean isCreate();

    /**
     * @return 当前语句是否是一个 DROP 语句
     */
    boolean isDrop();

    /**
     * @return 当前语句是否是一个 RUN 语句
     */
    boolean isRun();

    /**
     * @return 当前语句是否是一个 ALTER 语句
     */
    boolean isAlter();

    /**
     * @return 当前语句是否是一个 OTHER 语句
     */
    boolean isOther();
    
    boolean isExec();
    
    boolean isCall();

    /**
     * @return 当前语句所关联的实体
     */
    Entity<?> getEntity();

    /**
     * 设置 当前语句对应的实体
     * 
     * @param entity
     *            实体
     * @return 自身
     */
    DaoStatement setEntity(Entity<?> entity);

    /**
     * @return 返回执行的语句的类型
     */
    SqlType getSqlType();

    /**
     * 为本语句每一个参数提供一个适配器
     * 
     * @return JDBC 缓冲语句的参数适配器
     */
    ValueAdaptor[] getAdaptors();

    /**
     * 用一个矩阵描述语句的参数表。<br>
     * 这个参数矩阵将同 getAdaptors() 的返回组合使用
     * <p>
     * 矩阵的每一行相当于执行一条语句
     * <p>
     * 参数表的下标意义为： Object[行][列]
     * 
     * @return 语句参数表
     */
    Object[][] getParamMatrix();

    /**
     * 将 Dao 语句转换为 JDBC SQL 缓冲语句
     * 
     * @return JDBC SQL 缓冲语句
     */
    String toPreparedStatement();

    /**
     * 输出打印字符串
     * 
     * @return 日志打印字符串
     */
    String toString();

    /**
     * 你可以通过 setCallback 函数为本语句设置一个回调。
     * <p>
     * 在回调中，你可以返回一个对象。这个对象会存储在本语句中。 <br>
     * 当本语句 执行完毕，你可以通过这个函数获得回调函数生成的返回。
     * <p>
     * 一般的情况，回调函数是用来从 ResultSet 生成对象的。<br>
     * 即，如果 本语句不是 SELECT XXXX， 一般不会被设置回调
     * 
     * @return 执行结果。
     * 
     * @see org.nutz.dao.sql.SqlCallback
     */
    Object getResult();

    /**
     * <b>无结果的话,会抛NPE</b>
     * 
     * @return 将结果对象作为 int 返回
     */
    int getInt();

    /**
     * @return 将结果对象作为 String 返回
     */
    String getString();

    /**
     * <b>无结果的话,会抛NPE</b>
     * 
     * @return 将结果对象作为 boolean 返回
     */
    boolean getBoolean();

    /**
     * 一个 getResult() 函数的变种，将当前对象的 Result 转换成 List<T> 返回。<br>
     * 如果 Result 本身就是一个列表，如果第一个元素的类型和参数相符，则直接返回，<br>
     * 否则会被用 Castors 智能转换 如果不是列表，则会强制用 ArrayList 包裹
     * 
     * @param <T>
     *            列表容器內的元素类型
     * @param classOfT
     *            列表容器內的元素类型
     * @return 列表
     */
    <T> List<T> getList(Class<T> classOfT);

    /**
     * 转换结果对象到你想要的类型
     * 
     * @param <T>
     *            对象类型
     * @param classOfT
     *            对象类型
     * @return 对象
     */
    <T> T getObject(Class<T> classOfT);

    /**
     * @return 如果当前语句为 DELETE | UPDATE | INSERT，返回执行后所影响的记录数。否则返回 -1
     * 
     * @see org.nutz.dao.sql.SqlType
     */
    int getUpdateCount();

    /**
     * 获取 SQL 执行的上下文对象，以便做更多的操作
     * 
     * @return SQL 上下文对象
     */
    SqlContext getContext();

    /**
     * 语句执行之前的操作
     * <p>
     * 这个接口函数你基本不会直接使用的
     * 
     * @param conn
     *            当前执行语句的连接
     * 
     * @throws SQLException
     */
    void onBefore(Connection conn) throws SQLException;

    /**
     * 语句执行完毕的后续操作
     * <p>
     * 这个接口函数你基本不会直接使用的
     * 
     * @param conn
     *            当前执行语句的连接
     * @param rs
     *            当前语句执行的结果集
     * @throws SQLException
     *             回调函数抛出的异常
     */
    void onAfter(Connection conn, ResultSet rs) throws SQLException;

    DaoStatement setPager(Pager pager);
}
