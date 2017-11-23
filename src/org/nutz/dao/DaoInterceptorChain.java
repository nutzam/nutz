package org.nutz.dao;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.nutz.dao.impl.DaoExecutor;
import org.nutz.dao.sql.DaoStatement;
import org.nutz.dao.sql.SqlContext;
import org.nutz.lang.random.R;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * Dao执行拦截器链.
 * 
 * @author wendal
 * @see org.nutz.dao.impl.interceptor.DaoLogInterceptor
 * @see org.nutz.dao.impl.interceptor.DaoTimeInterceptor
 */
public class DaoInterceptorChain implements ConnCallback {

    private static final Log log = Logs.get();

    protected int autoTransLevel;

    protected Connection connection;

    protected int current = 0;

    protected DaoStatement daoStatement;

    protected DaoExecutor executor;

    protected List<DaoInterceptor> interceptors = new ArrayList<DaoInterceptor>();

    protected int updateCount;

    protected DaoStatement[] sts;

    protected String id;

    /**
     * 新建一个DaoInterceptorChain.
     * 
     * @param sts
     *            将要进行的Dao操作(不一定是SQL操作,有可能是EL)
     */
    public DaoInterceptorChain(DaoStatement... sts) {
        this.sts = sts;
        id = R.UU32();
    }

    /**
     * 继续下一个拦截器,如果已经是最后一个拦截器,那么执行executor.exec
     * 
     * @return 本对象,用于链式操作
     * @throws Exception
     */
    public DaoInterceptorChain doChain() throws DaoException {
        if (hasNext()) {
            DaoInterceptor interceptor = next();
            current++;
            interceptor.filter(this);
        } else {
            executor.exec(getConnection(), getDaoStatement());
            updateCount += getDaoStatement().getUpdateCount();
        }
        return this;
    }

    /**
     * 获取当前自动事务级别,DaoRunner中使用强制事务时会使用之.拦截器不能修改,即使修改也不会生效
     * 
     * @return 当前自动(强制)事务级别
     */
    public int getAutoTransLevel() {
        return autoTransLevel;
    }

    /**
     * 当前执行的DaoStatement
     * 
     * @return 当前执行的DaoStatement
     */
    public DaoStatement getDaoStatement() {
        return daoStatement;
    }

    /**
     * 全部DaoStatement,可能不止一条
     * 
     * @return 全部DaoStatement
     */
    public DaoStatement[] getDaoStatements() {
        return sts;
    }

    /**
     * 拦截器列表(暂不开放修改)
     * 
     * @return 全体拦截器列表
     */
    public List<DaoInterceptor> getInterceptors() {
        return interceptors;
    }

    /**
     * 更新总数,用于DaoSupport(NutDao)获取更新总数.
     * 
     * @return 更新记录总数
     */
    public int getUpdateCount() {
        return updateCount;
    }

    /**
     * 是否还有下一个拦截器
     * 
     * @return true,如果还有拦截器要执行
     */
    public boolean hasNext() {
        return current < interceptors.size();
    }

    /**
     * 这是DaoExecutor会执行的方法,拦截器内不要执行这个方法!! 这里也是拦截器开始生效的地方.
     */
    public void invoke(Connection conn) throws Exception {
        for (DaoStatement st : sts) {
            if (st == null) {
                if (log.isInfoEnabled())
                    log.info("Found a null DaoStatement(SQL), ingore it ~~");
                continue;
            }
            current = 0;
            daoStatement = st;
            this.connection = conn;
            doChain();
        }
    }

    /**
     * 获取下一个拦截器. 调用前必须先调用hasNext进行判断
     * 
     * @return 下一个拦截器
     */
    public DaoInterceptor next() {
        return interceptors.get(current);
    }

    /**
     * 设置强制事务的级别,对拦截器来说无意义.
     * 
     * @param autoTransLevel
     *            与DaoSupport(NutDao)内的值一致
     */
    public void setAutoTransLevel(int autoTransLevel) {
        this.autoTransLevel = autoTransLevel;
    }

    /**
     * 设置当前拦截器索引. 若设置值大于拦截器列表的大小,那么效果就等同于跳过剩余拦截器,直接执行DaoStatement
     * 
     * @param current
     */
    public void setCurrent(int current) {
        this.current = current;
    }

    /**
     * 设置DaoExecutor. 典型应用是在拦截器中替换成daocache提供的DaoExecutor
     * 
     * @param executor
     *            新的DaoExecutor,不可以是null
     */
    public void setExecutor(DaoExecutor executor) {
        this.executor = executor;
    }

    /**
     * 设置新的拦截器列表.
     * 
     * @param interceptors
     *            新的拦截器列表
     */
    public void setInterceptors(List<DaoInterceptor> interceptors) {
        this.interceptors = interceptors;
    }

    /**
     * 设置当前使用的数据库连接
     * 
     * @param connection
     *            新的数据库连接,不可以是null
     */
    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    /**
     * 获取当前数据库连接
     * 
     * @return 当前数据库连接
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * 获取当前DaoStatement的上下文,注意,一个拦截器链可能包含多个DaoStatement
     * 
     * @return 当前DaoStatement的上下文
     */
    public SqlContext getSqlContext() {
        return getDaoStatement().getContext();
    }

    /**
     * 拦截器链的id, 为一个uu32识别符.
     * 
     * @return 本拦截器链的id
     */
    public String getId() {
        return id;
    }
    
    /**
     * 替换当前执行的DaoStatement
     * @param daoStatement
     */
    public void setDaoStatement(DaoStatement daoStatement) {
        this.daoStatement = daoStatement;
    }
}
