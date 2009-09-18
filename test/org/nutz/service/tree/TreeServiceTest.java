package org.nutz.service.tree;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import org.nutz.service.pojo.Person;
import org.nutz.dao.Sqls;
import org.nutz.dao.test.DaoCase;
import org.nutz.lang.Lang;

public class TreeServiceTest extends DaoCase {

	private NameTreeService<Person> tree;

	@Override
	protected void before() {
		Sqls.executeFile(dao, "org/nutz/service/pojo/person.dod");
		tree = new NameTreeService<Person>(dao) {};
		try {
			tree.setChildrenField("children");
			tree.setParentField("father");
		} catch (NoSuchFieldException e) {
			throw Lang.wrapThrow(e);
		}
		Person.prepareTable(dao);
	}

	@Override
	protected void after() {}

	@Test
	public void testFetchWithDescendants() {
		Person zyy = tree.fetchDescendants(tree.fetch("zyy"));
		Person yy = zyy.getChildren().get(0);
		Person pp = yy.getChildren().get(0);
		Person me = pp.getChildren().get(0);
		Person brother = pp.getChildren().get(1);
		assertEquals("yy", yy.getName());
		assertEquals("ycs", pp.getName());
		assertEquals("zzh", me.getName());
		assertEquals("ydl", brother.getName());

		assertNull(yy.getFather());
		assertNull(pp.getFather());
		assertNull(me.getFather());
		assertNull(brother.getFather());
	}

	@Test
	public void testFetchAll() {
		Person zyy = tree.fetchAll(tree.fetch("zyy"));
		Person yy = zyy.getChildren().get(0);
		Person pp = yy.getChildren().get(0);
		Person me = pp.getChildren().get(0);
		Person brother = pp.getChildren().get(1);
		assertEquals("yy", yy.getName());
		assertEquals("ycs", pp.getName());
		assertEquals("zzh", me.getName());
		assertEquals("ydl", brother.getName());

		assertEquals(zyy, yy.getFather());
		assertEquals(yy, pp.getFather());
		assertEquals(pp, me.getFather());
		assertEquals(pp, brother.getFather());
	}

	@Test
	public void testFetchChildren() {
		Person pp = tree.fetchChildren(tree.fetch("ycs"));
		tree.fetchParent(pp);
		Person me = pp.getChildren().get(0);
		Person brother = pp.getChildren().get(1);
		Person yy = pp.getFather();
		assertEquals("yy", yy.getName());
		assertEquals("ycs", pp.getName());
		assertEquals("zzh", me.getName());
		assertEquals("ydl", brother.getName());
		assertEquals(pp, me.getFather());
		assertEquals(pp, brother.getFather());
	}

	@Test
	public void testFetchAncestors() {
		Person me = tree.fetchAncestors(tree.fetch("zzh"));
		Person pp = me.getFather();
		Person yy = pp.getFather();
		Person zyy = yy.getFather();
		assertEquals("zyy", zyy.getName());
		assertEquals("yy", yy.getName());
		assertEquals("ycs", pp.getName());
		assertEquals("zzh", me.getName());
	}

	@Test
	public void testGetAncestors() {
		List<Person> ans = tree.getAncestors(tree.fetch("zzh"));
		assertEquals(3, ans.size());
		Person zyy = ans.get(0);
		Person yy = ans.get(1);
		Person pp = ans.get(2);
		assertEquals("zyy", zyy.getName());
		assertEquals("yy", yy.getName());
		assertEquals("ycs", pp.getName());
	}

	@Test
	public void testClearDescendants() {
		tree.clearDescendants(tree.fetch("zyy"));
		Person zyy = tree.fetchDescendants(tree.fetch("zyy"));
		assertEquals("zyy", zyy.getName());
		assertEquals(0, zyy.getChildren().size());
	}

	@Test
	public void testInsertChildren() {
		Person john = tree.fetch("John");
		john.setChildren(new ArrayList<Person>(2));
		john.getChildren().add(new Person("Mick"));
		john.getChildren().add(new Person("Tony"));
		tree.insertChildren(john);
		john = tree.fetchAll(tree.fetch("John"));
		assertEquals("John", john.getName());
		assertEquals(2, john.getChildren().size());
		assertEquals("Mick", john.getChildren().get(0).getName());
		assertEquals(john, john.getChildren().get(0).getFather());
		assertEquals("Tony", john.getChildren().get(1).getName());
		assertEquals(john, john.getChildren().get(1).getFather());
	}

	@Test
	public void testInsertDescendants() {
		Person john = tree.fetch("John");
		john.setChildren(new ArrayList<Person>(2));

		Person mick = new Person("Mick");
		mick.setChildren(new ArrayList<Person>(2));

		Person tony = new Person("Tony");
		tony.setChildren(new ArrayList<Person>(2));

		Person m1 = new Person("m1");
		Person m2 = new Person("m2");
		Person t1 = new Person("t1");
		Person t2 = new Person("t2");

		john.getChildren().add(mick);
		john.getChildren().add(tony);

		mick.getChildren().add(m1);
		mick.getChildren().add(m2);

		tony.getChildren().add(t1);
		tony.getChildren().add(t2);

		tree.insertDescendants(john);
		john = mick = tony = m1 = m2 = t1 = t2 = null;

		john = tree.fetchAll(tree.fetch("John"));

		assertEquals("John", john.getName());
		assertEquals(2, john.getChildren().size());

		Person mick2 = john.getChildren().get(0);
		assertEquals("Mick", mick2.getName());
		assertEquals(john, mick2.getFather());
		assertEquals(2, mick2.getChildren().size());
		assertEquals("m1", mick2.getChildren().get(0).getName());
		assertEquals("m2", mick2.getChildren().get(1).getName());
		assertEquals(mick2, mick2.getChildren().get(0).getFather());
		assertEquals(mick2, mick2.getChildren().get(1).getFather());

		Person tony2 = john.getChildren().get(1);
		assertEquals("Tony", tony2.getName());
		assertEquals(john, tony2.getFather());
		assertEquals(2, tony2.getChildren().size());
		assertEquals("t1", tony2.getChildren().get(0).getName());
		assertEquals("t2", tony2.getChildren().get(1).getName());
		assertEquals(tony2, tony2.getChildren().get(0).getFather());
		assertEquals(tony2, tony2.getChildren().get(1).getFather());

	}
}
