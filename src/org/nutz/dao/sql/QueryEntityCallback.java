package org.nutz.dao.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.nutz.dao.pager.Pager;
import org.nutz.dao.entity.Entity;

public class QueryEntityCallback extends EntityCallback {

	@Override
	protected Object process(ResultSet rs, Entity<?> entity, SqlContext context)
			throws SQLException {
		Pager pager = context.getPager();
		List<Object> list = new LinkedList<Object>();
		if (null == rs)
			return list;
		/**
		 * 如果没有设置 Pager 或者 rs 的类型是 ResultSet.TYPE_FORWARD_ONLY，那么<br>
		 * 无法利用 游标的滚动 来计算结果集合大小。这比较高效，但是如果使用者希望得到页数量，<br>
		 * 需要为 Pager 另行计算 总体的结果集大小。
		 * <p>
		 * 一般的，为特殊数据建立的 Pager，生成的 ResultSet 类型应该是 TYPE_FORWARD_ONLY
		 */
		if (null == pager || ResultSet.TYPE_FORWARD_ONLY == rs.getType()) {
			while (rs.next()) {
				list.add(entity.getObject(rs, context.getMatcher()));
			}
		}
		/**
		 * 如果进行到了这个分支，则表示，整个查询的 Pager 是不区分数据库类型的。 <br>
		 * 并且 ResultSet 的游标是可以来回滚动的。
		 * <p>
		 * 所以我就会利用游标的滚动，为你计算整个结果集的大小。比较低效，在很小<br>
		 * 数据量的时候 还是比较有用的
		 */
		else if (rs.last()) {
			if (pager.getPageSize() > 1000)
				rs.setFetchSize(20);
			else
				rs.setFetchSize(pager.getPageSize());
			LoopScope ls = LoopScope.eval(pager, rs.getRow());
			if (rs.absolute(ls.start + 1))
				for (int i = ls.start; i < ls.max; i++) {
					Object o = entity.getObject(rs, context.getMatcher());
					list.add(o);
					if (!rs.next())
						break;
				}
		}
		return list;
	}

}
