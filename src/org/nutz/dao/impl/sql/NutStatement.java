package org.nutz.dao.impl.sql;

import java.util.List;

import org.nutz.castor.Castors;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.sql.DaoStatement;
import org.nutz.dao.sql.SqlContext;
import org.nutz.dao.sql.SqlType;
import org.nutz.lang.Strings;

public abstract class NutStatement implements DaoStatement {

	private Entity<?> entity;

	private SqlContext context;

	private SqlType sqlType;

	public NutStatement() {
		this.context = new SqlContext();
	}

	public Entity<?> getEntity() {
		return entity;
	}

	public DaoStatement setEntity(Entity<?> entity) {
		this.entity = entity;
		return this;
	}

	public SqlContext getContext() {
		return context;
	}

	public void setContext(SqlContext context) {
		this.context = context;
	}

	public SqlType getSqlType() {
		return sqlType;
	}

	public DaoStatement setSqlType(SqlType sqlType) {
		this.sqlType = sqlType;
		return this;
	}

	public Object getResult() {
		return context.getResult();
	}

	// TODO 是不是太暴力了涅~~~ --> 不是一般的暴力!!
	@SuppressWarnings("unchecked")
	public <T> List<T> getList(Class<T> classOfT) {
		return (List<T>) getResult();//TODO 考虑先遍历转换一次
	}

	public <T> T getObject(Class<T> classOfT) {
		return Castors.me().castTo(getResult(), classOfT);
	}

	public int getInt() {
		Integer i = getObject(Integer.class);
		if(i == null)
			return 0;//TODO 是不是应该抛出异常呢?
		return i;//TODO 怪怪的,如果getObject返回null,这里就NPE了 by zozoh
		         // 因为自动解包的原因,by wendal
	}

	public String getString() {
		return getObject(String.class);
	}

	public int getUpdateCount() {
		return context.getUpdateCount();
	}

	public String toString() {
		String sql = this.toPreparedStatement();
		StringBuilder sb = new StringBuilder(sql);

		// 准备打印参数表
		Object[][] mtrx = this.getParamMatrix();
		if (null != mtrx && mtrx.length > 0 && mtrx[0].length > 0) {
			// 计算每列最大宽度，以及获取列参数的内容
			int[] maxes = new int[mtrx[0].length];
			String[][] sss = new String[mtrx.length][mtrx[0].length];
			for (int row = 0; row < mtrx.length; row++)
				for (int col = 0; col < mtrx[0].length; col++) {
					String s = Strings.sNull(Castors.me().castToString(mtrx[row][col]), "NULL");
					maxes[col] = Math.max(maxes[col], s.length());
					sss[row][col] = s;
				}
			// 输出表头
			sb.append("\n    |");
			for (int i = 0; i < mtrx[0].length; i++) {
				sb.append(' ');
				sb.append(Strings.alignRight("" + (i + 1), maxes[i], ' '));
				sb.append(" |");
			}
			// 输出分隔线
			sb.append("\n    |");
			for (int i = 0; i < mtrx[0].length; i++) {
				sb.append('-');
				sb.append(Strings.dup('-', maxes[i]));
				sb.append("-|");
			}

			// 输出内容到字符串缓冲区
			for (int row = 0; row < mtrx.length; row++) {
				sb.append("\n    |");
				for (int col = 0; col < mtrx[0].length; col++) {
					sb.append(' ');
					sb.append(Strings.alignLeft(sss[row][col], maxes[col], ' '));
					sb.append(" |");
				}
			}

			// 输出可执行的 SQL 语句, TODO 格式非常不好看!!如果要复制SQL,很麻烦!!!
			sb.append("\n  For example:> \"");
			sb.append(toStatement(mtrx, sql));
			sb.append('"');
		}

		return sb.toString();
	}

	protected String toStatement(Object[][] mtrx, String sql) {
		StringBuilder sb = new StringBuilder();
		String[] ss = sql.split("[?]");
		int i;
		for (i = 0; i < mtrx[0].length; i++) {
			sb.append(ss[i]);
			sb.append(Sqls.formatFieldValue(mtrx[0][i]));
		}
		if (i < ss.length)
			sb.append(ss[i]);

		return sb.toString();
	}
}
