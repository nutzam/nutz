package org.nutz.dao.entity.next;

import org.nutz.dao.DatabaseMeta;
import org.nutz.dao.DB;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.EntityField;
import org.nutz.dao.entity.annotation.SQL;
import org.nutz.dao.sql.Sql;
import org.nutz.lang.Mirror;

/**
 * 创建二次查询的帮助函数。
 * <p>
 * <em>一般用户使用不到这里的方法</em>
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public abstract class FieldQuerys {

	/**
	 * 根据 '@SQL' 注解，实体字段以及数据库类型，为一个字段创建插入前后的二次查询。
	 * 
	 * @param meta
	 *            数据库元数据
	 * @param sqls
	 *            SQL 语句
	 * @param ef
	 *            实体字段
	 * @return 二次查询对象
	 */
	public static FieldQuery eval(DatabaseMeta meta, SQL[] sqls, EntityField ef) {
		if (null != sqls) {
			SQL query = null;
			for (SQL q : sqls) {
				if (q.db() == meta.getType()) {
					query = q;
					break;
				}
				if (q.db() == DB.OTHER)
					query = q;
			}
			if (null != query)
				return create(query.value(), ef);
		}
		return null;
	}

	/**
	 * 根据 SQL 语句，以及实体字段，为一个字段创建插入前后的二次查询。
	 * 
	 * @param sql
	 *            SQL 语句
	 * @param ef
	 *            实体字段
	 * @return 二次查询对象
	 */
	public static FieldQuery create(String sql, EntityField ef) {
		Sql oSql = Sqls.create(sql);
		Mirror<?> mirror = ef.getEntity().getMirror();
		if (mirror.isIntLike()) {
			oSql.setCallback(Sqls.callback.integer());
		} else {
			oSql.setCallback(Sqls.callback.str());
		}
		return new FieldQueryImpl(oSql, ef);
	}

}
