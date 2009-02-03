package com.zzh.ioc;

import com.zzh.castor.Castors;
import com.zzh.ioc.json.JsonAssemble;
import com.zzh.lang.Files;

import junit.framework.TestCase;

public class JsonNutTest extends TestCase {

	private Nut nut;

	@Override
	protected void setUp() throws Exception {
		nut = new Nut(new JsonAssemble(Files.findFile("com/zzh/ioc")), Castors.me());
	}

	public void testSimpleOne() {
		Guy ycs = nut.getObject(Guy.class, "ycs");
		assertEquals("YouChunSheng", ycs.name);
		assertEquals("youoo", ycs.emails[0].getAccount());
		assertEquals(.70f, ycs.health);
		assertNull(ycs.father);
	}

	public void testSimpleCombin() {
		Guy ycs = nut.getObject(Guy.class, "ycs");
		Guy zzh = nut.getObject(Guy.class, "zzh");
		assertTrue(ycs == zzh.father);
		assertEquals("gmail.com", zzh.emails[1].getHost());
		Guy zzh2 = nut.getObject(Guy.class, "zzh");
		assertFalse(zzh == zzh2);
		assertTrue(ycs == zzh2.father);
	}
}
