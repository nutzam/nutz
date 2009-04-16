package com.zzh.ioc;

import static org.junit.Assert.*;

import java.util.Calendar;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.junit.Test;

import com.zzh.Main;
import com.zzh.dao.Dao;
import com.zzh.dao.ExecutableSql;
import com.zzh.dao.Sql;
import com.zzh.dao.impl.FileSqlManager;
import com.zzh.dao.impl.NutDao;
import com.zzh.dao.test.DaoCase;
import com.zzh.ioc.db.DatabaseMappingLoader;
import com.zzh.ioc.db.FieldBean;
import com.zzh.ioc.db.ObjectBean;
import com.zzh.ioc.db.ValueBean;
import com.zzh.service.EntityService;
import com.zzh.service.IdNameEntityService;

public class DatabaseIocTest extends DaoCase {

	private static FieldBean makeField(int objId, String name, ValueBean value) {
		FieldBean fb = new FieldBean();
		fb.setName(name);
		fb.setValue(value);
		fb.setObjectId(objId);
		return fb;
	}

	private static ValueBean makeValue(String type, String value) {
		ValueBean vb = new ValueBean();
		vb.setType(type);
		vb.setValue(value);

		return vb;
	}

	private static ValueBean makeValue(String value) {
		return makeValue(null, value);
	}

	private static ObjectBean makeObj(Class<?> classOfT, String parentName, String objName,
			String depmd, String deptype) {
		ObjectBean ob = makeObj(classOfT, objName, true, false);
		ob.setParentName(parentName);
		ob.setDeposeMethodName(depmd);
		ob.setDeposerTypeName(deptype);
		return ob;
	}

	private static ObjectBean makeObj(Class<?> classOfT, String objName, boolean singleton,
			boolean anonymous) {
		ObjectBean obj = new ObjectBean();
		obj.setName(objName);
		obj.setType(classOfT);
		obj.setSingleton(singleton);
		obj.setAnonymous(anonymous);
		return obj;
	}

	private Nut nut;

	public static class Company {
		public String name;
		public byte year;
		public float profits;
		public boolean closed;
		public Calendar createTime;
	}

	public static class Employee {
		public String name;
		public Company company;
	}

	static class FieldBeanService extends EntityService<FieldBean> {

		protected FieldBeanService(Dao dao) {
			super(dao);
		}

		public FieldBean insert(FieldBean obj) {
			if (obj.getValue() != null) {
				obj.setValueId(dao().insert(obj.getValue()).getId());
			}
			return dao().insert(obj);
		}
	}

