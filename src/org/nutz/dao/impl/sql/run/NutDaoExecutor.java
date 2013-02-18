package org.nutz.dao.impl.sql.run;

import static java.lang.String.format;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.nutz.dao.DaoException;
import org.nutz.dao.impl.DaoExecutor;
import org.nutz.dao.jdbc.ValueAdaptor;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.DaoStatement;
import org.nutz.dao.sql.SqlType;
import org.nutz.dao.util.Daos;
import org.nutz.lang.Lang;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class NutDaoExecutor implements DaoExecutor {

    private static final Log log = Logs.get();

    public void exec(Connection conn, DaoStatement st) {
        // 这个变量声明，后面两 case 要用到
        Object[][] paramMatrix;

        // 在这个块里执行语句
        try {
            /*
             * 语句执行前的预操作
             */
            st.onBefore(conn);
            /*
             * 开始执行语句
             */
            switch (st.getSqlType()) {
            // 查询
            case SELECT:
                _runSelect(conn, st);
                break;
            // 创建 & 删除 & 修改 & 清空
            case ALTER:
            case TRUNCATE:
            case CREATE:
            case DROP:
                _runStatement(conn, st);
                st.onAfter(conn, null);
                break;
            // 仅仅是运行回调
            case RUN:
                st.onAfter(conn, null);
                break;
            case CALL:
            case EXEC:
            	_runExec(conn, st);
            	break;
            // 插入 & 删除 & 更新
            // case DELETE:
            // case UPDATE:
            // case INSERT:
            // 见鬼了，未知类型，也当作普通 SQL 运行吧，见 Issue#13
            default:
                if (st.getSqlType() == SqlType.OTHER && log.isInfoEnabled())
                    log.info("Can't indentify SQL type :   " + st);
                paramMatrix = st.getParamMatrix();
                // 木有参数，直接运行
                if (null == paramMatrix || paramMatrix.length == 0) {
                    _runStatement(conn, st);
                }
                // 有参数，用缓冲语句
                else {
                    _runPreparedStatement(conn, st, paramMatrix);
                }
                // 运行回调
                st.onAfter(conn, null);
            }
        }
        // If any SQLException happend, throw out the SQL string
        catch (SQLException e) {
            if (log.isInfoEnabled())
                log.debug("SQLException", e);
            throw new DaoException(format(    "!Nutz SQL Error: '%s'\nPreparedStatement: \n'%s'",
                                            st.toString(),
                                            st.toPreparedStatement()), e);
        }

    }

    // 执行存储过程,简单实现
    protected void _runExec(Connection conn, DaoStatement st) throws SQLException {
		if (st.getContext().getPager() != null) {
			throw Lang.makeThrow(DaoException.class, "NOT Pageable : " + st);
		}
		
		// 打印调试信息
		String sql = st.toPreparedStatement();
        if (log.isDebugEnabled())
            log.debug(sql);
		
		Object[][] paramMatrix = st.getParamMatrix();
		
		CallableStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.prepareCall(sql);
			ValueAdaptor[] adaptors = st.getAdaptors();
			// 创建语句并设置参数
			if (paramMatrix != null && paramMatrix.length > 0) {
				for (int i = 0; i < paramMatrix[0].length; i++) {
			        adaptors[i].set((PreparedStatement) stmt,
			                paramMatrix[0][i], i + 1);
			    }
			}
			
			stmt.execute();
			
			//先尝试读取第一个,并调用一次回调
			rs = stmt.getResultSet();
			try {
				st.onAfter(conn, rs);
			}
			finally {
				if (rs != null)
					rs.close();
			}
			
			while (true) {
				if (stmt.getMoreResults()) {
					rs = stmt.getResultSet();
					try {
						st.onAfter(conn, rs);
					}
					finally {
						if (rs != null)
							rs.close();
					}
				// NOT support for this yet.  by wendal
				//} else if (stmt.getUpdateCount() > -1) {
				//	st.onAfter(conn, null);
				}
				break;
			}
		}
		finally {
			stmt.close();
		}
	}

	private void _runSelect(Connection conn, DaoStatement st)
            throws SQLException {

        Object[][] paramMatrix = st.getParamMatrix();
        // -------------------------------------------------
        // 以下代码,就为了该死的游标分页!!
        // -------------------------------------------------
        int startRow = -1;
        int lastRow = -1;
        if (st.getContext().getResultSetType() == ResultSet.TYPE_SCROLL_INSENSITIVE) {
            Pager pager = st.getContext().getPager();
            if (pager != null) {
                startRow = pager.getOffset();
                lastRow = pager.getOffset() + pager.getPageSize();
            }
        }
        // -------------------------------------------------
        // 生成 Sql 语句
        String sql = st.toPreparedStatement();
        // 打印调试信息
        
        ResultSet rs = null;
        Statement stat = null;
        try {

            // 木有参数，直接运行
            if (null == paramMatrix || paramMatrix.length == 0
                    || paramMatrix[0].length == 0) {
                if (log.isDebugEnabled())
                    log.debug(st);
                stat = conn.createStatement(st.getContext()
                        .getResultSetType(), ResultSet.CONCUR_READ_ONLY);
                if (lastRow > 0)
                    stat.setMaxRows(lastRow); // 游标分页,现在总行数
                if (st.getContext().getFetchSize() > 0)
                    stat.setFetchSize(st.getContext().getFetchSize());
                rs = stat.executeQuery(sql);
            }
            // 有参数，用缓冲语句
            else {

                // 打印调试信息
                if (paramMatrix.length > 1) {
                    if (log.isWarnEnabled())
                        log.warnf("Drop last %d rows parameters for:\n%s",
                                paramMatrix.length - 1, st);
                } 
                if (log.isDebugEnabled()) {
                    log.debug(st);
                }

                // 准备运行语句
                ValueAdaptor[] adaptors = st.getAdaptors();
                // 创建语句并设置参数
                stat = conn.prepareStatement(sql, st
                        .getContext().getResultSetType(),
                        ResultSet.CONCUR_READ_ONLY);
                if (lastRow > 0)
                    stat.setMaxRows(lastRow);
                for (int i = 0; i < paramMatrix[0].length; i++) {
                    adaptors[i].set((PreparedStatement) stat,
                            paramMatrix[0][i], i + 1);
                }
                rs = ((PreparedStatement) stat).executeQuery();
            }
            if (startRow > 0)
                rs.absolute(startRow);
            // 执行回调
            st.onAfter(conn, rs);
        } finally {
            Daos.safeClose(stat, rs);
        }
        // 打印更详细的调试信息
        if (log.isTraceEnabled())
            log.trace("...DONE");
    }

    private void _runPreparedStatement(Connection conn, DaoStatement st, Object[][] paramMatrix)
            throws SQLException {
        ValueAdaptor[] adaptors = st.getAdaptors();
        if (adaptors.length != paramMatrix[0].length)
            throw Lang.makeThrow("DaoStatement adaptor MUST same width with param matrix.");

        boolean statIsClosed = false;
        String sql = st.toPreparedStatement();
        PreparedStatement pstat = null;

        // 打印调试信息
        if (log.isDebugEnabled())
            log.debug(st);

        try {
            // 创建 SQL 语句
            pstat = conn.prepareStatement(sql);

            // 就一条记录，不要批了吧
            if (paramMatrix.length == 1) {
                for (int i = 0; i < paramMatrix[0].length; i++) {
                    adaptors[i].set(pstat, paramMatrix[0][i], i + 1);
                }
                pstat.execute();
                st.getContext().setUpdateCount(pstat.getUpdateCount());
                pstat.close();
                statIsClosed = true;
            }
            // 恩，批
            else {
                for (Object[] params : paramMatrix) {
                    for (int i = 0; i < params.length; i++) {
                        adaptors[i].set(pstat, params[i], i + 1);
                    }
                    pstat.addBatch();// 需要配置一下batchSize,嘻嘻,不然分分钟爆内存!!
                }
                int[] counts = pstat.executeBatch();

                // 计算总共影响的行数
                int sum = 0;
                for (int i : counts)
                    if (i > 0)
                        sum += i;
                        
                if (sum == 0)
                    sum = pstat.getUpdateCount();

                pstat.close();
                statIsClosed = true;
                
                st.getContext().setUpdateCount(sum);
            }
        }
        finally {
            if (!statIsClosed)
                Daos.safeClose(pstat);
        }

        // 打印更详细的调试信息
        if (log.isTraceEnabled())
            log.trace("...DONE");
    }

    private void _runStatement(Connection conn, DaoStatement st) throws SQLException {
        boolean statIsClosed = false;
        Statement stat = null;
        String sql = st.toPreparedStatement();

        // 打印调试信息
        if (log.isDebugEnabled())
            log.debug(sql);

        try {
            stat = conn.createStatement();
            stat.execute(sql);
            st.getContext().setUpdateCount(stat.getUpdateCount());
            stat.close();
            statIsClosed = true;
        }
        finally {
            if (!statIsClosed)
                Daos.safeClose(stat);
        }
        // 打印更详细的调试信息
        if (log.isTraceEnabled())
            log.trace("...DONE");
    }

}
