package org.nutz.dao.impl;

import javax.sql.DataSource;

import org.nutz.dao.ConnCallback;

/**
 * 这个接口是一个扩展点。通过修改这个接口，你可以为 Dao 的默认实现类 NutDao 彻底定制事务行为
 * <p>
 * 你需要面对的只是
 * <ul>
 * <li>DataSource - 数据源
 * <li>ConnCallback - 数据操作细节
 * </ul>
 * 你的事务行为据此来扩展。 默认的，DefaultDaoRunner 类为你提供了 Nutz 的事务模板解决方案
 * <p>
 * 如果你不喜欢事务模板的方式，你可以实现一个新的 DaoRunner 并通过 NutDao 的 setRunner 方法 设置进来，你的 Dao
 * 的数据执行行为将焕然一新。
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * 
 * @see org.nutz.dao.impl.sql.run.NutDaoRunner
 */
public interface DaoRunner {

    void run(DataSource dataSource, ConnCallback callback);

}
