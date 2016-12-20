package org.nutz.dao.impl.sql.run;

import static java.lang.String.format;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map.Entry;

import org.nutz.dao.DaoException;
import org.nutz.dao.DatabaseMeta;
import org.nutz.dao.entity.Record;
import org.nutz.dao.impl.DaoExecutor;
import org.nutz.dao.jdbc.JdbcExpert;
import org.nutz.dao.jdbc.ValueAdaptor;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.DaoStatement;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlType;
import org.nutz.dao.sql.VarIndex;
import org.nutz.dao.sql.VarSet;
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
                break;
            // 仅仅是运行回调
            case RUN:
                st.onAfter(conn, null, null);
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
            	if (st.isForceExecQuery()) {
            		// run as select
            		_runSelect(conn, st);
                    break;
            	}
                if (st.getSqlType() == SqlType.OTHER && log.isInfoEnabled())
                    log.info("Can't identify SQL type :   " + st);
                paramMatrix = st.getParamMatrix();
                // 木有参数，直接运行
                if (null == paramMatrix || paramMatrix.length == 0) {
                    _runStatement(conn, st);
                }
                // 有参数，用缓冲语句
                else {
                    _runPreparedStatement(conn, st, paramMatrix);
                }
            }
        }
        // If any SQLException happend, throw out the SQL string
        catch (SQLException e) {
            if (log.isDebugEnabled()) {
            	log.debug("SQLException", e);
            	SQLException nextException = e.getNextException();
                if (nextException != null)
                	log.debug("SQL NextException", nextException);
            }
            throw new DaoException(format(    "!Nutz SQL Error: '%s'\nPreparedStatement: \n'%s'",
                                            st.toString(),
                                            st.toPreparedStatement()) + "\nCaseMessage=" + e.getMessage(), e);
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
            HashMap<Integer, OutParam> outParams = new HashMap<Integer, OutParam>();
            if (st instanceof Sql) {
                VarIndex varIndex = ((Sql) st).paramIndex();
                VarSet varSet = ((Sql) st).params();
                for (int i = 0; i < varIndex.size(); i++) {
                    String name = varIndex.getOrderName(i);
                    if (name.startsWith("OUT") && varSet.get(name).getClass() == Integer.class) {
                        Integer t = (Integer) varSet.get(name);
                        outParams.put(i, new OutParam(name, t));
                    }
                }
            }
			// 创建语句并设置参数
            if (paramMatrix != null && paramMatrix.length > 0) {
                PreparedStatement pst = (PreparedStatement) stmt;
                Object[] pm = paramMatrix[0];
                for (int i = 0; i < pm.length; i++) {
                    OutParam outParam = outParams.get(i);
                    if (outParam == null)
                        adaptors[i].set(pst, pm[i], i + 1);
                    else
                        stmt.registerOutParameter(i + 1, outParam.jdbcType);
                }
            }

			stmt.execute();

            if (outParams.size() > 0) {
                Record r = Record.create();
                for (Entry<Integer, OutParam> en : outParams.entrySet()) {
                    OutParam outParam = en.getValue();
                    int jdbcIndex = en.getKey() + 1;
                    Object value;
                    switch (outParam.jdbcType) {
                    case Types.INTEGER:
                        value = stmt.getInt(jdbcIndex);
                        break;
                    case Types.TIMESTAMP:
                        value = stmt.getTimestamp(jdbcIndex);
                        break;
                    case Types.CLOB:
                        value = stmt.getString(jdbcIndex);
                        break;
                    case Types.DATE:
                        value = stmt.getDate(jdbcIndex);
                        break;
                    default:
                        value = stmt.getObject(jdbcIndex);
                        break;
                    }
                    r.set(outParam.name.substring(3), value);
                }
                st.getContext().attr("OUT", r);
            }
			//先尝试读取第一个,并调用一次回调
			rs = stmt.getResultSet();
			try {
				st.onAfter(conn, rs, null);
			}
			finally {
				if (rs != null)
					rs.close();
			}
			
			while (true) {
				if (stmt.getMoreResults()) {
					rs = stmt.getResultSet();
					try {
						if (rs != null)
							st.onAfter(conn, rs, null);
					}
					finally {
						if (rs != null)
							rs.close();
					}
				}
				break;
			}
		}
		finally {
			if (stmt != null)
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
                stat = conn.createStatement(st.getContext()
                        .getResultSetType(), ResultSet.CONCUR_READ_ONLY);
                if (lastRow > 0)
                    stat.setMaxRows(lastRow); // 游标分页,现在总行数
                if (st.getContext().getFetchSize() != 0)
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

                // 准备运行语句
                ValueAdaptor[] adaptors = st.getAdaptors();
                // 创建语句并设置参数
                stat = conn.prepareStatement(sql, st
                        .getContext().getResultSetType(),
                        ResultSet.CONCUR_READ_ONLY);
                if (lastRow > 0)
                    stat.setMaxRows(lastRow);
                if (st.getContext().getFetchSize() != 0)
                    stat.setFetchSize(st.getContext().getFetchSize());
                for (int i = 0; i < paramMatrix[0].length; i++) {
                    adaptors[i].set((PreparedStatement) stat,
                            paramMatrix[0][i], i + 1);
                }
                rs = ((PreparedStatement) stat).executeQuery();
            }
            if (startRow > 0)
                rs.absolute(startRow);
            // 执行回调
            st.onAfter(conn, rs, stat);
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

        try {
            // 创建 SQL 语句
        	if (st.getContext().attr("RETURN_GENERATED_KEYS") == null)
        		pstat = conn.prepareStatement(sql);
        	else
        		pstat = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            // 就一条记录，不要批了吧
            if (paramMatrix.length == 1) {
                for (int i = 0; i < paramMatrix[0].length; i++) {
                    adaptors[i].set(pstat, paramMatrix[0][i], i + 1);
                }
                pstat.execute();
                st.getContext().setUpdateCount(pstat.getUpdateCount());
                st.onAfter(conn, null, pstat);
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

                st.onAfter(conn, null, pstat);
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

        try {
            stat = conn.createStatement();
            stat.execute(sql);
            st.getContext().setUpdateCount(stat.getUpdateCount());
            st.onAfter(conn, null, stat);
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
    
    protected DatabaseMeta meta;
    
    protected JdbcExpert expert;
    
    public void setMeta(DatabaseMeta meta) {
		this.meta = meta;
	}
    
    public void setExpert(JdbcExpert expert) {
		this.expert = expert;
	}
    
    // 写在这里完全是为了兼容老版本的log4j配置
    public static void printSQL(DaoStatement sql) {
        // 打印调试信息
        if (log.isDebugEnabled())
            log.debug(sql.forPrint());
    }
    
    static class OutParam implements Serializable {
        private static final long serialVersionUID = 1L;
        String name;
        int jdbcType;
        public OutParam() {}
        public OutParam(String name, int jdbcType) {
            super();
            this.name = name;
            this.jdbcType = jdbcType;
        }
    }
}
