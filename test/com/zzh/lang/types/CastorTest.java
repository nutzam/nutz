package com.zzh.lang.types;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import com.zzh.lang.types.Castors;
import com.zzh.lang.types.FailToCastObjectException;

import junit.framework.TestCase;

public class CastorTest extends TestCase {

	public void testCalendar() throws FailToCastObjectException {
		Calendar c = Calendar.getInstance();
		c.set(2008, 5, 20, 5, 46, 26);
		Castors castor = Castors.me();
		String s = castor.castToString(c, Calendar.class);
		assertEquals("2008-06-20 05:46:26", s);
	}

	public void testCalendarParse() throws FailToCastObjectException {
		Calendar c = Calendar.getInstance();
		c.set(2008, 5, 20, 5, 46, 26);

		Calendar c2 = (Calendar) Castors.me().cast("2008-06-20 05:46:26", String.class,
				Calendar.class);

		assertEquals(c.getTimeInMillis() / 1000, c2.getTimeInMillis() / 1000);
	}

	public void testInteger2int() throws FailToCastObjectException {
		int x = 23;
		int n = Castors.me().cast(new Integer(x), Integer.class, int.class);
		assertEquals(x, n);

	}

	public void testLong2int() throws FailToCastObjectException {
		Long l = new Long(59);
		int x = Castors.me().cast(l, Long.class, int.class);
		assertEquals(59, x);
	}

	public void testLong2Float() throws FailToCastObjectException {
		Long l = new Long(59);
		float x = Castors.me().cast(l, Long.class, float.class);
		assertEquals(59.0f, x);
	}

	public void testBoolean2Int() throws FailToCastObjectException {
		assertTrue(1 == Castors.me().cast(true, boolean.class, int.class));
		assertFalse(2 == Castors.me().cast(true, boolean.class, int.class));
		assertFalse(0 == Castors.me().cast(true, boolean.class, int.class));
		
		assertTrue(0 == Castors.me().cast(false, boolean.class, int.class));
		assertFalse(1 == Castors.me().cast(false, boolean.class, int.class));
	}
	

	public void testString2Long() throws FailToCastObjectException {
		long l = Castors.me().castTo("34", long.class);
		assertEquals(34L, l);
		assertEquals(new Long(89L), Castors.me().castTo("89", Long.class));
	}

	public void testLong2String() throws FailToCastObjectException {
		String s = Castors.me().castTo(34L, String.class);
		assertEquals("34", s);
		s = Castors.me().castTo(new Long(89L), String.class);
		assertEquals("89", s);
	}

	public void testString2Float() throws FailToCastObjectException {
		assertEquals(34.67f, Castors.me().castTo("34.67", float.class));
		assertEquals(new Float(34.67), Castors.me().castTo("34.67", Float.class));
	}

	public void testFloat2String() throws FailToCastObjectException {
		String s = Castors.me().castTo(4.978f, String.class);
		assertEquals("4.978", s);
		s = Castors.me().castTo(new Float(4.978f), String.class);
		assertEquals("4.978", s);
	}

	@SuppressWarnings("deprecation")
	public void testString2JavaDate() throws FailToCastObjectException {
		java.util.Date date = Castors.me().cast("2008-6-12 15:28:35", String.class,
				java.util.Date.class);
		assertEquals(108, date.getYear());
		assertEquals(5, date.getMonth());
		assertEquals(12, date.getDate());
		assertEquals(15, date.getHours());
		assertEquals(28, date.getMinutes());
		assertEquals(35, date.getSeconds());
	}

	@SuppressWarnings("deprecation")
	public void testString2Date() throws FailToCastObjectException {
		java.sql.Date date = Castors.me().cast("1977-9-21", String.class, java.sql.Date.class);
		assertEquals(77, date.getYear());
		assertEquals(8, date.getMonth());
		assertEquals(21, date.getDate());
	}

	@SuppressWarnings("deprecation")
	public void testString2Time() throws FailToCastObjectException {
		java.sql.Time date = Castors.me().cast("15:17:23", String.class, java.sql.Time.class);
		assertEquals(15, date.getHours());
		assertEquals(17, date.getMinutes());
		assertEquals(23, date.getSeconds());
	}

	@SuppressWarnings("unchecked")
	public void testStringArray2List() throws Exception {
		String[] inAry = { "e1", "e2" };
		List list = Castors.me().castTo(inAry, List.class);
		assertEquals(2, list.size());
		assertEquals("e1", list.get(0).toString());
		assertEquals("e2", list.get(1).toString());
	}

	@SuppressWarnings("unchecked")
	public void testIntArray2List() throws Exception {
		int[] inAry = { 34, 78 };
		List list = Castors.me().castTo(inAry, List.class);
		assertEquals(2, list.size());
		assertEquals(34, list.get(0));
		assertEquals(78, list.get(1));
	}

	@SuppressWarnings("unchecked")
	public void testStringList2Arry() throws Exception {
		String[] inAry = { "e1", "e2" };
		List list = Castors.me().cast(inAry, String[].class, List.class);
		String[] reAry = Castors.me().castTo(list, String[].class);
		assertTrue(Arrays.equals(inAry, reAry));
	}

	@SuppressWarnings("unchecked")
	public void testIntList2Array() throws Exception {
		int[] inAry = { 34, 78 };
		List list = Castors.me().castTo(inAry, List.class);
		int[] reAry = Castors.me().castTo(list, int[].class);
		assertTrue(Arrays.equals(inAry, reAry));
	}
}
