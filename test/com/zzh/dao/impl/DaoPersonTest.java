package com.zzh.dao.impl;

import com.zzh.Main;
import com.zzh.dao.Dao;

import junit.framework.TestCase;

public class DaoPersonTest extends TestCase {

	private Dao dao;

	@Override
	protected void setUp() throws Exception {
		this.dao = Main.getDao("com/zzh/dao/impl/personTest.sqls");
		Person.prepareTable(dao);
	}

	@Override
	protected void tearDown() throws Exception {
		Main.closeDataSource();
	}

	public void testFetchOne() throws Exception {
		Person zzh = dao.fetch(Person.class, "zzh");
		dao.fetchOne(zzh, "father");
		assertEquals("ycs", zzh.getFather().getName());
	}

	public void testFetchOneByName() throws Exception {
		Person john = dao.fetch(Person.class, "John");
		dao.fetchOne(john, "master");
		assertEquals("zzh", john.getMaster().getName());
	}

	public void testFetchManyByList() throws Exception {
		Person ycs = dao.fetch(Person.class, "ycs");
		dao.fetchMany(ycs, "children");
		assertEquals(2, ycs.getChildren().size());
		assertEquals("zzh", ycs.getChildren().get(0).getName());
		assertEquals("ydl", ycs.getChildren().get(1).getName());
	}

	public void testFetchManyByArray() throws Exception {
		Person zzh = dao.fetch(Person.class, "zzh");
		dao.fetchMany(zzh, "employees");
		assertEquals(2, zzh.getEmployees().length);
		assertEquals("Merry", zzh.getEmployees()[0].getName());
		assertEquals("John", zzh.getEmployees()[1].getName());
	}

	public void testFetchManyByName() throws Exception {
		Person zzh = dao.fetch(Person.class, "zzh");
		dao.fetchMany(zzh, "students");
		assertEquals(2, zzh.getStudents().size());
		assertEquals("Merry", zzh.getStudents().get(0).getName());
		assertEquals("John", zzh.getStudents().get(1).getName());
	}

	public void testDeleteOne() throws Exception {
		Person pp = dao.fetch(Person.class, "ycs");
		dao.fetchOne(pp, "father");
		assertEquals("yy", pp.getFather().getName());
		dao.deleteOne(pp, "father");
		pp = dao.fetch(Person.class, "ycs");
		dao.fetchOne(pp, "father");
		assertNull(pp.getFather());
	}

	public void testClearMany() throws Exception {
		Person pp = dao.fetch(Person.class, "ycs");
		dao.fetchMany(pp, "children");
		assertEquals(2, pp.getChildren().size());
		dao.clearMany(pp, "children");
		pp = dao.fetch(Person.class, "ycs");
		dao.fetchMany(pp, "children");
		assertEquals(0, pp.getChildren().size());
	}

}
