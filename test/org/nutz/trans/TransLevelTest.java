package org.nutz.trans;

import java.sql.Connection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import junit.framework.Assert;

import org.junit.Test;
import org.nutz.dao.test.DaoCase;
import org.nutz.service.IdEntityService;

public class TransLevelTest extends DaoCase {

	private static IdEntityService<Company> comService;

	@Override
	protected void before() {
		pojos.execFile("org/nutz/trans/trans.sqls");
		comService = new IdEntityService<Company>(dao) {};
		Company c = Company.create("com1");
		comService.dao().insert(c);
		c = Company.create("com2");
		comService.dao().insert(c);
		c = Company.create("com3");
		comService.dao().insert(c);
	}

	@Override
	protected void after() {
		super.after();
	}

	@Test
	public void testReadCommitted() {
		final ExecutorService es = Executors.newCachedThreadPool();
		Trans.exec(Connection.TRANSACTION_READ_COMMITTED, new Atom() {
			@Override
			public void run() {
				Company c = new Company();
				c.setId(1);
				c.setName("update");
				comService.dao().update(c);
				try {
					Assert.assertEquals("com1", es.submit(
							new QueryCompany_ReadCommitted()).get());
				} catch (Exception e) {
					Assert.assertTrue(false);
				}
				c.setName("com1");
				comService.dao().update(c);
			}
		});
		es.shutdown();
		Assert.assertEquals("com1", comService.fetch(1).getName());
	}

	static class QueryCompany_ReadCommitted extends DaoCase implements
			Callable<String> {
		@Override
		public String call() throws Exception {
			ResultAtom<String> ra = null;
			Trans.exec(Connection.TRANSACTION_READ_COMMITTED,
					ra = new ResultAtom<String>() {
						@Override
						public void run() {
							setResult(comService.dao().fetch(Company.class, 1)
									.getName());
						}
					});
			String i = ra.getResult();
			return i;
		}
	}

	@Test
	public void testRepeatableRead() {
		final ExecutorService es = Executors.newCachedThreadPool();
		Trans.exec(Connection.TRANSACTION_READ_COMMITTED, new Atom() {
			@Override
			public void run() {
				Assert.assertEquals("com1", comService.fetch(1).getName());
				es.submit(new RepeatableRead());
				// wait the thread to finish the update
				synchronized (Thread.currentThread()) {
					try {
						Thread.currentThread().wait(3 * 1000);
					} catch (InterruptedException e) {}
				}
				Assert.assertEquals("update", comService.fetch(1).getName());
			}
		});
		es.shutdown();
		// Company c = new Company();
		// c.setId(1);
		// c.setName("com1");
		// comService.dao().update(c);
	}

	static class RepeatableRead extends DaoCase implements Runnable {
		@Override
		public void run() {
			Trans.exec(Connection.TRANSACTION_READ_COMMITTED, new Atom() {
				@Override
				public void run() {
					Company c = new Company();
					c.setId(1);
					c.setName("update");
					comService.dao().update(c);
				}
			});
		}
	}

	@Test
	public void testSerializable() {
		final ExecutorService es = Executors.newCachedThreadPool();
		Trans.exec(Connection.TRANSACTION_SERIALIZABLE, new Atom() {
			@Override
			public void run() {
				Assert.assertEquals("com1", comService.fetch(1).getName());
				es.submit(new RepeatableRead());
				// wait the thread to finish the update
				synchronized (Thread.currentThread()) {
					try {
						Thread.currentThread().wait(3 * 1000);
					} catch (InterruptedException e) {}
				}
				Assert.assertEquals("com1", comService.fetch(1).getName());
			}
		});
	}

	static abstract class ResultAtom<T> implements Atom {

		private T result;

		public T getResult() {
			return result;
		}

		public void setResult(T result) {
			this.result = result;
		}
	}

}
