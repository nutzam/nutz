package org.nutz.dao.impl;

import java.io.Closeable;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.nutz.lang.Lang;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * 这是一个神奇的DataSource!!你甚至不需要设置driverClassName!!
 * <p>把用户名,密码,jdbcURL设置一下,这个类就能用了!!
 * <p>当然，你在你的 CLASSPATH 下要放置相应的数据库驱动 jar 包
 * 
 * @author wendal(wendal1985@gmail.com)
 */
public class SimpleDataSource implements DataSource, Closeable {

	private static final Log log = Logs.get();
	
	protected String username;
    protected String password;
    protected String driverClassName;
    protected String jdbcUrl;
    
    public SimpleDataSource() {
		log.warn("SimpleDataSource is NOT a Connection Pool, So it is slow but safe for debug/study");
	}
    
    /**
     * 这是唯一会被NutDao调用的方法
     */
    public Connection getConnection() throws SQLException {
        Connection conn;
        if (username != null)
            conn = DriverManager.getConnection(jdbcUrl, username, password);
        else
            conn = DriverManager.getConnection(jdbcUrl);
        return conn;
    }
    
    public void close() {}
    
    public void setDriverClassName(String driverClassName) throws ClassNotFoundException {
        Lang.loadClass(driverClassName);
    }
    
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }
    
    public void setUrl(String url) {
        this.jdbcUrl = url;
    }

    //加载Nutz所支持的数据库的驱动!!
    static {
        String[] drivers = {"org.h2.Driver",
                            "com.ibm.db2.jcc.DB2Driver",
                            "org.hsqldb.jdbcDriver",
                            "oracle.jdbc.OracleDriver",
                            "org.postgresql.Driver",
                            "net.sourceforge.jtds.jdbc.Driver",
                            "com.microsoft.sqlserver.jdbc.SQLServerDriver",
                            "org.sqlite.JDBC",
                            "com.mysql.jdbc.Driver",
                            "com.beyondb.jdbc.BeyondbDriver"};
        for (String driverClassName : drivers) {
            try {
                Class.forName(driverClassName);
            } catch (Throwable e) {}
        }
    }
    
//---------------------------------------------------------------

    public PrintWriter getLogWriter() throws SQLException {
        throw Lang.noImplement();
    }

    public void setLogWriter(PrintWriter out) throws SQLException {
        throw Lang.noImplement();
    }

    public void setLoginTimeout(int seconds) throws SQLException {throw Lang.noImplement();}

    public int getLoginTimeout() throws SQLException {
        throw Lang.noImplement();
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw Lang.noImplement();
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw Lang.noImplement();
    }

    public Connection getConnection(String username, String password)
            throws SQLException {
        throw Lang.noImplement();
    }

    public Logger getParentLogger()  {
        throw Lang.noImplement();
    }

    public static DataSource createDataSource(Properties props) {
        SimpleDataSource sds = new SimpleDataSource();
        sds.setJdbcUrl(props.getProperty("url", props.getProperty("jdbcUrl")));
        sds.setPassword(props.getProperty("password"));
        sds.setUsername(props.getProperty("username"));
        return sds;
    }
}
