package org.nutz.dao.test.mapping;

import static org.junit.Assert.*;

import org.junit.Test;
import org.nutz.dao.test.DaoCase;
import org.nutz.dao.test.meta.Base;
import org.nutz.dao.test.meta.Country;
import org.nutz.lang.Strings;

public class LinksGeneral extends DaoCase {

	@Test
	public void fetch_all_links() {
		pojos.initData();
		Base b = dao.fetchLinks(dao.fetch(Base.class, "red"), null);
		assertEquals("red", b.getName());
		assertEquals("China", b.getCountry().getName());
		assertEquals(6, b.getFighters().size());
		assertEquals(3, b.getPlatoons().size());
	}

	@Test
	public void insert_links_with_exception() {
		try {
			pojos.init();
			Base b = Base.make("Red");
			dao.insert(b);
			b.setCountry(Country.make(Strings.dup('C', 52)));
			dao.insertLinks(b, "country");
			fail();
		}
		catch (Exception e) {
			assertTrue(true);
		}
	}

}
