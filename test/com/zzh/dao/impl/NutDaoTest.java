package com.zzh.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import com.zzh.Main;
import com.zzh.lang.random.GM;
import com.zzh.lang.random.StringGenerator;
import com.zzh.castor.Castors;
import com.zzh.dao.Condition;
import com.zzh.dao.Dao;
import com.zzh.dao.ExecutableSql;
import com.zzh.dao.FetchSql;
import com.zzh.dao.QuerySql;
import com.zzh.dao.Sql;
import com.zzh.dao.callback.QueryCallback;
import com.zzh.dao.entity.Entity;
import com.zzh.dao.entity.annotation.*;
import com.zzh.lang.meta.Email;
import com.zzh.lang.meta.Pager;

import junit.framework.TestCase;

public class NutDaoTest extends TestCase {

	@Table("t_abc")
	public class Abc {

		@Column
		@Id
		public int id;

		@Column
		@Name
		public String name;

		public Abc() {
		}

		public Abc(ResultSet rs) throws SQLException {
			id = rs.getInt("id");
			name = rs.getString("name");
		}

	}

	private Dao dao;

	@Override
	protected void setUp() throws Exception {
		this.dao = Main.getDao("com/zzh/dao/impl/test.sqls");
	}

	@Override
	protected void tearDown() throws Exception {
		Main.closeDataSource();
	}

	public void testGetSQL() {
		assertTrue(dao.sqls().createSql(".abc.create") instanceof ExecutableSql);
	}

	public void testExecuteBatchSQLs() {
		try {
			Sql<?> sqlDrop = dao.sqls().createSql(".abc.drop");
			Sql<?> sqlCreate = dao.sqls().createSql(".abc.create");
			dao.execute(sqlDrop, sqlCreate);
		} catch (Exception e) {
			fail();
		}
	}

	public void testExecuteInsertSQL() {
		try {
			Sql<?> sql = dao.sqls().createSql(".abc.insert");
			dao.execute(sql);
		} catch (Exception e) {
			fail();
		}
	}

	public void testFetchByField() {
		Abc abc = dao.fetch(Abc.class, 1);
		assertEquals("ZZH", abc.name);
	}

