package com.zzh.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.zzh.Main;
import com.zzh.lang.random.GM;
import com.zzh.dao.Condition;
import com.zzh.dao.Conditions;
import com.zzh.dao.Dao;
import com.zzh.dao.ExecutableSql;
import com.zzh.dao.FetchSql;
import com.zzh.dao.Pager;
import com.zzh.dao.QuerySql;
import com.zzh.dao.Sql;
import com.zzh.dao.callback.QueryCallback;
import com.zzh.dao.entity.Entity;
import com.zzh.dao.entity.annotation.*;
import com.zzh.lang.meta.Email;

import junit.framework.TestCase;

public class NutDaoTest extends TestCase {

	@Table("t_abc")
	public static class Abc {

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

	public void testFetchById() {
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

	public static class FetchAbc extends FetchSql<Abc> {
	}

	public void testExecuteFetchSQL() {
		try {
			FetchAbc sql = dao.sqls().createSql(FetchAbc.class, "abc.fetch");
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
			QuerySql sql = (QuerySql) dao.sqls().createSql("abc.query");
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
		return Student.make(name, GM.random(10, 45));
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

	public void testFetchByCondition() {
		dao.clear(Student.class, null);
		Student stu = setupStudent(new Student(), "linux");
		stu.setAboutMe("I love Linux");
		stu.setEmail(new Email("linux@zozoh.com"));
		stu.setAge(32);
		dao.insert(stu);

		stu = dao.fetch(Student.class, Conditions.format("aboutme LIKE '%%%s%%'", "Linux"));
		assertEquals(32, stu.getAge());
	}

	public void testUpdate() {
		dao.clear(Student.class, null);
		Student stu = setupStudent(new Student(), "zzh");
		stu = dao.insert(stu);
		stu = dao.fetch(Student.class, "zzh");
		String txt = "I am great!!!";
		stu.setAboutMe(txt);
		dao.update(stu);
		Student stu2 = dao.fetch(Student.class, "zzh");
		assertTrue(stu.equals(stu2));
	}

	public void testUpdate2Default() {
		Student stu = dao.fetch(Student.class, "zzh");
		stu.setAboutMe(null);
		dao.update(stu);
		Student stu2 = dao.fetch(Student.class, "zzh");
		assertEquals("I am zzh", stu2.getAboutMe());
	}

	public void testUpdateNull() {
		Student stu = dao.fetch(Student.class, "zzh");
		stu.setEmail(null);
		dao.update(stu);
		Student stu2 = dao.fetch(Student.class, "zzh");
		assertNull(stu2.getEmail());
	}

	public void testUpdateIgnoreNull() {
		Student stu = dao.fetch(Student.class, "zzh");
		Email email = stu.getEmail();
		stu.setEmail(null);
		dao.update(stu, true);
		Student stu2 = dao.fetch(Student.class, "zzh");
		assertEquals(email, stu2.getEmail());
	}

	public void testUpdateIgnoredField() {
		Student stu = dao.fetch(Student.class, "zzh");
		Email email = stu.getEmail();
		stu.setEmail(new Email("bbb@bbb.com"));
		stu.setAboutMe("haha");
		dao.update(stu, "email", null);
		Student stu2 = dao.fetch(Student.class, "zzh");
		assertEquals("haha", stu2.getAboutMe());
		assertEquals(email, stu2.getEmail());
	}

	public void testUpdateActivedField() {
		Student stu = dao.fetch(Student.class, "zzh");
		Email email = stu.getEmail();
		boolean isnew = stu.isNew();
		stu.setEmail(new Email("bbb@bbb.com"));
		stu.setAboutMe("xyz");
		stu.setAge(77);
		stu.setNew(!isnew);
		dao.update(stu, null, "aboutMe|age");
		Student stu2 = dao.fetch(Student.class, "zzh");
		assertEquals("xyz", stu2.getAboutMe());
		assertEquals(email, stu2.getEmail());
		assertEquals(77, stu2.getAge());
		assertEquals(isnew, stu2.isNew());
	}

	public void testDeleteById() {
		initStudentsData(dao);
		Student stu = dao.fetch(Student.class, (Condition) null);
		assertEquals(5, dao.count(Student.class, null));
		dao.delete(Student.class, stu.getId());
		assertEquals(4, dao.count(Student.class, null));
	}

	public void testDeleteByName() {
		initStudentsData(dao);
		Student stu = dao.fetch(Student.class, (Condition) null);
		assertEquals(5, dao.count(Student.class, null));
		dao.delete(Student.class, stu.getName());
		assertEquals(4, dao.count(Student.class, null));
	}

	public void testQuery() {
		initStudentsData(dao);
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
		initStudentsData(dao);
		Pager p = com.zzh.dao.Pager.create(null, 2, 2);
		List<Student> list = dao.query(Student.class, null, p);
		assertEquals(5, p.getRecordCount());
		assertEquals(2, list.size());
		assertEquals("ttt", list.get(1).getName());
	}

	public void testClear() {
		initStudentsData(dao);
		dao.clear(Student.class, Conditions.wrap("name LIKE '%t%'"));
		assertEquals(3, dao.count(Student.class, null));
		dao.clear(Student.class, null);
		assertEquals(0, dao.count(Student.class, null));
	}

	private static void initStudentsData(Dao dao) {
		dao.clear(Student.class, null);
		assertEquals(0, dao.count(Student.class, null));
		dao.insert(setupStudent(new Student(), "abc"));
		dao.insert(setupStudent(new Student(), "xyz"));
		dao.insert(setupStudent(new Student(), "mmm"));
		dao.insert(setupStudent(new Student(), "ttt"));
		dao.insert(setupStudent(new Student(), "ytm"));
	}

	public void testDefaultValue() {
		dao.clear(Student.class, null);
		Student stu = setupStudent(new Student(), "zzh");
		stu.setAboutMe(null);
		stu = dao.insert(stu);
		assertEquals("I am zzh", stu.getAboutMe());
	}

}
