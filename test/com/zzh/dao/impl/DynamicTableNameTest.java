package com.zzh.dao.impl;

import com.zzh.Main;
import com.zzh.dao.ComboSql;
import com.zzh.dao.Dao;
import com.zzh.dao.ExecutableSql;
import com.zzh.dao.TableName;
import com.zzh.dao.entity.annotation.*;
import com.zzh.lang.Lang;
import com.zzh.lang.random.StringGenerator;
import com.zzh.trans.Atom;

import junit.framework.TestCase;

public class DynamicTableNameTest extends TestCase {

	private Dao dao;

	@Override
	protected void setUp() throws Exception {
		ExecutableSql drop = new ExecutableSql("DROP TABLE IF EXISTS t_a;");
		ExecutableSql create = new ExecutableSql(
				"CREATE TABLE t_a(id SERIAL PRIMARY KEY, txt VARCHAR(20));");
		ComboSql sql = new ComboSql();
		sql.addSQL(drop).addSQL(create);
		dao = Main.getDao(null);
		dao.execute(sql);
	}

	@Override
	protected void tearDown() throws Exception {
		Main.closeDataSource();
	}

	/*--------------------------------------------------------------------------*/
	@Table("t_a")
	public static class A {
		@Column
		@Id
		private int id;
		@Column
		protected String txt;

		@Many(target = B.class, field = "id")
		private B[] bs;
	}

	@Table("t_b_${id}")
	public static class B {
		static B make(String name, String value) {
			B b = new B();
			b.name = name;
			b.value = value;
			return b;
		}

		@Column
		@Name
		private String name;
		@Column
		private String value;
		@ManyMany(target = C.class, relation = "m_b_c_${id}", from = "name", to = "cid")
		private C[] cs;
	}

	@Table("t_c_${id}")
	public static class C {
		static C make(String txt) {
			C c = new C();
			c.txt = txt;
			return c;
		}

		@Column
		@Id
		int id;
		@Column
		String txt;
	}

	/*--------------------------------------------------------------------------*/
	public void testDynamicManyManyRelation() {
		int id = 4;
		prepareTableForB(id);
		TableName.run(id, new Atom() {
			public void run() {
				B b = dao.insert(B.make("abc", "xyz"));
				b.cs = new C[2];
				b.cs[0] = C.make("C1");
				b.cs[1] = C.make("C2");
				dao.insertManyMany(b, "cs");

				b = dao.fetch(B.class, "abc");
				dao.fetchManyMany(b, "cs");
				assertEquals(2, b.cs.length);
			}
		});
	}

	public void testSimple() {
		int id = 3;
		prepareTableForB(id);
		B b = B.make("b1", "v1");
		TableName.set(id);
		dao.insert(b);
		b = dao.fetch(B.class, "b1");
		TableName.clear();
		assertEquals("v1", b.value);
	}

	private void prepareTableForB(int id) {
		ExecutableSql dropB = new ExecutableSql("DROP TABLE IF EXISTS t_b_${.id};");
		ExecutableSql createB = new ExecutableSql(
				"CREATE TABLE t_b_${.id}(name VARCHAR(255) PRIMARY KEY, value VARCHAR(20));");
		ExecutableSql dropC = new ExecutableSql("DROP TABLE IF EXISTS t_c_${.id};");
		ExecutableSql createC = new ExecutableSql(
				"CREATE TABLE t_c_${.id}(id SERIAL PRIMARY KEY, txt VARCHAR(20));");
		ExecutableSql dropBC = new ExecutableSql("DROP TABLE IF EXISTS m_b_c_${.id};");
		ExecutableSql createBC = new ExecutableSql(
				"CREATE TABLE m_b_c_${.id}(cid INT, name VARCHAR(20));");
		ComboSql sql = new ComboSql();
		sql.addSQL(dropB).addSQL(createB).addSQL(dropC).addSQL(createC).addSQL(dropBC).addSQL(
				createBC);
		sql.set(".id", id);
		dao.execute(sql);
	}

	public void testSimpleInsertMany() {
		A a = new A();
		a.txt = "AAA";
		a.bs = Lang.array(B.make("b1", "v1"), B.make("b2", "v2"));
		dao.insert(a);
		prepareTableForB(a.id);
		dao.insertMany(a, "bs");
		try {
			dao.count(B.class);
			fail();
		} catch (Exception e) {
			TableName.set(a.id);
			assertEquals(2, dao.count(B.class));
			a.bs = null;
			dao.fetchMany(a, "bs");
			assertEquals("b1", a.bs[0].name);
			assertEquals("b2", a.bs[1].name);
		}
	}

	/*--------------------------------------------------------------------------*/
	public static class Flag {
		private int refer;
		private int ready;