	@Override
	protected void before() {
		pojos.execFile("com/zzh/ioc/dbtest.sqls");
		MappingLoader ass = new DatabaseMappingLoader(dao);
		nut = new Nut(ass);
		// prepare data service
		IdNameEntityService<ObjectBean> objsrv = new IdNameEntityService<ObjectBean>(dao) {};
		FieldBeanService fldsrv = new FieldBeanService(dao);
		// prepare datasource
		ObjectBean obj = objsrv.dao().insert(
				makeObj(BasicDataSource.class, "dataSource", true, false));
		fldsrv.insert(makeField(obj.getId(), "driverClassName", makeValue(Main.getDriver())));
		fldsrv.insert(makeField(obj.getId(), "url", makeValue(Main.getUrl())));
		fldsrv.insert(makeField(obj.getId(), "username", makeValue(Main.getUserName())));
		fldsrv.insert(makeField(obj.getId(), "password", makeValue(Main.getPassword())));
		// prepare sqlManager
		obj = objsrv.dao().insert(makeObj(FileSqlManager.class, "sqlManager", true, false));
		fldsrv.insert(makeField(obj.getId(), "paths", makeValue("com/zzh/ioc/dbtest.sqls")));
		// prepare dao
		obj = objsrv.dao().insert(makeObj(NutDao.class, "dao", true, false));
		fldsrv.insert(makeField(obj.getId(), "dataSource", makeValue("refer", "dataSource")));
		fldsrv.insert(makeField(obj.getId(), "sqlManager", makeValue("refer", "sqlManager")));
		// Prepare company
		obj = objsrv.dao().insert(makeObj(Company.class, "dtri", true, false));
		fldsrv.insert(makeField(obj.getId(), "name", makeValue("DT Research Inc.")));
		fldsrv.insert(makeField(obj.getId(), "year", makeValue("15")));
		fldsrv.insert(makeField(obj.getId(), "profits", makeValue(".25")));
		fldsrv.insert(makeField(obj.getId(), "closed", makeValue("false")));
		fldsrv.insert(makeField(obj.getId(), "createTime", makeValue("1995-8-7 14:23:23")));
		// Prepare emplyoo
		obj = objsrv.dao().insert(makeObj(Employee.class, "zzh", false, false));
		fldsrv.insert(makeField(obj.getId(), "name", makeValue("Peter.Zhang")));
		fldsrv.insert(makeField(obj.getId(), "company", makeValue("refer", "dtri")));
		obj = objsrv.dao().insert(makeObj(Employee.class, "joey", false, false));
		fldsrv.insert(makeField(obj.getId(), "name", makeValue("Joey.Huang")));
		fldsrv.insert(makeField(obj.getId(), "company", makeValue("refer", "dtri")));
		// Prepare Fruits
		obj = objsrv.dao().insert(makeObj(Fruit.class, "seasonFruit", true, false));
		fldsrv.insert(makeField(obj.getId(), "onSale", makeValue("true")));
		obj = objsrv.dao().insert(makeObj(Fruit.class, "expiredFruit", true, false));
		fldsrv.insert(makeField(obj.getId(), "onSale", makeValue("false")));

		obj = objsrv.dao().insert(makeObj(Fruit.class, "seasonFruit", "apple", "destroy", null));
		fldsrv.insert(makeField(obj.getId(), "name", makeValue("Apple")));
		fldsrv.insert(makeField(obj.getId(), "price", makeValue("4")));

		obj = objsrv.dao().insert(
				makeObj(Fruit.class, "apple", "guoguang", "", "com.zzh.ioc.FruitDeposer"));
		fldsrv.insert(makeField(obj.getId(), "price", makeValue("3")));

		obj = objsrv.dao().insert(makeObj(Fruit.class, "expiredFruit", "strawberry", null, null));
		fldsrv.insert(makeField(obj.getId(), "name", makeValue("Strawberry")));
		fldsrv.insert(makeField(obj.getId(), "price", makeValue("15")));
	}

	@Override
	protected void after() {}

	@Test
	public void testGetDataSource() throws Exception {
		DataSource dataSource = nut.get(DataSource.class, "dataSource");
		Dao dao = new NutDao(dataSource);
		assertTrue(dao.count(ObjectBean.class, null) > 0);
	}

	@Test
	public void testGetDao() throws Exception {
		Dao dao = nut.get(Dao.class, "dao");
		assertTrue(dao.count(ObjectBean.class, null) > 0);
		Sql<?> sql = dao.sqls().createSql(".field.create");
		assertTrue(sql instanceof ExecutableSql);
	}

	@Test
	public void testSingleton() throws Exception {
		Dao dao1 = nut.get(Dao.class, "dao");
		Dao dao2 = nut.get(Dao.class, "dao");
		assertEquals(dao1, dao2);
	}

	@Test
	public void testCompany() throws Exception {
		Company dtri = nut.get(Company.class, "dtri");
		assertEquals("DT Research Inc.", dtri.name);
		assertFalse(dtri.closed);
		assertEquals(15, dtri.year);
		assertEquals(.25f, dtri.profits);
	}

	@Test
	public void testSgngleton2() throws Exception {
		Employee zzh = nut.get(Employee.class, "zzh");
		Employee zzh2 = nut.get(Employee.class, "zzh");
		assertFalse(zzh == zzh2);
		assertEquals(zzh.name, zzh2.name);
		assertTrue(zzh.company == zzh2.company);
		Employee joey = nut.get(Employee.class, "joey");
		assertEquals("Joey.Huang", joey.name);
		assertTrue(zzh.company == joey.company);
	}

	@Test
	public void testFruit() {
		Fruit apple = nut.get(Fruit.class, "apple");
		assertTrue(apple.isOnSale());
		assertEquals(4, apple.getPrice());

		Fruit gg = nut.get(Fruit.class, "guoguang");
		assertTrue(gg.isOnSale());
		assertEquals("Apple", gg.getName());
		assertEquals(3, gg.getPrice());

		Fruit strawberry = nut.get(Fruit.class, "strawberry");
		assertFalse(strawberry.isOnSale());

		// test depose
		nut.depose();
		assertEquals(-1, apple.getPrice());
		assertEquals(-2, gg.getPrice());
	}

}
