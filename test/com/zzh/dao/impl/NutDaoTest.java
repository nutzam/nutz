package com.zzh.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.dbcp.BasicDataSource;

import com.zzh.lang.random.GM;
import com.zzh.lang.random.StringGenerator;
import com.zzh.dao.Condition;
import com.zzh.dao.Dao;
import com.zzh.dao.ExecutableSQL;
import com.zzh.dao.FetchSQL;
import com.zzh.dao.QuerySQL;
import com.zzh.dao.SQL;
import com.zzh.dao.SQLManager;
import com.zzh.dao.callback.QueryCallback;
import com.zzh.dao.entity.Entity;
import com.zzh.dao.entity.annotation.*;
import com.zzh.dao.impl.NutDao;
import com.zzh.lang.Lang;
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
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setUsername("root");
		dataSource.setPassword("123456");
		dataSource.setUrl("jdbc:mysql://localhost:3306/zzhtest");
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		SQLManager sqlManager = new com.zzh.dao.impl.FileSQLManager("com/zzh/dao/impl/test.sqls");
		NutDao dao = new com.zzh.dao.impl.NutDao();
		dao.setDataSource(dataSource);
		dao.setSqlManager(sqlManager);
		this.dao = dao;
	}

	public void testGetSQL() {
		assertTrue(dao.sqls().createSQL(".abc.create") instanceof ExecutableSQL);
	}

	public void testExecuteBatchSQLs() {
		try {
			SQL<?> sqlDrop = dao.sqls().createSQL(".abc.drop");
			SQL<?> sqlCreate = dao.sqls().createSQL(".abc.create");
			dao.execute(sqlDrop, sqlCreate);
		} catch (Exception e) {
			fail();
		}
	}

	public void testExecuteInsertSQL() {
		try {
			SQL<?> sql = dao.sqls().createSQL(".abc.insert");
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
			SQL<?> sql = dao.sqls().createSQL(".abc.update");
			sql.set("name", "zozoh");
			sql.set("id", 1);
			dao.execute(sql);
		} catch (Exception e) {
			fail();
		}
	}

	public void testExecuteFetchSQL() {
		try {
			FetchSQL<Abc> sql = (FetchSQL<Abc>) dao.sqls().createSQL("fetch.abc", Abc.class);
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
			QuerySQL sql = (QuerySQL) dao.sqls().createSQL("query.abc");
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
			fail();
		}
	}

	@Table("student")
	public class Student {

		@Column
		@Id
		private int id;

		@Column
		@NotNull
		@Name
		private String name;

		@Column
		private int age;

		@Column
		private Email email;

		@Column
		@Default("I am ${name}")
		private String aboutMe;

		@Column
		private Calendar birthday;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getAge() {
			return age;
		}

		public void setAge(int age) {
			this.age = age;
		}

		public Email getEmail() {
			return email;
		}

		public void setEmail(Email email) {
			this.email = email;
		}

		public String getAboutMe() {
			return aboutMe;
		}

		public void setAboutMe(String aboutMe) {
			this.aboutMe = aboutMe;
		}

		public Calendar getBirthday() {
			return birthday;
		}

		public void setBirthday(Calendar birthday) {
			this.birthday = birthday;
		}

		public boolean equals(Student stu) {
			if (id != stu.id)
				return false;
			if (!Lang.equals(name, stu.name))
				return false;
			if (!Lang.equals(aboutMe, stu.aboutMe))
				return false;
			if (age != stu.age)
				return false;
			if (null == email && stu.email != null)
				return false;
			if (!email.equals(stu.email))
				return false;
			if (null == birthday && null == stu.birthday)
				return true;
			if (null == birthday || null == stu.birthday)
				return false;
			if (birthday.getTimeInMillis() != stu.birthday.getTimeInMillis())
				return false;
			return true;
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
			@Override
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
