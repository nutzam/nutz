package org.nutz.dao.impl.sql.pojo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.nutz.dao.entity.Entity;
import org.nutz.dao.pager.ResultSetLooping;
import org.nutz.dao.sql.Pojo;
import org.nutz.dao.sql.PojoCallback;
import org.nutz.dao.sql.SqlContext;
import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Lang;
import org.nutz.lang.Loop;
import org.nutz.lang.LoopException;

public class PojoEachEntityCallback implements PojoCallback {

    @SuppressWarnings("unchecked")
    public Object invoke(Connection conn, ResultSet rs, Pojo pojo) throws SQLException {
        // 得到回调
        final Each<Object> each = pojo.getContext().attr(Each.class);
        // 没有回调，什么都不用执行了
        if (null == each)
            return null;

        // 开始执行
        final Entity<?> en = pojo.getEntity();
        ResultSetLooping ing = new ResultSetLooping() {
            protected boolean createObject(int index, ResultSet rs, SqlContext context, int rowCount) {
                Object obj = en.getObject(rs, context.getFieldMatcher());
                try {
                    each.invoke(index, obj, rowCount);
                }
                catch (ContinueLoop e) {}
                catch (LoopException e) {
                    throw Lang.wrapThrow(e);
                }
                return false;
            }
        };
        try {
            // 循环开始
            if (each instanceof Loop)
                if (!((Loop<?>) each).begin())
                    return 0;
            // 循环中
            ing.doLoop(rs, pojo.getContext());

            // 循环结束
            if (each instanceof Loop)
                ((Loop<?>) each).end();
        }
        catch (ExitLoop e) {}
        catch (LoopException e) {
            SQLException e2 = new SQLException();
            e2.initCause(e.getCause());
            throw e2;
        }

        // 返回数量
        return ing.getIndex() + 1;

    }

}
