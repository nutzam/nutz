package org.nutz.dao.test.mapping;

import org.junit.Test;

import org.nutz.dao.test.DaoCase;
import org.nutz.dao.test.meta.Base;
import org.nutz.dao.test.meta.Country;

import static org.junit.Assert.*;

public class One extends DaoCase {

	@Override
	protected void before() {
		pojos.initData();
	}

	@Override
	protected void after() {}

	@Test
	public void fetch_links() {
		Base b = dao.fetchLinks(dao.fetch(Base.class, "red"), "country");
		assertEquals("China", b.getCountry().getName());
	}

	@Test
	public void delete_links() {
		Base b = dao.fetchLinks(dao.fetch(Base.class, "red"), "country");
		dao.deleteLinks(b, "country");
		assertEquals(1, dao.count(Country.class));
	}

	@Test
	public void delete_with() {
		Base b = dao.fetchLinks(dao.fetch(Base.class, "red"), "country");
		dao.deleteWith(b, "country");
		assertEquals(1, dao.count(Country.class));
		assertEquals(1, dao.count(Base.class));
	}

	@Test
	public void clear_links() {
		Base b = dao.fetch(Base.class, "red");
		dao.clearLinks(b, "country");
		assertEquals(1, dao.count(Country.class));
	}

	@Test
	public void update_links() {
		Base b = dao.fetchLinks(dao.fetch(Base.class, "red"), "country");
		int lv = b.getLevel();
		b.setLevel(45);
		b.getCountry().setName("ABC");
		dao.updateLinks(b, "country");
		b = dao.fetch(Base.class, "red");
		assertEquals(lv, b.getLevel());
		Country c = dao.fetch(Country.class, b.getCountryId());
		assertEquals("ABC", c.getName());
	}

	@Test
	public void update_with() {
		Base b = dao.fetchLinks(dao.fetch(Base.class, "red"), "country");
		b.setLevel(6);
		b.getCountry().setName("ABC");
		dao.updateWith(b, "country");
		b = dao.fetch(Base.class, b.getName());
		assertEquals(6, b.getLevel());
		Country c = dao.fetch(Country.class, b.getCountryId());
		assertEquals("ABC", c.getName());
	}

}
