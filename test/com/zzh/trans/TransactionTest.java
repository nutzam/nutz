package com.zzh.trans;

import org.apache.commons.dbcp.BasicDataSource;

import com.zzh.dao.impl.FileSQLManager;
import com.zzh.dao.impl.NutDao;
import com.zzh.service.IdEntityService;
import com.zzh.trans.NutTransaction;
import com.zzh.trans.Atom;
import com.zzh.trans.Trans;

import junit.framework.TestCase;

public class TransactionTest extends TestCase {

	private static IdEntityService<Cat> catService = new IdEntityService<Cat>(){};
	private static IdEntityService<Company> comService = new IdEntityService<Company>(){};
	private static IdEntityService<Master> masterService = new IdEntityService<Master>(){};
	private static NutDao dao;;

	static {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource
				.setUrl("jdbc:mysql://localhost:3306/nutagitest?useUnicode=true&characterEncoding=UTF-8");
		dataSource.setUsername("root");
		dataSource.setPassword("123456");
		dao = new NutDao();
		dao.setDataSource(dataSource);
		dao.setSqlManager(new FileSQLManager("com/zzh/trans/TransTest.sqls"));
		catService.setDao(dao);
		comService.setDao(dao);
		masterService.setDao(dao);
		Trans.setup(NutTransaction.class);
	}

	public static void reset() {
		String[] keys = { ".company.drop", ".company.create", ".cat.drop", ".cat.create",
				".master.drop", ".master.create" };
		dao.executeBySqlKey(keys);
	}

	public static Cat createCat(String name, Master m) {
		Cat c = new Cat();
		c.setName(name);
		c.setMaster(m);
		return c;
	}

	public static Master createMaster(String name, Company com) {
		Master c = new Master();
		c.setName(name);
		c.setCom(com);
		return c;
	}

	public static Company createCompany(String name) {
		Company c = new Company();
		c.setName(name);
		return c;
	}

	class Thread2 extends Thread {

		private boolean done = false;

		public boolean isDone() {
			return done;
		}

		@Override
		public void run() {
			TestCase.assertEquals(0, dao.count(Company.class, null));
			TestCase.assertEquals(0, dao.count(Master.class, null));
			TestCase.assertEquals(0, dao.count(Cat.class, null));
			done = true;
		}

	}

	public void testCommit() {
		reset();
		// In transaction
		Trans.exec(new Atom() {
			@Override
			public void run() throws Exception {
				Company com = createCompany("dtri");
				Master m = createMaster("zzh", com);
				Cat c1 = createCat("XiaoBai", m);
				Cat c2 = createCat("Tony", m);

				comService.insert(com);
				assertEquals(1, dao.count(Company.class, null));
				m.setComId(com.getId());
				masterService.insert(m);
				assertEquals(1, dao.count(Master.class, null));
				c1.setMasterId(m.getId());
				c2.setMasterId(m.getId());
				catService.insert(c1);
				catService.insert(c2);
				assertEquals(2, dao.count(Cat.class, null));
				// In another thread, can not see the result
				Thread2 checker = new Thread2();
				checker.start();
				while (!checker.isDone()) {
					Thread.sleep(10);
				}
			}
		});
		// Out of transaction
		assertEquals(1, dao.count(Company.class, null));
		assertEquals(1, dao.count(Master.class, null));
		assertEquals(2, dao.count(Cat.class, null));
	}

	public void testRollback() {
		reset();
		// In transaction
		try {
			Trans.exec(new Atom() {
				@Override
				public void run() throws Exception {
					Company com = createCompany("dtri");
					Master m = createMaster("zzh", com);
					Cat c1 = createCat("XiaoBai", m);
					Cat c2 = createCat("Tony", m);

					comService.insert(com);
					assertEquals(1, dao.count(Company.class, null));
					m.setComId(com.getId());
					masterService.insert(m);
					assertEquals(1, dao.count(Master.class, null));
					c1.setMasterId(m.getId());
					c2.setMasterId(m.getId());
					catService.insert(c1);
					catService.insert(c2);
					assertEquals(2, dao.count(Cat.class, null));
					// In another thread, can not see the result
					Thread2 checker = new Thread2();
					checker.start();
					while (!checker.isDone()) {
						Thread.sleep(10);
					}
					throw new RuntimeException("Stop!!!");
				}
			});
			fail();
		} catch (Exception e) {
		}
		// Out of transaction
		assertEquals(0, dao.count(Company.class, null));
		assertEquals(0, dao.count(Master.class, null));
		assertEquals(0, dao.count(Cat.class, null));
	}
}
