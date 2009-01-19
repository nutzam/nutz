package com.zzh.dao.impl;

import java.sql.Timestamp;
import java.util.Calendar;

import com.zzh.dao.SQL;
import com.zzh.dao.entity.EntityHolder;
import com.zzh.dao.entity.annotation.*;
import com.zzh.dao.impl.NutSQLMaker;
import com.zzh.lang.meta.Email;
import com.zzh.lang.types.Castors;

import junit.framework.TestCase;

public class NutSQLMakerTest extends TestCase {

	/**
	 * Entity class
	 */
	@Table("t_xyz")
	public class Entity {

		@Column
		@Id
		private int id;

		@Column
		@Name
		private String name;

		@Column
		private short age;

		@Column("myEmail")
		@NotNull
		private Email email;

		@Column
		private Calendar calendar;

		@Column
		private java.sql.Date sqlDate;

		@Column
		private java.sql.Time sqlTime;

		@Column
		private java.util.Date javaDate;

		@Column
		private Timestamp timestamp;

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

		public short getAge() {
			return age;
		}

		public void setAge(short age) {
			this.age = age;
		}

		public Email getEmail() {
			return email;
		}

		public void setEmail(Email email) {
			this.email = email;
		}

		public Calendar getCalendar() {
			return calendar;
		}

		public void setCalendar(Calendar calendar) {
			this.calendar = calendar;
		}

		public java.sql.Date getSqlDate() {
			return sqlDate;
		}

		public void setSqlDate(java.sql.Date sqlDate) {
			this.sqlDate = sqlDate;
		}

		public java.util.Date getJavaDate() {
			return javaDate;
		}

		public void setJavaDate(java.util.Date javaDate) {
			this.javaDate = javaDate;
		}

		public Timestamp getTimestamp() {
			return timestamp;
		}

		public void setTimestamp(Timestamp timestamp) {
			this.timestamp = timestamp;
		}

		public java.sql.Time getSqlTime() {
			return sqlTime;
		}

		public void setSqlTime(java.sql.Time sqlTime) {
			this.sqlTime = sqlTime;
		}

	}

	NutSQLMaker maker;
	Entity entity;

	@Override
	protected void setUp() throws Exception {
		EntityHolder holder = new EntityHolder(Castors.me());
		maker = new NutSQLMaker(holder);

		Calendar c = Calendar.getInstance();
		c.set(2008, 8, 8, 20, 0, 0);
		entity = new Entity();
		entity.setId(23);
		entity.setName("ABC");
		entity.setAge((short) 10);
		entity.setCalendar(c);
		entity.setJavaDate(c.getTime());
		entity.setSqlDate(new java.sql.Date(c.getTimeInMillis()));
		entity.setTimestamp(new Timestamp(c.getTimeInMillis()));
		entity.setSqlTime(new java.sql.Time(c.getTimeInMillis()));
		entity.setEmail(new Email("zzh@263.net"));
	}

	public void testMakeInsertSQL() {
		SQL<?> sql = maker.makeInsertSQL(Entity.class);
		sql.setValue(entity);
		String exp = "INSERT INTO t_xyz(timestamp,javaDate,myEmail,age,name,sqlTime,sqlDate,calendar) VALUES('2008-09-08 20:00:00','2008-09-08 20:00:00','zzh@263.net',10,'ABC','20:00:00','2008-09-08','2008-09-08 20:00:00');";
		assertEquals(exp, sql.toString());
	}

	public void testMakeClearSQL() {
		SQL<?> sql = maker.makeClearSQL(Entity.class);
		sql.set("condition", "[...]");
		String exp = "DELETE FROM t_xyz [...];";
		assertEquals(exp, sql.toString());
	}

	public void testMakeUpdateSQL() {
		SQL<?> sql = maker.makeUpdateSQL(Entity.class);
		sql.setValue(entity);
		String exp = "UPDATE t_xyz SET timestamp='2008-09-08 20:00:00',javaDate='2008-09-08 20:00:00',myEmail='zzh@263.net',age=10,name='ABC',sqlTime='20:00:00',sqlDate='2008-09-08',calendar='2008-09-08 20:00:00' WHERE id=23;";
		assertEquals(exp, sql.toString());
	}

	public void testMakeDeleteByIdSQL() {
		SQL<?> sql = maker.makeDeleteByIdSQL(Entity.class, 10);
		String exp = "DELETE FROM t_xyz WHERE id=10;";
		assertEquals(exp, sql.toString());
	}

	public void testMakeDeleteByNameSQL() {
		SQL<?> sql = maker.makeDeleteByNameSQL(Entity.class, "ABC");
		String exp = "DELETE FROM t_xyz WHERE name='ABC';";
		assertEquals(exp, sql.toString());
	}

	public void testMakeFetchByIdSQL() {
		SQL<?> sql = maker.makeFetchByIdSQL(Entity.class, 10);
		String exp = "SELECT * FROM t_xyz WHERE id=10;";
		assertEquals(exp, sql.toString());
	}

	public void testMakeFetchByNameSQL() {
		SQL<?> sql = maker.makeFetchByNameSQL(Entity.class, "ABC");
		String exp = "SELECT * FROM t_xyz WHERE name='ABC';";
		assertEquals(exp, sql.toString());
	}

	public void testMakeQuerySQL() {
		SQL<?> sql = maker.makeQuerySQL(Entity.class);
		sql.set("condition", "[...]");
		String exp = "SELECT * FROM t_xyz [...];";
		assertEquals(exp, sql.toString());
	}

	@Table("t_ne")
	public class NameEntity {
		@Column
		@Name
		public String name;

		@Column
		public String text;

	}

	public void testMakeUpdateByNameSQL() {
		SQL<?> sql = maker.makeUpdateSQL(NameEntity.class);
		NameEntity entity = new NameEntity();
		entity.name = "zzh";
		entity.text = "great";
		sql.setValue(entity);
		String exp = "UPDATE t_ne SET text='great',name='zzh' WHERE name='zzh';";
		assertTrue(exp.equals(sql.toString()));
	}

}
