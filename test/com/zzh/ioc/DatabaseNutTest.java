package com.zzh.ioc;

import java.util.Calendar;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

import com.zzh.Main;
import com.zzh.castor.Castors;
import com.zzh.dao.ComboSql;
import com.zzh.dao.Dao;
import com.zzh.dao.ExecutableSql;
import com.zzh.dao.Sql;
import com.zzh.dao.impl.FileSqlManager;
import com.zzh.dao.impl.NutDao;
import com.zzh.ioc.db.DatabaseAssemble;
import com.zzh.ioc.db.ObjectBean;
import com.zzh.ioc.db.FieldBean;
import com.zzh.lang.Mirror;
import com.zzh.service.EntityService;
import com.zzh.service.NutEntityService;

import junit.framework.TestCase;

public class DatabaseNutTest extends TestCase {

	private static FieldBean makeField(int objId, String name, String value) {
		FieldBean fb = new FieldBean();
		fb.setName(name);
		fb.setValue(value);
		fb.setObjectId(objId);
		return fb;
	}

	private static ObjectBean makeObj(Class<?> classOfT, String objName, boolean singleton,
			boolean anonymous) {
		ObjectBean obj = new ObjectBean();
		obj.setName(objName);
		obj.setType(Mirror.me(classOfT));
		obj.setSingleton(singleton);
		obj.setAnonymous(anonymous);
		return obj;
	}

	private Nut nut;

	public class Company {
		public String name;
		public byte year;
		public float profits;
		public boolean closed;
		public Calendar createTime;
	}

	public class Employee {
		public String name;
		public Company company;
	}

	@Override
	protected void setUp() throws Exception {
		Dao dao = Main.getDao("com/zzh/ioc/dbtest.sqls");
		ComboSql combo = dao.sqls().createComboSQL(".object.drop", ".object.create", ".field.drop",
				".field.create");
		dao.execute(combo);
		Assemble ass = new DatabaseAssemble(dao);
		nut = new Nut(ass, Castors.me());
		// prepare data service
		NutEntityService<ObjectBean> objsrv = new NutEntityService<ObjectBean>(dao) {
		};
		EntityService<FieldBean> fldsrv = new EntityService<FieldBean>(dao) {
		};
		// prepare datasource
		ObjectBean obj = objsrv.insert(makeObj(BasicDataSource.class, "dataSource", true, false));
		fldsrv.insert(makeField(obj.getId(), "driverClassName", Main.getDriver()));
		fldsrv.insert(makeField(obj.getId(), "url", Main.getUrl()));
		fldsrv.insert(makeField(obj.getId(), "username", Main.getUserName()));
		fldsrv.insert(makeField(obj.getId(), "password", Main.getPassword()));
		// prepare castors
		obj = objsrv.insert(makeObj(Castors.class, "castors", true, false));
		// prepare sqlManager
		obj = objsrv.insert(makeObj(FileSqlManager.class, "sqlManager", true, false));
		fldsrv.insert(makeField(obj.getId(), "castors", "-> castors"));
		fldsrv.insert(makeField(obj.getId(), "paths", "\"com/zzh/ioc/dbtest.sqls\""));
		// prepare dao
		obj = objsrv.insert(makeObj(NutDao.class, "dao", true, false));
		fldsrv.insert(makeField(obj.getId(), "$0", "-> dataSource"));
		fldsrv.insert(makeField(obj.getId(), "$1", "-> castors"));
		fldsrv.insert(makeField(obj.getId(), "$2", "-> sqlManager"));
		// Prepare company
		obj = objsrv.insert(makeObj(Company.class, "dtri", true, false));
		fldsrv.insert(makeField(obj.getId(), "name", "DT Research Inc."));
		fldsrv.insert(makeField(obj.getId(), "year", "15"));
		fldsrv.insert(makeField(obj.getId(), "profits", ".25"));
		fldsrv.insert(makeField(obj.getId(), "closed", "false"));
		fldsrv.insert(makeField(obj.getId(), "createTime", "1995-8-7 14:23:23"));
		// Prepare emplyoo
		obj = objsrv.insert(makeObj(Employee.class, "zzh", false, false));
		fldsrv.insert(makeField(obj.getId(), "name", "Peter.Zhang"));
		fldsrv.insert(makeField(obj.getId(), "company", "-> dtri"));
		obj = objsrv.insert(makeObj(Employee.class, "joey", false, false));
		fldsrv.insert(makeField(obj.getId(), "name", "Joey.Huang"));
		fldsrv.insert(makeField(obj.getId(), "company", "-> dtri"));
	}

	@Override
	protected void tearDown() throws Exception {
		Main.closeDataSource();
	}

	public void testGetDataSource() throws Exception {
		DataSource dataSource = nut.getObject(DataSource.class, "dataSource");
		Dao dao = new NutDao(dataSource);
		assertTrue(dao.count(ObjectBean.class, null) > 0);
	}

	public void testGetDao() throws Exception {
		Dao dao = nut.getObject(Dao.class, "dao");
		assertTrue(dao.count(ObjectBean.class, null) > 0);
		Sql<?> sql = dao.sqls().createSql(".field.create");
		assertTrue(sql instanceof ExecutableSql);
		assertEquals(4, dao.sqls().count());
	}

	public void testSingleton() throws Exception {
		Dao dao1 = nut.getObject(Dao.class, "dao");
		Dao dao2 = nut.getObject(Dao.class, "dao");
		assertEquals(dao1, dao2);
	}

	public void testCompany() throws Exception {
		Company dtri = nut.getObject(Company.class, "dtri");
		assertEquals("DT Research Inc.", dtri.name);
		assertFalse(dtri.closed);
		assertEquals(15, dtri.year);
		assertEquals(.25f, dtri.profits);
	}

	public void testSgngleton2() throws Exception {
		Employee zzh = nut.getObject(Employee.class, "zzh");
		Employee zzh2 = nut.getObject(Employee.class, "zzh");
		assertFalse(zzh == zzh2);
		assertEquals(zzh.name, zzh2.name);
		assertTrue(zzh.company == zzh2.company);
		Employee joey = nut.getObject(Employee.class, "joey");
		assertEquals("Joey.Huang", joey.name);
		assertTrue(zzh.company == joey.company);
	}

}
