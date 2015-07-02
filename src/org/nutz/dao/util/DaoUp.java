package org.nutz.dao.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.sql.DataSource;

import org.nutz.dao.Dao;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.impl.SimpleDataSource;
import org.nutz.lang.Files;
import org.nutz.lang.Mirror;
import org.nutz.lang.Streams;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * 为非Mvc,Ioc环境下的程序提供辅助支持.<p/>
 * <b>请注意使用场景!!! 在Mvc下有IocBy的情况下,不需要也不应该使用本类!!</b><p/>
 * <b>Mvc下可以通过 Mvcs.getIoc()或Mvcs.ctx().getDefaultIoc()获取Ioc容器,从而获取其中的Dao实例!!</b><p/>
 * <b>Mvc应尽量使用注入,而非主动取Dao实例,更不应该主动new NutDao!!!</b>
 * <p/> 最基本的用法<p/>
<code>
    DaoUp.me().init(new File("db.properties"));
    Dao dao = DaoUp.me().dao();
    
    dao.insert(.......);
    
    // 注意,不是每次用完Dao就关,是整个程序关闭的时候才关!!
    // 程序结束前关闭相关资源.
    DaoUp.me().close();
</code>
<p/><p/>
请参阅test源码中的DaoUpTest获取Dao的入门技巧.
 * 
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class DaoUp {
    
    /**
     * 日志对象
     */
    private static final Log log = Logs.get();

    /**
     * 内置单例
     */
    protected static DaoUp me = new DaoUp();
    
    /**
     * Druid数据源的工厂方法类
     */
    protected static Class<?> druidFactoryClass;
    
    /**
     * 如果本对象被GC,是否触发自动关闭
     */
    protected boolean autoCloseWhenFinalize = true;
    
    static {
        try {
            /**
             * 加载DruidDataSourceFactory, 即Druid连接池的工厂类
             */
            druidFactoryClass = Class.forName("com.alibaba.druid.pool.DruidDataSourceFactory");
        }
        catch (ClassNotFoundException e) {
            // 找不到就用内置的SimpleDataSource好了.
            // TODO 支持其他类型的数据源, 低优先级
        }
    }
    
    /**
     * 获取内置的DaoHelper单例
     * @return DaoHelper实例
     */
    public static DaoUp me() {
        return me;
    }

    /**
     * 注意构造方法时protected的,如果需要新建多个DaoHelper,请继承DaoHelper,从而暴露构造方法或使用工厂方法!!
     */
    protected DaoUp(String name) {
        this.name = name;
    }
    
    /**
     * 注意构造方法时protected的,如果需要新建多个DaoHelper,请继承DaoHelper,从而暴露构造方法或使用工厂方法!!
     */
    protected DaoUp() {
        this("t"+Thread.currentThread().getId() + "_" + System.currentTimeMillis());
    }
    
    /**
     * Dao对象
     */
    protected Dao dao;
    
    /**
     * 连接池
     */
    protected DataSource dataSource;
    
    /**
     * 当前DaoHelper的名词
     */
    protected String name;
    
    /**
     * 返回所持有的Dao实例,如果DaoHelper还没初始化或已经关闭,这里会返回null
     * @return Dao实例
     */
    public Dao dao() {
        return dao;
    }
    
    /**
     * 获取数据源, 如果DaoHelper还没初始化或已经关闭,这里会返回null
     * @return 数据源(连接池)
     */
    public DataSource getDataSource() {
        return dataSource;
    }
    
    /**
     * 主动设置数据源(连接池)
     * @param dataSource 数据源(连接池)
     */
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        setDao(new NutDao(dataSource));
    }
    
    /**
     * 主动设置Dao实例
     * @param dao Dao实例
     */
    public void setDao(Dao dao) {
        if (this.dao != null)
            log.infof("override old Dao=%s by new Dao=%s", this.dao, dao);
        this.dao = dao;
    }
    
    /**
     * 从classpath或当前目录下查找配置文件来进行初始化
     * @param name
     */
    public void init(String name) throws IOException {
        init(new FileInputStream(Files.findFile(name)));
    }
    
    /**
     * 从一个文件读取数据库配置
     * @param f 配置文件
     * @throws IOException 文件不可读取时抛出异常
     */
    public void init(File f) throws IOException {
        init(new FileInputStream(f));
    }
    
    /**
     * 从一个流读取数据库配置
     * @param in 输入流,包含配置信息
     * @throws IOException 读取失败是抛出异常
     */
    public void init(InputStream in) throws IOException {
        Properties props = new Properties();
        try {
            props.load(in);
            init(props);
        }
        finally {
            Streams.safeClose(in);
        }
    }
    
    /**
     * 给定一个Properties配置,不能为null!!!! 最起码要包含一个叫url的参数!!!
     * @param props 配置信息
     */
    public void init(Properties props) {
        if (dao != null) {
            throw new IllegalArgumentException("DaoUp is inited!!");
        }
        if (props.size() == 0) {
            throw new IllegalArgumentException("DaoUp props size=0!!!");
        }
        DataSource ds = buildDataSource(props);
        setDataSource(ds);
    }
    
    /**
     * 构建DataSource,子类可覆盖. 如果存在Druid,则使用之,否则使用内置的SimpleDataSource
     * @param props 配置信息
     * @return 目标DataSource
     */
    protected DataSource buildDataSource(Properties props) {
        if (druidFactoryClass != null) {
            log.debug("build DruidDataSource by props");
            Mirror<?> mirror = Mirror.me(druidFactoryClass);
            return (DataSource) mirror.invoke(null, "createDataSource", props);
        }
        log.debug("build SimpleteDataSource by props");
        return SimpleDataSource.createDataSource(props);
    }
    
    /**
     * 关闭本DaoHelper,将关闭DataSource并将dao和dataSource置为null!!!<p/>
     * <b>只能在程序关闭时调用,严禁在每次Dao操作后调用!!</b>
     */
    public synchronized void close() {
        if (dao == null)
            return;
        log.infof("shutdown DaoUp(name=%s)", name);
        try {
            Mirror.me(dataSource).invoke(dataSource, "close");
        }
        catch (Throwable e) {
        }
        this.dataSource = null;
        this.dao = null;
    }
    
    /**
     * 设置是否在本对象被GC时自动关闭相关资源.<p/>
     * <b>若要设置为false, 请慎重考虑,因为绝大部分情况下设置为true并不能解决您当前遇到的问题!!</b><p/>
     * DaoHelper类不是设计为即用即抛的!!!而是设计为单例模式的!!!!!!!<p/>
     * <b>如果是遇到DataSource is closed之类的异常, 在考虑使用本配置前请先检讨代码!!!</b><p/>
     * @param autoCloseWhenFinalize 是否自动关闭资源
     */
    public void setAutoCloseWhenFinalize(boolean autoCloseWhenFinalize) {
        this.autoCloseWhenFinalize = autoCloseWhenFinalize;
        if (!autoCloseWhenFinalize) {
            log.warnf("DaoUp[%s] autoCloseWhenFinalize is disabled. You had been WARN!!", name);
        }
    }
    
    /**
     * 如果被GC,主动触发关闭,除非autoCloseWhenFinalize为false
     */
    protected void finalize() throws Throwable {
        if (autoCloseWhenFinalize)
            close();
        super.finalize();
    }
    
    // TODO 完成一个repl
//    public static void main(String[] args) {
//        
//    }
}
