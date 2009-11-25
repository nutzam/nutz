package org.nutz.dao.entity;

import java.sql.Connection;

import org.nutz.dao.DatabaseMeta;

/**
 * 封装了 Entity 的创建逻辑
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface EntityMaker {

	/**
	 * 创建一个 Entity
	 * 
	 * @param db
	 *            数据库
	 * @param conn
	 *            连接。<b style=color:red>注意：</b> 请不要关闭这个连接，调用者会确保其关闭的
	 * @param type
	 *            POJO 类型
	 * @return 实体
	 */
	Entity<?> make(DatabaseMeta db, Connection conn, Class<?> type);

}
