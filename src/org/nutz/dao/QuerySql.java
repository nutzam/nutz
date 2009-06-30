package org.nutz.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.nutz.dao.callback.Callback;

public class QuerySql<T> extends ConditionSql<List<T>, T, ResultSet> {

	public QuerySql() {
		super();
	}

	public QuerySql(String sql) {
		super(sql);
	}

	// private QueryCallback<T> queryCallback;
	//
	// public QuerySql<T> setCallback(QueryCallback<T>
	// callback) {
	// this.queryCallback = callback;
	// return this;
	// }

	private Pager pager;

	public Pager getPager() {
		return pager;
	}

	private FieldMatcher matcher;

	void setMatcher(FieldMatcher fm) {
		this.matcher = fm;
	}

	void setPager(Pager pager) {
		this.pager = pager;
	}

	@Override
	public List<T> execute(Connection conn) throws Exception {
		setResult(execute(conn, callback));
		return this.getResult();
	}

	protected List<T> execute(Connection conn, Callback<T, ResultSet> callback) throws SQLException {
		PreparedStatement stat = null;
		try {
			List<T> list = new LinkedList<T>();
			stat = conn.prepareStatement(getPreparedStatementString(),
					ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			super.setupStatement(stat);
			ResultSet rs = stat.executeQuery();
			callback.getContext().setFieldsMatcher(matcher);
			if (null == pager) {
				while (rs.next()) {
					list.add(callback.invoke(rs));
				}
			} else if (rs.last()) {
				if (pager.getPageSize() > 1000)
					rs.setFetchSize(20);
				else
					rs.setFetchSize(pager.getPageSize());
				LoopScope ls = evaluateLoopScopeAndPager(pager, rs.getRow());
				if (rs.absolute(ls.start + 1))
					for (int i = ls.start; i < ls.max; i++) {
						T o = callback.invoke(rs);
						list.add(o);
						if (!rs.next())
							break;
					}
			}
			rs.close();
			return list;
		} catch (Exception e) {
			throw new DaoException(this, e);
		} finally {
			if (null != stat)
				try {
					stat.close();
				} catch (SQLException e1) {}
		}
	}

	private static LoopScope evaluateLoopScopeAndPager(Pager pager, int len) {
		LoopScope ls = new LoopScope();
		if (null == pager) {
			ls.start = 0;
			ls.max = len;
		} else {
			pager.setRecordCount(len);
			ls.start = pager.getOffset();
			ls.max = ls.start + pager.getPageSize();
		}
		return ls;
	}

	private static class LoopScope {
		public int start; // inclusive
		public int max; // exclusive

		@Override
		public String toString() {
			return "[" + start + "," + max + "]";
		}
	}

}
