package org.nutz.dao.impl.sql.pojo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.nutz.dao.entity.Record;
import org.nutz.dao.pager.ResultSetLooping;
import org.nutz.dao.sql.Pojo;
import org.nutz.dao.sql.PojoCallback;
import org.nutz.dao.sql.SqlContext;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;

public class PojoEachRecordCallback implements PojoCallback {

	@SuppressWarnings("unchecked")
	public Object invoke(Connection conn, ResultSet rs, Pojo pojo) throws SQLException {
		// 得到回调
		final Each<Object> each = pojo.getContext().attr(Each.class);
		// 没有回调，什么都不用执行了
		if (null == each)
			return null;
		// 开始执行
		ResultSetLooping ing = new ResultSetLooping() {
			protected boolean createObject(int index, ResultSet rs, SqlContext context, int rowCount) {
				Object obj = Record.create(rs);
				try {
					each.invoke(index, obj, rowCount);
				}
				catch (LoopException e) {
					throw Lang.wrapThrow(e);
				}
				return false;
			}
		};
		try {
			ing.doLoop(rs, pojo.getContext());
		}
		catch (ExitLoop e) {}

		// 返回数量
		return ing.getIndex() + 1;
	}

}
