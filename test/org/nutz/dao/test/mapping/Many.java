package org.nutz.dao.test.mapping;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;

import org.junit.Test;

import org.nutz.dao.test.DaoCase;
import org.nutz.dao.test.meta.Base;
import org.nutz.dao.test.meta.Platoon;
import org.nutz.dao.test.meta.WaveBand;

public class Many extends DaoCase {

	@Override
	protected void before() {
		pojos.initData();
	}

	@Override
	protected void after() {}

	@Test
	public void fetch_links() {
		Base b = dao.fetchLinks(dao.fetch(Base.class, "red"), "platoons");
		assertEquals(3, b.getPlatoons().size());
		assertEquals(b.getName(), b.getPlatoons().get("C").getBaseName());
		assertEquals(b.getName(), b.getPlatoons().get("ES").getBaseName());
		assertEquals(b.getName(), b.getPlatoons().get("DT").getBaseName());
	}

	@Test
	public void fetch_null_field_links() {
		Base b = dao.fetchLinks(dao.fetch(Base.class, "red"), "wavebands");
		assertEquals(4, b.getWavebands().size());
		Base b2 = dao.fetchLinks(dao.fetch(Base.class, "blue"), "wavebands");
		assertEquals(4, b2.getWavebands().size());
	}

	@Test
	public void delete_links() {
		assertEquals(6, dao.count(Platoon.class));
		Base b = dao.fetchLinks(dao.fetch(Base.class, "red"), "platoons");
		dao.deleteLinks(b, "platoons");
		assertEquals(3, dao.count(Platoon.class));
	}

	@Test
	public void delete_null_field_links() {
		Base b = dao.fetchLinks(dao.fetch(Base.class, "red"), "wavebands");
		dao.deleteLinks(b, "wavebands");
		assertEquals(0, dao.count(WaveBand.class));
	}

	@Test
	public void delete_links_partly() {
		Base b = dao.fetchLinks(dao.fetch(Base.class, "red"), "platoons");
		b.getPlatoons().remove("C");
		dao.deleteLinks(b, "platoons");
		assertEquals(4, dao.count(Platoon.class));
	}

	@Test
	public void delete_null_field_links_partly() {
		Base b = dao.fetchLinks(dao.fetch(Base.class, "red"), "wavebands");
		b.getWavebands().remove(0);
		dao.deleteLinks(b, "wavebands");
		assertEquals(1, dao.count(WaveBand.class));
	}

	@Test
	public void delete_with() {
		Base b = dao.fetchLinks(dao.fetch(Base.class, "red"), "platoons");
		dao.deleteWith(b, "platoons");
		assertEquals(3, dao.count(Platoon.class));
		assertEquals(1, dao.count(Base.class));
	}

	@Test
	public void delete_with_partly() {
		Base b = dao.fetchLinks(dao.fetch(Base.class, "red"), "platoons");
		b.getPlatoons().remove("C");
		dao.deleteWith(b, "platoons");
		assertEquals(4, dao.count(Platoon.class));
		assertEquals(1, dao.count(Base.class));
	}

	@Test
	public void clear_links() {
		Base b = dao.fetch(Base.class, "red");
		dao.clearLinks(b, "platoons");
		assertEquals(3, dao.count(Platoon.class));
	}

	@Test
	public void clear_null_field_links() {
		Base b = dao.fetch(Base.class, "red");
		dao.clearLinks(b, "wavebands");
		assertEquals(0, dao.count(WaveBand.class));
	}

	@Test
	public void update_links() {
		Base b = dao.fetchLinks(dao.fetch(Base.class, "blue"), "platoons");
		int lv = b.getLevel();
		b.setLevel(45);
		for (Iterator<Platoon> it = b.getPlatoons().values().iterator(); it.hasNext();) {
			it.next().setBaseName("red");
		}
		dao.updateLinks(b, "platoons");
		b = dao.fetch(Base.class, "blue");
		assertEquals(lv, b.getLevel());
		b = dao.fetchLinks(dao.fetch(Base.class, "red"), "platoons");
		assertEquals(6, b.getPlatoons().size());
	}

	@Test
	public void update_with() {
		Base b = dao.fetchLinks(dao.fetch(Base.class, "blue"), "platoons");
		b.setLevel(45);
		for (Iterator<Platoon> it = b.getPlatoons().values().iterator(); it.hasNext();) {
			it.next().setBaseName("red");
		}
		dao.updateWith(b, "platoons");
		b = dao.fetch(Base.class, "blue");
		assertEquals(45, b.getLevel());
		b = dao.fetchLinks(dao.fetch(Base.class, "red"), "platoons");
		assertEquals(6, b.getPlatoons().size());
	}
}
