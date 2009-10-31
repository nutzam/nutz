package org.nutz.dao.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.nutz.dao.Condition;
import org.nutz.dao.entity.Entity;

/**
 * 为了将来构建性能更好的 Sql 解析和执行的实现，创建此接口
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface Sql {

	/**
	 * 所谓"变量"，就是当 Sql 对象转换成 Statement 对象前，预先被填充的占位符
	 * 
	 * @return 变量集合
	 */
	VarSet vars();

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
	 * 上下文对象，随着 Sql 对象的创建而创建，随着其消亡而消亡。
	 * 
	 * @return 上下文对象
	 */
	SqlContext getContext();

	/**
	 * 当执行完查询后，你还需要做些什么。尤其是 SELECT 语句，你需要在回调里从 ResultSet 取出<br>
	 * 查询的结果，记录到你的 JAVA 对象中。
	 * 
	 * @param callback
	 *            回调函数
	 * @return 自身
	 */
	Sql setCallback(SqlCallback callback);

	/**
	 * 条件，当主要用来生成 WHERE 后面的那段 SQL
	 * 
	 * @param condition
	 *            条件对象
	 * @return 自身
	 * @see org.nutz.dao.Condition
	 */
	Sql setCondition(Condition condition);

	/**
	 * 执行本 Sql
	 * 
	 * @param conn
	 *            数据库连接
	 * @throws SQLException
	 */
	void execute(Connection conn) throws SQLException;

	/**
	 * @return 执行结果。由你设置的 callback 生成
	 * @see org.nutz.dao.Sql.setCallback
	 * @see org.nutz.dao.sql.SqlCallback
	 */
	Object getResult();

	/**
	 * @return 当前 Sql 所对应的实体
	 * @see org.nutz.dao.entity.Entity
	 */
	Entity<?> getEntity();

	/**
	 * 设置语句适配器
	 * 
	 * @param adapter
	 *            语句适配器
	 * @return 自身
	 */
	Sql setAdapter(StatementAdapter adapter);

	/**
	 * 设置 当前 Sql 对应的实体
	 * 
	 * @param entity
	 *            实体
	 * @return 自身
	 */
	Sql setEntity(Entity<?> entity);

	/**
	 * @return 如果当前 Sql 为 DELETE | UPDATE | INSERT，返回执行后所影响的记录数。否则返回 -1
	 */
	int getUpdateCount();

	/**
	 * @return 一个新的和当前对象一样的对象。只是原来设置的变量和参数，需要你重新设置
	 */
	Sql duplicate();

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
	 * @return 将结果对象作为 int 返回
	 */
	int getInt();

}
