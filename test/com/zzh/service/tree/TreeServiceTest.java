package com.zzh.service.tree;

import java.util.List;

import com.zzh.Main;
import com.zzh.dao.Dao;
import com.zzh.dao.impl.Person;

import junit.framework.TestCase;

public class TreeServiceTest extends TestCase {

	private NameTreeService<Person> tree;

	@Override
	protected void setUp() throws Exception {
		Dao dao = Main.getDao("com/zzh/dao/impl/personTest.sqls");
		tree = new NameTreeService<Person>(dao) {
		};
		tree.setChildrenField("children");
		tree.setParentField("father");
		Person.prepareTable(dao);
	}

	@Override
	protected void tearDown() throws Exception {
		Main.closeDataSource();
	}

	public void testFetchAll() {
		Person zyy = tree.fetchWithDescendants("zyy");
		Person yy = zyy.getChildren().get(0);
		Person pp = yy.getChildren().get(0);
		Person me = pp.getChildren().get(0);
		Person brother = pp.getChildren().get(1);
		assertEquals("yy", yy.getName());
		assertEquals("ycs", pp.getName());
		assertEquals("zzh", me.getName());
		assertEquals("ydl", brother.getName());
	}

	public void testFetchChildren() {
		Person pp = tree.fetchWithChildren("ycs");
		tree.fetchParent(pp);
		Person me = pp.getChildren().get(0);
		Person brother = pp.getChildren().get(1);
		Person yy = pp.getFather();
		assertEquals("yy", yy.getName());
		assertEquals("ycs", pp.getName());
		assertEquals("zzh", me.getName());
		assertEquals("ydl", brother.getName());
	}

	public void testFetchAncestors() {
		Person me = tree.fetchWithAncestors("zzh");
		Person pp = me.getFather();
		Person yy = pp.getFather();
		Person zyy = yy.getFather();
		assertEquals("zyy", zyy.getName());
		assertEquals("yy", yy.getName());
		assertEquals("ycs", pp.getName());
		assertEquals("zzh", me.getName());
	}

	public void testGetAncestors() {
		List<Person> ans = tree.getAncestors("zzh");
		assertEquals(3, ans.size());
		Person zyy = ans.get(0);
		Person yy = ans.get(1);
		Person pp = ans.get(2);
		assertEquals("zyy", zyy.getName());
		assertEquals("yy", yy.getName());
		assertEquals("ycs", pp.getName());
	}

	public void testClearDescendants() {
		tree.clearDescendants("zyy");
		Person zyy = tree.fetchWithDescendants("zyy");
		assertEquals("zyy", zyy.getName());
		assertEquals(0, zyy.getChildren().size());
	}

}
