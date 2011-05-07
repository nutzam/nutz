package org.nutz.dao;

import java.sql.Connection;

import org.apache.commons.dbcp.BasicDataSource;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.test.meta.Country;
import org.nutz.trans.Atom;
import org.nutz.trans.Trans;

/**
 *
 * @author wendal
 */
public class PsTest {

	/**
	 * 测试新的Dao实现在postgresql下的表现
	 */
	public static void main(String[] args) throws Throwable {
		BasicDataSource ds = new BasicDataSource();
		ds.setDriverClassName("org.postgresql.Driver");
		ds.setUsername("postgres");
		ds.setPassword("123456");
		ds.setUrl("jdbc:postgresql:test");
		ds.setDefaultAutoCommit(false);
		
		final NutDao dao = new NutDao(ds);
		
		TableName.run(1, new Atom() {
			public void run() {
				Trans.exec(new Atom() {
					public void run() {
						dao.create(Country.class, true);
						System.out.println("------------???");
						dao.insert(Country.make("A"));
						try {
							System.out.println("---------------------");
							dao.insert(Country.make("A"));
							System.out.println("+++++++++++++++++++++++++++++++++++");
						}
						catch (DaoException e) {
						}
						try {
							dao.insert(Country.make("C"));
						} catch (Throwable e) {
							System.out.println("天啊,还是报错!!");
						}
					}
				});
			}
		});
		Connection conn = ds.getConnection();
		conn.prepareStatement("insert into dao_country(name,detail) values('ABC','CC')").execute();
		try {
			conn.prepareStatement("insert into dao_country(name,detail) values('ABC','CC')").execute();
		} catch (Exception e) {}
		conn.prepareStatement("insert into dao_country(name,detail) values('CC','CC')").execute();
		System.out.println("竟然能通过??!!");
		conn.commit();
	}

}
