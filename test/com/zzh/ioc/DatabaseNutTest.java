package com.zzh.ioc;

import java.util.Calendar;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

import com.zzh.Main;
import com.zzh.dao.ComboSql;
import com.zzh.dao.Dao;
import com.zzh.dao.ExecutableSql;
import com.zzh.dao.Sql;
import com.zzh.dao.impl.FileSqlManager;
import com.zzh.dao.impl.NutDao;
import com.zzh.ioc.db.DatabaseMappingLoader;
import com.zzh.ioc.db.FieldBean;
import com.zzh.ioc.db.ObjectBean;
import com.zzh.ioc.db.ValueBean;
import com.zzh.service.EntityService;
import com.zzh.service.IdNameEntityService;

import junit.framework.TestCase;

public class DatabaseNutTest extends TestCase {

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

	@Override
	protected void setUp() throws Exception {
		Dao dao = Main.getDao("com/zzh/ioc/dbtest.sqls");
		ComboSql combo = dao.sqls().createComboSql();
		dao.execute(combo);
		MappingLoader ass = new DatabaseMappingLoader(dao);
		nut = new Nut(ass);
		// prepare data service
		IdNameEntityService<ObjectBean> objsrv = new IdNameEntityService<ObjectBean>(dao) {};
		EntityService<FieldBean> fldsrv = new EntityService<FieldBean>(dao) {

			@Override
			public FieldBean insert(FieldBean obj) {
				if (obj.getValue() != null) {
					obj.setValueId(dao().insert(obj.getValue()).getId());
				}
				return super.insert(obj);
			}

		};
		// prepare datasource
		ObjectBean obj = objsrv.insert(makeObj(BasicDataSource.class, "dataSource", true, false));
		fldsrv.insert(makeField(obj.getId(), "driverClassName", makeValue(null, Main.getDriver())));
		fldsrv.insert(makeField(obj.getId(), "url", makeValue(null, Main.getUrl())));
		fldsrv.insert(makeField(obj.getId(), "username", makeValue(null, Main.getUserName())));
		fldsrv.insert(makeField(obj.getId(), "password", makeValue(null, Main.getPassword())));
		// prepare sqlManager
		obj = objsrv.insert(makeObj(FileSqlManager.class, "sqlManager", true, false));
		fldsrv.insert(makeField(obj.getId(), "paths",
				makeValue(null, "\"com/zzh/ioc/dbtest.sqls\"")));
		// prepare dao
		obj = objsrv.insert(makeObj(NutDao.class, "dao", true, false));
		fldsrv.insert(makeField(obj.getId(), "dataSource", makeValue("refer", "dataSource")));
		fldsrv.insert(makeField(obj.getId(), "sqlManager", makeValue("refer", "sqlManager")));
		// Prepare company
		obj = objsrv.insert(makeObj(Company.class, "dtri", true, false));
		fldsrv.insert(makeField(obj.getId(), "name", makeValue(null, "DT Research Inc.")));
		fldsrv.insert(makeField(obj.getId(), "year", makeValue(null, "15")));
		fldsrv.insert(makeField(obj.getId(), "profits", makeValue(null, ".25")));
		fldsrv.insert(makeField(obj.getId(), "closed", makeValue(null, "false")));
		fldsrv.insert(makeField(obj.getId(), "createTime", makeValue(null, "1995-8-7 14:23:23")));
		// Prepare emplyoo
		obj = objsrv.insert(makeObj(Employee.class, "zzh", false, false));
		fldsrv.insert(makeField(obj.getId(), "name", makeValue(null, "Peter.Zhang")));
		fldsrv.insert(makeField(obj.getId(), "company", makeValue("refer", "dtri")));
		obj = objsrv.insert(makeObj(Employee.class, "joey", false, false));
		fldsrv.insert(makeField(obj.getId(), "name", makeValue(null, "Joey.Huang")));
		fldsrv.insert(makeField(obj.getId(), "company", makeValue("refer", "dtri")));
		// Prepare Fruits
		obj = objsrv.insert(makeObj(Fruit.class, "seasonFruit", true, false));
		fldsrv.insert(makeField(obj.getId(), "onSale", makeValue(null, "true")));
		obj = objsrv.insert(makeObj(Fruit.class, "expiredFruit", true, false));
		fldsrv.insert(makeField(obj.getId(), "onSale", makeValue(null, "false")));

		obj = objsrv.insert(makeObj(Fruit.class, "seasonFruit", "apple", "destroy", null));
		fldsrv.insert(makeField(obj.getId(), "name", makeValue(null, "Apple")));
		fldsrv.insert(makeField(obj.getId(), "price", makeValue(null, "4")));

		obj = objsrv.insert(makeObj(Fruit.class, "apple", "guoguang", "",
				"com.zzh.ioc.FruitDeposer"));
		fldsrv.insert(makeField(obj.getId(), "price", makeValue(null, "3")));
		
		obj = objsrv.insert(makeObj(Fruit.class, "expiredFruit", "strawberry", null, null));
		fldsrv.insert(makeField(obj.getId(), "name", makeValue(null, "Strawberry")));
		fldsrv.insert(makeField(obj.getId(), "price", makeValue(null, "15")));

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
	
	public void testFruit() {
		Fruit apple = nut.getObject(Fruit.class, "apple");
		assertTrue(apple.isOnSale());
		assertEquals(4, apple.getPrice());

		Fruit gg = nut.getObject(Fruit.class, "guoguang");
		assertTrue(gg.isOnSale());
		assertEquals("Apple", gg.getName());
		assertEquals(3, gg.getPrice());

		Fruit strawberry = nut.getObject(Fruit.class, "strawberry");
		assertFalse(strawberry.isOnSale());

		// test depose
		nut.depose();
		assertEquals(-1, apple.getPrice());
		assertEquals(-2, gg.getPrice());
	}

}
