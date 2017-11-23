package org.nutz.dao.sql;

import org.nutz.dao.Condition;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.Record;
import org.nutz.dao.jdbc.ValueAdaptor;

/**
 * 封装了自定义 SQL
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 */
public interface Sql extends DaoStatement {

    /**
     * 所谓"变量"，就是当 Sql 对象转换成 Statement 对象前，预先被填充的占位符。
     * <p>
     * 这个集合允许你为 SQL 的变量占位符设值
     * 
     * @return 变量集合
     */
    VarSet vars();
    
    /**
     * sql.vars().set(name, value)的链式调用方式
     * @param name 变量名称
     * @param value 变量值
     * @return 原Sql对象,用于链式调用
     * @see #vars()
     */
    Sql setVar(String name, Object value);

    /**
     * 所谓"参数"，就是当 Sql 对象转换成 PreparedStatement 对象前，会被填充成 ? 的占位符
     * <p>
     * 集合是一个个的名值对，你设置了值的地方，会在执行时，被设置到 PreparedStatement中。<br>
     * 这样省却了你一个一个计算 ? 位置的烦恼
     * 
     * @return 参数集合
     */
    VarSet params();
    
    /**
     * sql.params().set(name, value)的链式调用方式
     * @param name 参数名称
     * @param value 参数值
     * @return 原Sql对象,用于链式调用
     * @see #params()
     */
    Sql setParam(String name, Object value);

    /**
     * 手动为某个语句参数设置适配器。
     * <p>
     * 默认的，Sql 的实现类会自动根据你设置的值，自动为所有的参数设置适配器。<br>
     * 但是，有些时候，你可能传入了 null 值或者其他的特殊对象，<br>
     * 这里允许你主动为其设置一个适配器，这样你就有了终极手段最合理的适配你的参数对象
     * 
     * @param name
     *            对应参数的名称
     * @param adaptor
     *            适配器实例
     */
    void setValueAdaptor(String name, ValueAdaptor adaptor);

    /**
     * @return 整个 SQL 的变量索引，你可以获得变量的个数和名称
     */
    VarIndex varIndex();

    /**
     * @return 整个 SQL 的参数索引，你可以获得参数的个数和名称
     */
    VarIndex paramIndex();

    /**
     * 将当前的参数列表存储，以便执行批处理
     */
    void addBatch();

    /**
     * 清除所有的曾经设置过的参数
     */
    void clearBatch();

    /**
     * 重写父接口返回值
     */
    Sql setEntity(Entity<?> entity);

    /**
     * 当前 Statement 被执行完毕后，有可能会产生一个 ResultSet。 针对这个 ResultSet 你可以执行更多的操作。
     * <p>
     * 当然如果不是 SELECT 语句，那么你依然可以设置一个回调，<br>
     * 当你的语句执行完毕后， 会调用它（Connection 不会被 commit），但是 ResultSet 参数会是 null
     * 
     * @param callback
     *            回调函数
     * @return 自身
     */
    Sql setCallback(SqlCallback callback);

    /**
     * 为 SQL 增加条件，SQL 必须有 '$condition' 变量
     * 
     * @param cnd
     *            条件
     * @return 自身
     */
    Sql setCondition(Condition cnd);

    /**
     * @return 一个新的和当前对象一样的对象。只是原来设置的变量和参数，需要你重新设置
     */
    Sql duplicate();

    /**
     * 设置原始SQL,将重新解析里面的占位符
     * @param sql
     */
    void setSourceSql(String sql);
    
    /**
     * 获取原始SQL
     */
    String getSourceSql();
    
    /**
     * 获取存储过程的出参
     */
    Record getOutParams();
    
    /**
     * 修改原有的占位符(默认是$和@),并重新解析.仅作用于当前SQL对象
     * @param param 参数占位符,默认是@
     * @param var 变量占位符,默认是$
     * @return 当前SQL对象
     */
    Sql changePlaceholder(char param, char var);
}
