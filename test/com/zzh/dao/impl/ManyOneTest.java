package com.zzh.dao.impl;

import java.util.HashMap;

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
		Main.prepareTables(dao);
	}

	@Override
	protected void tearDown() throws Exception {
		Main.closeDataSource();
	}
	
	public void testClearManyMany() {
		Host h = Host.make("A");
		h.mainAddress = Address.make("100.100.100.1", "For LAN Game");
		h.mainPort = Port.make(80);
		h.adrs = new Address[2];
		h.adrs[0] = Address.make("192.168.0.1", "local-connection");
		h.adrs[1] = Address.make("222.169.58.7", "global-connection");
		h.ports = new Port[2];
		h.ports[0] = Port.make(8080);
		h.ports[1] = Port.make(9789);
		dao.insert(h);
		dao.insertManyMany(h, "mainAddress", "mainPort", "adrs", "ports");
		assertEquals(3, dao.count(Address.class));
		assertEquals(3, dao.count(Port.class));
		dao.clearManyMany(h, "mainAddress", "mainPort", "adrs", "ports");
		assertEquals(3, dao.count(Address.class));
		assertEquals(3, dao.count(Port.class));

		h.adrs = null;
		h.ports = null;
		h.mainAddress = null;
		h.mainPort = null;
		
		dao.fetchManyMany(h, "mainAddress", "mainPort", "adrs", "ports");
		assertEquals(0,h.ports.length);
		assertEquals(0,h.adrs.length);
		assertNull(h.mainAddress);
		assertNull(h.mainPort);
	}

	public void testInsertManyFailed() {
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

	public void testInsertManyAllFailed() {
		final Host h = Host.make("A");
		h.others = Lang.array(Other.make("T1"), Other.make("T21234567"));
		try {
			Trans.exec(new Atom() {
				public void run(){
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

	public void testInsertMany() {
		Host h = Host.make("A");
		h.others = Lang.array(Other.make("T1"), Other.make("T2"));
		dao.insert(h);
		dao.insertMany(h, "others");
		assertEquals(1, dao.count(Host.class));
		assertEquals(2, dao.count(Other.class));
		assertEquals(h.id, dao.fetch(Other.class, "T1").hostId);
	}

	public void testInsertManyAsSingle() {
		Host h = Host.make("A");
		h.oneOther = Other.make("OO");
		dao.insert(h);
		dao.insertMany(h, "oneOther");
		assertEquals(h.id, h.oneOther.hostId);
		assertEquals(1, dao.count(Other.class));
	}

	public void testClearMany() {
		Host h = Host.make("A");
		h.oneOther = Other.make("OO");
		h.others = Lang.array(Other.make("T1"), Other.make("T2"));
		dao.insert(h);
		dao.insertMany(h, "oneOther", "others");
		assertEquals(3, dao.count(Other.class));
		dao.clearMany(h, "oneOther", "others");
		assertEquals(0, dao.count(Other.class));
	}

	public void testClearManyFailed() {
		Host h = Host.make("A");
		h.oneOther = Other.make("OO");
		h.others = Lang.array(Other.make("T1"), Other.make("T2"));
		dao.insert(h);
		dao.insertMany(h, "oneOther", "others");
		assertEquals(3, dao.count(Other.class));
		try {
			dao.clearMany(h, "doneOther", "others");
			fail();
		} catch (Exception e) {
			assertEquals(3, dao.count(Other.class));
		}
	}

	public void testInsertOne() {
		Host h = Host.make("A");
		h.type = HostType.make(23, "FF");
		dao.insertOne(h, "type");
		dao.insert(h);
		assertEquals(23, h.typeId);
	}

	public void testDeleteOne() {
		Host h = Host.make("A");
		h.type = HostType.make(23, "FF");
		dao.insertOne(h, "type");
		dao.insert(h);
		assertEquals(1, dao.count(HostType.class));
		dao.deleteOne(h, "type");
		assertEquals(0, dao.count(HostType.class));
	}

	public void testInsertManyManyByName() {
		Host h = Host.make("A");
		h.adrs = new Address[2];
		h.adrs[0] = Address.make("192.168.0.1", "local-connection");
		h.adrs[1] = Address.make("222.169.58.7", "global-connection");
		dao.insert(h);
		dao.insertManyMany(h, "adrs");
		h = null;
		h = dao.fetch(Host.class, "A");
		dao.fetchManyMany(h, "adrs");
		assertEquals("192.168.0.1", h.adrs[0].ip);
		assertEquals("local-connection", h.adrs[0].comment);
		assertEquals("222.169.58.7", h.adrs[1].ip);
		assertEquals("global-connection", h.adrs[1].comment);
	}

	public void testInsertManyManyById() {
		Host h = Host.make("A");
		h.ports = new Port[2];
		h.ports[0] = Port.make(8080);
		h.ports[1] = Port.make(9789);
		dao.insert(h);
		dao.insertManyMany(h, "ports");
		h = null;
		h = dao.fetch(Host.class, "A");
		dao.fetchManyMany(h, "ports");
		assertEquals(1, h.ports[0].id);
		assertEquals(8080, h.ports[0].value);
		assertEquals(2, h.ports[1].id);
		assertEquals(9789, h.ports[1].value);
	}

	public void testInsertManyManyByNameAsSingle() {
		Host h = Host.make("A");
		h.mainAddress = Address.make("100.100.100.1", "For LAN Game");
		dao.insert(h);
		dao.insertManyMany(h, "mainAddress");
		h = null;
		h = dao.fetch(Host.class, "A");
		dao.fetchManyMany(h, "mainAddress");
		assertEquals("100.100.100.1", h.mainAddress.ip);
		assertEquals("For LAN Game", h.mainAddress.comment);
	}

	public void testInsertManyManyByIdAsSingle() {
		Host h = Host.make("A");
		h.mainPort = Port.make(80);
		dao.insert(h);
		dao.insertManyMany(h, "mainPort");
		h = null;
		h = dao.fetch(Host.class, "A");
		dao.fetchManyMany(h, "mainPort");
		assertEquals(1, h.mainPort.id);
		assertEquals(80, h.mainPort.value);
	}

	public void testClearManyManyFailed() {
		Host h = Host.make("A");
		h.mainAddress = Address.make("100.100.100.1", "For LAN Game");
		h.mainPort = Port.make(80);
		h.adrs = new Address[2];
		h.adrs[0] = Address.make("192.168.0.1", "local-connection");
		h.adrs[1] = Address.make("222.169.58.7", "global-connection");
		h.ports = new Port[2];
		h.ports[0] = Port.make(8080);
		h.ports[1] = Port.make(9789);
		dao.insert(h);
		dao.insertManyMany(h, "mainAddress", "mainPort", "adrs", "ports");
		assertEquals(3, dao.count(Address.class));
		assertEquals(3, dao.count(Port.class));
		try {
			dao.clearManyMany(h, "mainAddress", "mainPortd", "adrs", "ports");
			fail();
		} catch (Exception e) {
			assertEquals(3, dao.count(Address.class));
			assertEquals(3, dao.count(Port.class));
		}
	}

	public void testInsertManyByMap() {
		Host h = Host.make("A");
		h.mapOther = new HashMap<String, Other>();
		h.mapOther.put("O1", Other.make("O1"));
		h.mapOther.put("O2", Other.make("O2"));
		dao.insert(h);
		dao.insertMany(h, "mapOther");
		h = null;
		assertEquals(1, dao.count(Host.class));
		assertEquals(2, dao.count(Other.class));
		h = dao.fetch(Host.class, "A");
		dao.fetchMany(h, "mapOther");
		assertTrue(h.mapOther.containsKey("O1"));
		assertTrue(h.mapOther.containsKey("O2"));
		h = null;
		h = dao.fetch(Host.class, "A");
		dao.clearMany(h, "mapOther");
		assertEquals(0, dao.count(Other.class));
	}

	public void testInsertManyManyByMap() {
		Host h = Host.make("A");
		h.mapAddress = new HashMap<String, Address>();
		Address adr = Address.make("192.168.0.1", "local-connection");
		h.mapAddress.put(adr.ip, adr);
		adr = Address.make("222.169.58.7", "global-connection");
		h.mapAddress.put(adr.ip, adr);
		dao.insert(h);
		dao.insertManyMany(h, "mapAddress");
		h = null;
		assertEquals(1, dao.count(Host.class));
		assertEquals(2, dao.count(Address.class));
		h = dao.fetch(Host.class, "A");
		dao.fetchManyMany(h, "mapAddress");
		assertEquals("local-connection", h.mapAddress.get("192.168.0.1").comment);
		assertEquals("global-connection", h.mapAddress.get("222.169.58.7").comment);
	}
}
