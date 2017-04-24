package org.nutz.dao.test;

import java.util.List;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.nutz.dao.Cnd;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.impl.SimpleDataSource;

public class DaoTest {

	private DataSource dataSource;
	private NutDao dao;

	@Before
	public void initDataSource() {
		SimpleDataSource dataSource = new SimpleDataSource();
		dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/test");
		dataSource.setUsername("root");
		dataSource.setPassword("rootroot");
		this.dataSource = dataSource;
		this.dao = new NutDao(dataSource);
	}

	@After
	public void destoryDataSource() {
		((SimpleDataSource) this.dataSource).close();
	}

	@Test
	public void simpleTest(){
		List<A> list = dao.query(A.class, Cnd.where("aid","=",Cnd.nst(dao).select("aaa", A.class, Cnd.where("aaa", "=", "a1"))));
		System.out.println(list);
	}
	
	public void test() {
		List<A> list = dao.query(A.class, Cnd.where("aid", "in", Cnd.nst(dao).select("aid", A.class,
				Cnd.where("aaa", "in", Cnd.nst(dao).select("distinct aaa", A.class, Cnd.where("aaa", "=", "a1"))))));
		System.out.println(list);
	}

}