	public void testExecuteUpdateSQL() {
		try {
			Sql<?> sql = dao.sqls().createSql(".abc.update");
			sql.set("name", "zozoh");
			sql.set("id", 1);
			dao.execute(sql);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	public class FetchAbc extends FetchSql<Abc> {

		public FetchAbc(Castors castors) {
			super(castors);
		}
	}

	public void testExecuteFetchSQL() {
		try {
			FetchAbc sql = dao.sqls().createSql(FetchAbc.class, "fetch.abc");
			sql.set("id", 1);
			sql.setCallback(new QueryCallback<Abc>() {
				@Override
				public Abc invoke(ResultSet rs) throws SQLException {
					return new Abc(rs);
				}
			});
			dao.execute(sql);
			Abc abc = (Abc) sql.getResult();
			assertEquals(1, abc.id);
			assertEquals("zozoh", abc.name);
		} catch (Exception e) {
			fail();
		}
	}

	@SuppressWarnings("unchecked")
	public void testExecuteQuerySQL() {
		try {
			QuerySql sql = (QuerySql) dao.sqls().createSql("query.abc");
			sql.setCallback(new QueryCallback() {
				@Override
				public Object invoke(ResultSet rs) throws SQLException {
					return new Abc(rs);
				}
			});
			dao.execute(sql);
			List<Abc> list = (List<Abc>) sql.getResult();
			assertEquals(3, list.size());
			assertEquals("zozoh", list.get(0).name);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	public static Student setupStudent(Student stu, String name) {
		stu.setId(-1);
		stu.setAge(GM.gRandom(10, 45));
		stu.setAboutMe(new StringGenerator(6, 10).next());
		stu.setBirthday(Calendar.getInstance());
		stu.setEmail(new Email(new StringGenerator(3, 5).next(), "gmail.com"));
		stu.setName(name);
		return stu;
	}

	public void testInsert() {
		dao.executeBySqlKey(".student.drop", ".student.create");
		Student stu = setupStudent(new Student(), "zzh");
		stu = dao.insert(stu);
		assertEquals(1, stu.getId());
		assertEquals(1, dao.count(Student.class, null));
	}

	public void testFetch() {
		Student stu = dao.fetch(Student.class, 1);
		Student stu2 = dao.fetch(Student.class, "zzh");
		assertTrue(stu.equals(stu2));
	}

	public void testUpdate() {
		Student stu = dao.fetch(Student.class, 1);
		String txt = "I am great!!!";
		stu.setAboutMe(txt);
		dao.update(stu);
		Student stu2 = dao.fetch(Student.class, "zzh");
		assertTrue(stu.equals(stu2));
	}

	public void testUpdate2Default() {
		Student stu = dao.fetch(Student.class, 1);
		stu.setAboutMe(null);
		dao.update(stu);
		Student stu2 = dao.fetch(Student.class, 1);
		assertEquals("I am zzh", stu2.getAboutMe());
	}

	public void testUpdateNull() {
		Student stu = dao.fetch(Student.class, 1);
		stu.setEmail(null);
		dao.update(stu);
		Student stu2 = dao.fetch(Student.class, 1);
		assertNull(stu2.getEmail());
	}

	public void testUpdateIgnoreNull() {
		Student stu = dao.fetch(Student.class, 1);
		Email email = stu.getEmail();
		stu.setEmail(null);
		dao.update(stu, true);
		Student stu2 = dao.fetch(Student.class, 1);
		assertEquals(email, stu2.getEmail());
	}

	public void testUpdateIgnoredField() {
		Student stu = dao.fetch(Student.class, 1);
		Email email = stu.getEmail();
		stu.setEmail(new Email("bbb@bbb.com"));
		stu.setAboutMe("haha");
		dao.update(stu, "[email]", null);
		Student stu2 = dao.fetch(Student.class, 1);
		assertEquals("haha", stu2.getAboutMe());
		assertEquals(email, stu2.getEmail());
	}

	public void testUpdateActivedField() {
		Student stu = dao.fetch(Student.class, 1);
		Email email = stu.getEmail();
		stu.setEmail(new Email("bbb@bbb.com"));
		stu.setAboutMe("xyz");
		dao.update(stu, null, "[aboutMe]");
		Student stu2 = dao.fetch(Student.class, 1);
		assertEquals("xyz", stu2.getAboutMe());
		assertEquals(email, stu2.getEmail());
	}

	public void testDeleteById() {
		Student stu = setupStudent(new Student(), "s2");
		dao.insert(stu);
		assertEquals(2, stu.getId());
		assertEquals(2, dao.count(Student.class, null));
		dao.delete(Student.class, stu.getId());
		assertEquals(1, dao.count(Student.class, null));
	}

	public void testDeleteByName() {
		Student stu = setupStudent(new Student(), "s3");
		dao.insert(stu);
		assertEquals(2, dao.count(Student.class, null));
		dao.delete(Student.class, stu.getName());
		assertEquals(1, dao.count(Student.class, null));
	}

	public void testQuery() {
		dao.insert(setupStudent(new Student(), "s4"));
		dao.insert(setupStudent(new Student(), "s5"));
		dao.insert(setupStudent(new Student(), "s6"));
		dao.insert(setupStudent(new Student(), "s7"));
		List<?> list = (List<?>) dao.query(Student.class, null, null);
		assertEquals(5, list.size());
	}

	public void testQueryByCondition() {
		List<?> list = (List<?>) dao.query(Student.class, new Condition() {
			public String toString(Entity<?> entity) {
				return "WHERE name>'s5'";
			}
		}, null);
		assertEquals(3, list.size());
	}

	public void testQueryByPager() {
		Pager p = new Pager(2, 2);
		List<Student> list = dao.query(Student.class, null, p);
		assertEquals(5, p.getRecordCount());
		assertEquals(2, list.size());
		assertEquals(6, list.get(1).getId());
		assertEquals("s6", list.get(1).getName());
	}

	public void testClear() {
		dao.clear(Student.class, new Condition() {
			@Override
			public String toString(Entity<?> entity) {
				return "WHERE name>'s5' AND name!='zzh'";
			}
		});
		assertEquals(3, dao.count(Student.class, null));
		dao.clear(Student.class, null);
		assertEquals(0, dao.count(Student.class, null));
	}

	public void testDefaultValue() {
		Student stu = setupStudent(new Student(), "zzh");
		stu.setAboutMe(null);
		stu = dao.insert(stu);
		assertEquals("I am zzh", stu.getAboutMe());
	}

}
