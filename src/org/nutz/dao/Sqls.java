package org.nutz.dao;

import java.util.regex.Pattern;

import org.nutz.dao.entity.Entity;
import org.nutz.dao.sql.DefaultStatementAdapter;
import org.nutz.dao.sql.FetchEntityCallback;
import org.nutz.dao.sql.FetchIntegerCallback;
import org.nutz.dao.sql.FetchStringCallback;
import org.nutz.dao.sql.QueryEntityCallback;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlCallback;
import org.nutz.dao.sql.SqlImpl;
import org.nutz.dao.sql.SqlLiteral;
import org.nutz.lang.Mirror;

/**
 * 提供了 Sql 相关的帮助函数
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class Sqls {

	/**
	 * 创建了一个 Sql 对象。
	 * <p>
	 * 传入的 Sql 语句支持变量和参数占位符：
	 * <ul>
	 * <li>变量： 格式为 <b>$XXXX</b>，在执行前，会被预先替换
	 * <li>参数： 格式为<b>@XXXX</b>，在执行前，会替换为 '?'，用以构建 PreparedStatement
	 * </ul>
	 * 
	 * @param sql
	 *            Sql 语句
	 * @return Sql 对象
	 * 
	 * @see org.nutz.dao.sql.Sql
	 */
	public static Sql create(String sql) {
		return new SqlImpl(new SqlLiteral().valueOf(sql), DefaultStatementAdapter.ME);
	}

	/**
	 * 创建一个获取单个实体对象的 Sql。
	 * <p>
	 * 这个函数除了执行 create(String)外，还会为这个 Sql 语句设置回调，用来获取实体对象。
	 * <p>
	 * <b style=color:red>注意：</b>返回的 Sql 对象在执行前，一定要通过 setEntity 设置
	 * 一个有效的实体，否则，会抛出异常。
	 * 
	 * @param sql
	 *            Sql 语句
	 * @return Sql 对象
	 * 
	 * @see org.nutz.dao.sql.Sql
	 * @see org.nutz.dao.entity.Entity
	 */
	public static Sql fetchEntity(String sql) {
		return create(sql).setCallback(callback.entity());
	}

	/**
	 * 创建一个获取整数的 Sql。
	 * <p>
	 * 这个函数除了执行 create(String)外，还会为这个 Sql 语句设置回调，用来获取整数值。
	 * <p>
	 * <b style=color:red>注意：</b>你的 Sql 语句返回的 ResultSet 的第一列必须是数字
	 * 
	 * @param sql
	 *            Sql 语句
	 * @return Sql 对象
	 * 
	 * @see org.nutz.dao.sql.Sql
	 */
	public static Sql fetchInt(String sql) {
		return create(sql).setCallback(callback.integer());
	}

	/**
	 * 创建一个获取字符串的 Sql。
	 * <p>
	 * 这个函数除了执行 create(String)外，还会为这个 Sql 语句设置回调，用来获取字符串。
	 * <p>
	 * <b style=color:red>注意：</b>你的 Sql 语句返回的 ResultSet 的第一列必须是字符串
	 * 
	 * @param sql
	 *            Sql 语句
	 * @return Sql 对象
	 * 
	 * @see org.nutz.dao.sql.Sql
	 */
	public static Sql fetchString(String sql) {
		return create(sql).setCallback(callback.str());
	}

	/**
	 * 创建一个获取一组实体对象的 Sql。
	 * <p>
	 * 这个函数除了执行 create(String)外，还会为这个 Sql 语句设置回调，用来获取一组实体对象。
	 * <p>
	 * <b style=color:red>注意：</b>返回的 Sql 对象在执行前，一定要通过 setEntity 设置
	 * 一个有效的实体，否则，会抛出异常。
	 * 
	 * @param sql
	 *            Sql 语句
	 * @return Sql 对象
	 * 
	 * @see org.nutz.dao.sql.Sql
	 * @see org.nutz.dao.entity.Entity
	 */
	public static Sql queryEntity(String sql) {
		return create(sql).setCallback(callback.entities());
	}

	/**
	 * 一些内置的回调对象
	 */
	public final static CallbackFactory callback = new CallbackFactory();

	public static class CallbackFactory {
		/**
		 * @return 从 ResultSet获取一个对象的回调对象
		 */
		public SqlCallback entity() {
			return new FetchEntityCallback();
		}

		/**
		 * @return 从 ResultSet 获取一个整数的回调对象
		 */
		public SqlCallback integer() {
			return new FetchIntegerCallback();
		}

		/**
		 * @return 从 ResultSet 获取一个字符串的回调对象
		 */
		public SqlCallback str() {
			return new FetchStringCallback();
		}

		/**
		 * @return 从 ResultSet获取一组对象的回调对象
		 */
		public SqlCallback entities() {
			return new QueryEntityCallback();
		}
	}

	private static final Pattern CND = Pattern.compile("^([ \t]*)(WHERE|ORDER BY)(.+)$",
			Pattern.CASE_INSENSITIVE);

	/**
	 * @param en
	 *            实体
	 * @param condition
	 *            条件
	 * @return WHERE 子句
	 */
	public static String getConditionString(Entity<?> en, Condition condition) {
		if (null != condition) {
			String cnd = condition.toSql(en);
			if (cnd != null) {
				if (!CND.matcher(cnd).find())
					return " WHERE " + cnd;
				return cnd;
			}
		}
		return null;
	}

	/**
	 * @param v
	 *            字段值
	 * @return 格式化后的 Sql 字段值，可以直接拼装在 SQL 里面
	 */
	public static CharSequence formatFieldValue(Object v) {
		if (null == v)
			return "NULL";
		else if (Sqls.isNotNeedQuote(v.getClass()))
			return Sqls.escapeFieldValue(v.toString());
		else
			return new StringBuilder("'").append(Sqls.escapeFieldValue(v.toString())).append('\'');
	}

	/**
	 * 将 SQL 的字段值进行转意，可以用来防止 SQL 注入攻击
	 * 
	 * @param s
	 *            字段值
	 * @return 格式化后的 Sql 字段值，可以直接拼装在 SQL 里面
	 */
	public static CharSequence escapeFieldValue(CharSequence s) {
		if (null == s)
			return null;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c == '\'')
				sb.append('\'').append('\'');
			else if (c == '\\')
				sb.append('\\').append('\\');
			else
				sb.append(c);
		}
		return sb;
	}

	/**
	 * 将 SQL 的 WHERE 条件值进行转意，可以用来防止 SQL 注入攻击
	 * 
	 * @param s
	 *            字段值
	 * @return 格式化后的 Sql 字段值，可以直接拼装在 SQL 里面
	 */
	public static CharSequence escapteConditionValue(CharSequence s) {
		if (null == s)
			return null;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c == '\'')
				sb.append('\'').append('\'');
			else if (c == '\\')
				sb.append('\\').append('\\');
			else if (c == '_')
				sb.append('\\').append(c);
			else if (c == '%')
				sb.append('\\').append(c);
			else
				sb.append(c);
		}
		return sb;
	}

	/**
	 * 判断一个值，在 SQL 中是否需要单引号
	 * 
	 * @param type
	 *            类型
	 * @return 是否需要加上单引号
	 */
	public static boolean isNotNeedQuote(Class<?> type) {
		Mirror<?> me = Mirror.me(type);
		return me.isBoolean() || me.isPrimitiveNumber();
	}
}
