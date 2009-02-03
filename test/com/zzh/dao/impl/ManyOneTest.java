package com.zzh.dao.impl;

import com.zzh.Main;
import com.zzh.dao.Dao;
import com.zzh.lang.Lang;
import com.zzh.trans.Atom;
import com.zzh.trans.Trans;

import junit.framework.TestCase;

public class ManyOneTest extends TestCase {

	private Dao dao;

	@Override
	protected void setUp() throws Exception {
		this.dao = Main.getDao("com/zzh/dao/impl/manyone.sqls");
		String[] keys = { ".host.drop", ".host.create", ".other.drop", ".other.create",
				".type.drop", ".type.create" };
		Main.prepareTables(dao,keys);
	}

	@Override
	protected void tearDown() throws Exception {
		Main.closeDataSource();
	}

	public void testInsertSimpleManyFailed() {
		Host h = Host.make("A");
		h.others = Lang.array(Other.make("T1"), Other.make("T21234567"));
		dao.insert(h);
		assertEquals(1, dao.count(Host.class));
		try {
			dao.insertMany(h, "others");
			fail();
		} catch (Exception e) {
			assertEquals(0, dao.count(Other.class));
		}
	}

	public void testInsertSimpleManyAllFailed() {
		final Host h = Host.make("A");
		h.others = Lang.array(Other.make("T1"), Other.make("T21234567"));
		try {
			Trans.exec(new Atom() {
				public void run() throws Exception {
					dao.insert(h);
					dao.insertMany(h, "others");
				}
			});
			fail();
		} catch (Exception e) {
			assertEquals(0, dao.count(Host.class));
			assertEquals(0, dao.count(Other.class));
		}
	}

	public void testInsertSimpleMany() {
		Host h = Host.make("A");
		h.others = Lang.array(Other.make("T1"), Other.make("T2"));
		dao.insert(h);
		dao.insertMany(h, "others");
		assertEquals(1, dao.count(Host.class));
		assertEquals(2, dao.count(Other.class));
		assertEquals(h.id, dao.fetch(Other.class, "T1").hostId);
	}

	public void testInsertOne() {
		Host h = Host.make("A");
		h.type = HostType.make(23, "FF");
		dao.insertOne(h, "type");
		dao.insert(h);
		assertEquals(23, h.typeId);
	}

}
