package org.nutz.dao.test.mapping;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.nutz.dao.test.DaoCase;
import org.nutz.dao.test.meta.Base;

public class LinksGeneral extends DaoCase {

	@Override
	protected void before() {
		pojos.initData();
	}

	@Test
	public void fetch_all_links() {
		Base b = dao.fetchLinks(dao.fetch(Base.class, "red"), null);
		assertEquals("red", b.getName());
		assertEquals("China", b.getCountry().getName());
		assertEquals(6, b.getFighters().size());
		assertEquals(3, b.getPlatoons().size());
	}

}
