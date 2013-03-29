package org.nutz.trans;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * 用模板的方式操作事务
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 */
public abstract class Trans {
    
    private static final Log log = Logs.get();

    private static Class<? extends Transaction> implClass;

    static ThreadLocal<Transaction> trans = new ThreadLocal<Transaction>();
    static ThreadLocal<Integer> count = new ThreadLocal<Integer>();
    
    /**
     * 事务debug开关
     */
    public static boolean DEBUG = false;

    /**
     * @return 当前线程的事务，如果没有事务，返回 null
     */
    public static Transaction get() {
        return trans.get();
    }

    /**
     * 这个函数允许你扩展默认的 Nutz 事务实现方式
     * 
     * @param classOfTransaction
     *            你的事务实现
     */
    public static void setup(Class<? extends Transaction> classOfTransaction) {
        implClass = classOfTransaction;
    }

    static void _begain(int level) throws Exception {
        Transaction tn = trans.get();
        if (null == tn) {
            tn = null == implClass ? new NutTransaction() : Mirror.me(implClass).born();
            tn.setLevel(level);
            trans.set(tn);
            count.set(0);
            if (DEBUG)
                log.debugf("Start New Transaction id=%d, level=%d", tn.getId(), level);
        } else {
            if (DEBUG)
                log.debugf("Attach Transaction id=%d, level=%d", tn.getId(), level);
        }
        int tCount = count.get() + 1;
        count.set(tCount);
        //if (DEBUG)
        //    log.debugf("trans_begain: %d", tCount);

    }

    static void _commit() throws Exception {
        count.set(count.get() - 1);
        Transaction tn = trans.get();
        if (count.get() == 0) {
            if (DEBUG)
                log.debug("Transaction Commit id="+tn.getId());
            tn.commit();
        } else {
            if (DEBUG)
                log.debugf("Transaction delay Commit id=%d, count=%d", tn.getId(), count.get());
        }
    }

    static void _depose() {
        if (count.get() == 0)
            try {
                if (DEBUG)
                    log.debugf("Transaction depose id=%d", trans.get().getId());
                trans.get().close();
            }
            catch (Throwable e) {
                throw Lang.wrapThrow(e);
            }
            finally {
                trans.set(null);
            }
    }

    static void _rollback(Integer num) {
        count.set(num);
        if (count.get() == 0) {
            if (DEBUG)
                log.debugf("Transaction rollback id=%s", trans.get().getId());
            trans.get().rollback();
        } else {
            if (DEBUG)
                log.debugf("Transaction delay rollback id=%s", trans.get().getId());
        }
    }

    public static boolean isTransactionNone() {
        Transaction t = trans.get();
        return null == t || t.getLevel() == Connection.TRANSACTION_NONE;
    }

    /**
     * 执行一组原子操作，默认的事务级别为: TRANSACTION_READ_COMMITTED。详细请看 exec(int level,
     * Atom... atoms) 函数的说明
     * 
     * @param atoms
     *            原子操作对象
     */
    public static void exec(Atom... atoms) {
        exec(Connection.TRANSACTION_READ_COMMITTED, atoms);
    }

    /**
     * 执行一组原子操作，并指定事务级别。
     * <p>
     * 这里需要注意的是，Nutz 支持事务模板的无限层级嵌套。 这里，如果每一层嵌套，指定的事务级别有所不同，不同的数据库，可能引发不可预知的错误。
     * <p>
     * 所以，嵌套的事务模板的事务，将以最顶层的事务为级别为标准。就是说，如果最顶层的事务级别为
     * 'TRANSACTION_READ_COMMITTED'，那么下面所包含的所有事务，无论你指定什么样的事务级别，都是
     * 'TRANSACTION_READ_COMMITTED'， 这一点，由抽象类 Transaction 来保证。其 setLevel
     * 当被设置了一个大于 0 的整数以后，将不再 接受任何其他的值。
     * <p>
     * 你可以通过继承 Transaction 来修改这个默认的行为，当然，这个行为修改一般是没有必要的。
     * <p>
     * 另外，你还可能需要知道，通过 Trans.setup 方法，能让整个虚拟机的 Nutz 事务操作都使用你的 Transaction 实现
     * 
     * @param level
     *            事务的级别。
     *            <p>
     *            你可以设置的事务级别是：
     *            <ul>
     *            <li>java.sql.Connection.TRANSACTION_NONE
     *            <li>java.sql.Connection.TRANSACTION_READ_UNCOMMITTED
     *            <li>java.sql.Connection.TRANSACTION_READ_COMMITTED
     *            <li>java.sql.Connection.TRANSACTION_REPEATABLE_READ
     *            <li>java.sql.Connection.TRANSACTION_SERIALIZABLE
     *            </ul>
     *            不同的数据库，对于 JDBC 事务级别的规范，支持的力度不同。请参看相应数据库的文档，已
     *            确定你设置的数据库事务级别是否被支持。
     * @param atoms
     *            原子操作对象
     * @see org.nutz.trans.Transaction
     * @see java.sql.Connection
     */
    public static void exec(int level, Atom... atoms) {
        if (null == atoms)
            return;
        int num = count.get() == null ? 0 : count.get();
        try {
            _begain(level);
            for (Atom atom : atoms)
                atom.run();
            _commit();
        }
        catch (Throwable e) {
            _rollback(num);
            throw Lang.wrapThrow(e);
        }
        finally {
            _depose();
        }
    }

    /**
     * 执行一个分子，并给出返回值
     * 
     * @param <T>
     * @param molecule
     *            分子
     * @return 分子返回值
     */
    public static <T> T exec(Molecule<T> molecule) {
        Trans.exec((Atom) molecule);
        return molecule.getObj();
    }

    /* ===========================下面暴露几个方法给喜欢 try...catch...finally 的人 ===== */

    /**
     * 开始一个事务，级别为 TRANSACTION_READ_COMMITTED
     * <p>
     * 你需要手工用 try...catch...finally 来保证你提交和关闭这个事务
     * 
     * @throws Exception
     */
    public static void begin() throws Exception {
        Trans._begain(Connection.TRANSACTION_READ_COMMITTED);
    }

    /**
     * 开始一个指定事务
     * <p>
     * 你需要手工用 try...catch...finally 来保证你提交和关闭这个事务
     * 
     * @param level
     *            指定级别
     * 
     * @throws Exception
     */
    public static void begin(int level) throws Exception {
        Trans._begain(level);
    }

    /**
     * 提交事务，执行它前，你必需保证你已经手工开始了一个事务
     * 
     * @throws Exception
     */
    public static void commit() throws Exception {
        Trans._commit();
    }

    /**
     * 回滚事务，执行它前，你必需保证你已经手工开始了一个事务
     * 
     * @throws Exception
     */
    public static void rollback() throws Exception {
        Integer c = Trans.count.get();
        if (c == null)
            c = Integer.valueOf(0);
        Trans._rollback(c);
    }

    /**
     * 关闭事务，执行它前，你必需保证你已经手工开始了一个事务
     * 
     * @throws Exception
     */
    public static void close() throws Exception {
        Trans._depose();
    }

    /**
     * 如果在事务中,则返回事务的连接,否则直接从数据源取一个新的连接
     */
    public static Connection getConnectionAuto(DataSource ds) throws SQLException {
        if (get() == null)
            return ds.getConnection();
        else
            return get().getConnection(ds);
    }

    public static void closeConnectionAuto(Connection conn) {
        if (get() == null && null != conn) {
            try {
                conn.close();
            }
            catch (SQLException e) {
                throw Lang.wrapThrow(e);
            }
        }
    }
}