		public boolean isReady() {
			return ready >= 3;
		}

		public Flag() {
			this.refer = 3;
			this.ready = 0;
		}

		public int getRefer() {
			return refer;
		}

		public void minusOne() {
			refer--;
		}

	}

	public abstract static class R implements Runnable {

		protected Dao dao;
		protected Object lock;
		protected Object pair;
		protected Flag flag;

		public Object getLock() {
			return lock;
		}

		public void setPair(Object pair) {
			this.pair = pair;
		}

		public R(Flag flag, Dao dao) {
			this.dao = dao;
			this.lock = new Object();
			this.flag = flag;

		}

	}

	public static class RRed extends R {

		public RRed(Flag flag, Dao dao) {
			super(flag, dao);
		}

		@Override
		public void run() {
			// System.out.printf("T[%s] start!\n",
			// this.getClass().getSimpleName());
			flag.ready++;
			synchronized (lock) {
				try {
					lock.wait(5000);
				} catch (InterruptedException e) {
					throw Lang.wrapThrow(e);
				}
			}
			// insert t_b_1
			TableName.set(1);
			dao.insert(B.make("red1", "v1"));
			// notify black
			synchronized (pair) {
				pair.notify();
			}
			// wait(5000)
			synchronized (lock) {
				try {
					lock.wait(5000);
				} catch (InterruptedException e) {
					throw Lang.wrapThrow(e);
				}
			}
			// insert t_b_2
			TableName.set(2);
			dao.insert(B.make("red2", "v2"));
			// notify black
			synchronized (pair) {
				pair.notify();
			}
			synchronized (flag) {
				flag.minusOne();
				flag.notify();
			}
			// System.out.printf("T[%s] end!\n",
			// this.getClass().getSimpleName());
		}

	}

	public static class RBlack extends R {

		public RBlack(Flag flag, Dao dao) {
			super(flag, dao);
		}

		@Override
		public void run() {
			// System.out.printf("T[%s] start!\n",
			// this.getClass().getSimpleName());
			flag.ready++;
			// wait(5000)
			synchronized (lock) {
				try {
					lock.wait(5000);
				} catch (InterruptedException e) {
					throw Lang.wrapThrow(e);
				}
			}
			// insert t_b_2
			TableName.set(2);
			dao.insert(B.make("black1", "v1"));
			// notify red
			synchronized (pair) {
				pair.notify();
			}
			// wait(5000)
			synchronized (lock) {
				try {
					lock.wait(5000);
				} catch (InterruptedException e) {
					throw Lang.wrapThrow(e);
				}
			}
			// insert t_b_1
			TableName.set(1);
			dao.insert(B.make("black2", "v2"));
			synchronized (flag) {
				flag.minusOne();
				flag.notify();
			}
			// System.out.printf("T[%s] end!\n",
			// this.getClass().getSimpleName());
		}

	}

	public static class RWhite extends R {

		public static final StringGenerator sg = new StringGenerator(200);

		public RWhite(Flag flag, Dao dao) {
			super(flag, dao);
		}

		@Override
		public void run() {
			// System.out.printf("T[%s] start!\n",
			// this.getClass().getSimpleName());
			flag.ready++;
			synchronized (lock) {
				try {
					lock.wait(5000);
				} catch (InterruptedException e) {
					throw Lang.wrapThrow(e);
				}
			}
			for (int i = 0; i < 100; i++) {
				TableName.set(1);
				dao.insert(B.make(sg.next(), "V"));
				TableName.set(2);
				dao.insert(B.make(sg.next(), "V"));
			}
			synchronized (flag) {
				flag.minusOne();
				flag.notify();
			}
			// System.out.printf("T[%s] end!\n",
			// this.getClass().getSimpleName());
		}
	}

	/*--------------------------------------------------------------------------*/
	public void testInMultiThread() throws InterruptedException {
		this.prepareTableForB(1);
		this.prepareTableForB(2);

		Flag f = new Flag();
		RRed red = new RRed(f, dao);
		RBlack black = new RBlack(f, dao);
		RWhite white = new RWhite(f, dao);
		red.setPair(black.getLock());
		black.setPair(red.getLock());

		new Thread(red).start();
		new Thread(black).start();
		new Thread(white).start();

		while (!f.isReady()) {}
		synchronized (white.getLock()) {
			// System.out.println("=> white!");
			white.getLock().notifyAll();
		}

		synchronized (red.getLock()) {
			// System.out.println("=> red!");
			red.getLock().notify();
		}

		while (f.getRefer() > 0) {
			synchronized (f) {
				f.wait();
			}
		}
		TableName.set(1);
		assertEquals(102, dao.count(B.class));
		TableName.set(2);
		assertEquals(102, dao.count(B.class));
	}

}
