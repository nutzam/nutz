package org.nutz.castor;

import static org.junit.Assert.*;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.junit.Test;

import org.nutz.NutzEnum;
import org.nutz.castor.Castors;
import org.nutz.castor.FailToCastObjectException;
import org.nutz.lang.Lang;
import org.nutz.lang.meta.Email;

public class CastorTest {

	@Test
	public void testCalendar() throws FailToCastObjectException {
		Calendar c = Calendar.getInstance();
		c.set(2008, 5, 20, 5, 46, 26);
		Castors castor = Castors.me();
		String s = castor.castToString(c);
		assertEquals("2008-06-20 05:46:26", s);
	}

	@Test
	public void testCalendarParse() throws FailToCastObjectException {
		Calendar c = Calendar.getInstance();
		c.set(2008, 5, 20, 5, 46, 26);

		Calendar c2 = (Calendar) Castors.me().cast("2008-06-20 05:46:26", String.class,
				Calendar.class);

		assertEquals(c.getTimeInMillis() / 1000, c2.getTimeInMillis() / 1000);
	}

	@Test
	public void testInteger2int() throws FailToCastObjectException {
		int x = 23;
		int n = Castors.me().cast(new Integer(x), Integer.class, int.class);
		assertEquals(x, n);

	}

	@Test
	public void testInt2Boolean() throws FailToCastObjectException {
		assertTrue(Castors.me().cast(3, int.class, boolean.class));
		assertFalse(Castors.me().cast(0, int.class, boolean.class));
	}

	@Test
	public void testFloat2Boolean() throws FailToCastObjectException {
		assertTrue(Castors.me().cast(3.674f, float.class, boolean.class));
		assertFalse(Castors.me().cast(0.45f, float.class, boolean.class));
		assertFalse(Castors.me().cast(0.0f, float.class, boolean.class));
	}

	@Test
	public void testLong2int() throws FailToCastObjectException {
		Long l = new Long(59);
		int x = Castors.me().cast(l, Long.class, int.class);
		assertEquals(59, x);
	}

	@Test
	public void testLong2Float() throws FailToCastObjectException {
		Long l = new Long(59);
		Float x = Castors.me().cast(l, Long.class, float.class);
		assertEquals(Float.valueOf(59.0f), x);
	}

	@Test
	public void testBoolean2Int() throws FailToCastObjectException {
		assertTrue(1 == Castors.me().cast(true, boolean.class, int.class));
		assertFalse(2 == Castors.me().cast(true, boolean.class, int.class));
		assertFalse(0 == Castors.me().cast(true, boolean.class, int.class));

		assertTrue(0 == Castors.me().cast(false, boolean.class, int.class));
		assertFalse(1 == Castors.me().cast(false, boolean.class, int.class));
	}

	@Test
	public void testString2Long() throws FailToCastObjectException {
		long l = Castors.me().castTo("34", long.class);
		assertEquals(34L, l);
		assertEquals(new Long(89L), Castors.me().castTo("89", Long.class));
	}

	@Test
	public void testLong2String() throws FailToCastObjectException {
		String s = Castors.me().castTo(34L, String.class);
		assertEquals("34", s);
		s = Castors.me().castTo(new Long(89L), String.class);
		assertEquals("89", s);
	}

	@Test
	public void testString2bool() throws FailToCastObjectException {
		assertTrue(Castors.me().castTo("true", boolean.class));
		assertTrue(Castors.me().castTo(" ", boolean.class));
		assertTrue(Castors.me().castTo("abc", boolean.class));
		assertTrue(Castors.me().castTo("1", boolean.class));
		assertTrue(Castors.me().castTo("-1", boolean.class));
		assertTrue(Castors.me().castTo("15", boolean.class));

		assertFalse(Castors.me().castTo("0", boolean.class));
		assertFalse(Castors.me().castTo("", boolean.class));
		assertFalse(Castors.me().castTo("oFf", boolean.class));
		assertFalse(Castors.me().castTo("No", boolean.class));
		assertFalse(Castors.me().castTo("faLsE", boolean.class));
	}

	@Test
	public void testString2int() throws FailToCastObjectException {
		assertEquals(45, (int) Castors.me().castTo("45", int.class));
	}

	public void testString2Email() throws FailToCastObjectException {
		Email em = new Email("zozoh@263.net");
		assertEquals(em, "zozoh@263.net");
	}

	@Test
	public void testString2Float() throws FailToCastObjectException {
		assertEquals(Float.valueOf(34.67f), Castors.me().castTo("34.67", float.class));
		assertEquals(new Float(34.67), Castors.me().castTo("34.67", Float.class));
	}

	@Test
	public void testFloat2String() throws FailToCastObjectException {
		String s = Castors.me().castTo(4.978f, String.class);
		assertEquals("4.978", s);
		s = Castors.me().castTo(new Float(4.978f), String.class);
		assertEquals("4.978", s);
	}

	@SuppressWarnings("deprecation")
	@Test
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
	@Test
	public void testString2Date() throws FailToCastObjectException {
		java.sql.Date date = Castors.me().cast("1977-9-21", String.class, java.sql.Date.class);
		assertEquals(77, date.getYear());
		assertEquals(8, date.getMonth());
		assertEquals(21, date.getDate());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testString2Time() throws FailToCastObjectException {
		java.sql.Time date = Castors.me().cast("15:17:23", String.class, java.sql.Time.class);
		assertEquals(15, date.getHours());
		assertEquals(17, date.getMinutes());
		assertEquals(23, date.getSeconds());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testStringArray2List() throws Exception {
		String[] inAry = {"e1", "e2"};
		List<String> list = Castors.me().castTo(inAry, List.class);
		assertEquals(2, list.size());
		assertEquals("e1", list.get(0).toString());
		assertEquals("e2", list.get(1).toString());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testIntArray2List() throws Exception {
		int[] inAry = {34, 78};
		List list = Castors.me().castTo(inAry, List.class);
		assertEquals(2, list.size());
		assertEquals(34, list.get(0));
		assertEquals(78, list.get(1));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testStringList2Arry() throws Exception {
		String[] inAry = {"e1", "e2"};
		List list = Castors.me().cast(inAry, String[].class, List.class);
		String[] reAry = Castors.me().castTo(list, String[].class);
		assertTrue(Arrays.equals(inAry, reAry));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testIntList2Array() throws Exception {
		int[] inAry = {34, 78};
		List list = Castors.me().castTo(inAry, List.class);
		int[] reAry = Castors.me().castTo(list, int[].class);
		assertTrue(Arrays.equals(inAry, reAry));
	}

	@Test
	public void testArray2String() throws Exception {
		Email[] mails = {new Email("zzh@263.net"), new Email("zozohtnt@yahoo.com.cn")};
		String done = Castors.me().castToString(mails);
		Email[] mails2 = Castors.me().castTo(done, Email[].class);
		assertTrue(Lang.equals(mails, mails2));
	}

	@Test
	public void testString2Array() throws Exception {
		String orgs = "zzh@263.net,zozohtnt@yahoo.com.cn";
		Email[] exp = {new Email("zzh@263.net"), new Email("zozohtnt@yahoo.com.cn")};
		Email[] done = Castors.me().castTo(orgs, Email[].class);
		assertTrue(Arrays.equals(exp, done));
	}

	@Test
	public void testArray2Array() throws Exception {
		String[] orgs = {"zzh@263.net", "zozoh@163.com"};
		Email[] emails = Castors.me().castTo(orgs, Email[].class);
		assertEquals(2, emails.length);
		assertEquals("zzh", emails[0].getAccount());
		assertEquals("zozoh", emails[1].getAccount());
	}

	@Test
	public void testEnum() throws Exception {
		assertEquals("Dao", Castors.me().castToString(NutzEnum.Dao));
		assertEquals(NutzEnum.Lang, Castors.me().castTo("Lang", NutzEnum.class));
	}

	static class FFF {
		String ID;
	}

	@Test
	public void testCollection2Object() throws Exception {
		FFF obj = new FFF();
		obj.ID = "XYZ";
		ArrayList<FFF> list = new ArrayList<FFF>(1);
		list.add(obj);
		FFF obj2 = Castors.me().cast(list, ArrayList.class, FFF.class);
		assertEquals(obj, obj2);
	}

	@Test
	public void testString2Char() throws Exception {
		char c = Castors.me().castTo("HH", Character.class);
		assertEquals('H', c);
		char c2 = Castors.me().castTo("HH", char.class);
		assertEquals('H', c2);
	}

	@Test
	public void testCharacter2char() throws Exception {
		Character cc = 'Z';
		char c = Castors.me().castTo(cc, char.class);
		assertEquals('Z', c);
	}

	@Test
	public void testString2File() throws Exception {
		File f = Castors.me().castTo("org/nutz", File.class);
		assertTrue(f.exists());
	}

	@Test
	public void testFile2String() throws Exception {
		File f = Castors.me().castTo("org/nutz", File.class);
		String path = Castors.me().castTo(f, String.class);
		assertEquals(f.getAbsolutePath(), path);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testTimestamp2sqlDate() throws Exception {
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		java.sql.Date date = Castors.me().castTo(ts, java.sql.Date.class);
		assertEquals(ts.getYear(), date.getYear());
		assertEquals(ts.getMonth(), date.getMonth());
		assertEquals(ts.getDate(), date.getDate());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testSqlDate2Timestamp() throws Exception {
		java.sql.Date date = new java.sql.Date(System.currentTimeMillis());
		Timestamp ts = Castors.me().castTo(date, Timestamp.class);
		assertEquals(ts.getYear(), date.getYear());
		assertEquals(ts.getMonth(), date.getMonth());
		assertEquals(ts.getDate(), date.getDate());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testTimestamp2sqlTime() throws Exception {
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		java.sql.Time time = Castors.me().castTo(ts, java.sql.Time.class);
		assertEquals(ts.getHours(), time.getHours());
		assertEquals(ts.getMinutes(), time.getMinutes());
		assertEquals(ts.getSeconds(), time.getSeconds());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testSqlTime2Timestamp() throws Exception {
		java.sql.Time time = new java.sql.Time(System.currentTimeMillis());
		Timestamp ts = Castors.me().castTo(time, Timestamp.class);
		assertEquals(ts.getHours(), time.getHours());
		assertEquals(ts.getMinutes(), time.getMinutes());
		assertEquals(ts.getSeconds(), time.getSeconds());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testDatetime2sqlDate() throws Exception {
		java.util.Date ts = new java.util.Date(System.currentTimeMillis());
		java.sql.Date date = Castors.me().castTo(ts, java.sql.Date.class);
		assertEquals(ts.getYear(), date.getYear());
		assertEquals(ts.getMonth(), date.getMonth());
		assertEquals(ts.getDate(), date.getDate());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testSqlDate2Datetime() throws Exception {
		java.sql.Date date = new java.sql.Date(System.currentTimeMillis());
		java.util.Date ts = Castors.me().castTo(date, java.util.Date.class);
		assertEquals(ts.getYear(), date.getYear());
		assertEquals(ts.getMonth(), date.getMonth());
		assertEquals(ts.getDate(), date.getDate());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testDatetime2sqlTime() throws Exception {
		java.util.Date ts = new java.util.Date(System.currentTimeMillis());
		java.sql.Time time = Castors.me().castTo(ts, java.sql.Time.class);
		assertEquals(ts.getHours(), time.getHours());
		assertEquals(ts.getMinutes(), time.getMinutes());
		assertEquals(ts.getSeconds(), time.getSeconds());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testSqlTime2Datetime() throws Exception {
		java.sql.Time time = new java.sql.Time(System.currentTimeMillis());
		java.util.Date ts = Castors.me().castTo(time, java.util.Date.class);
		assertEquals(ts.getHours(), time.getHours());
		assertEquals(ts.getMinutes(), time.getMinutes());
		assertEquals(ts.getSeconds(), time.getSeconds());
	}

	@Test
	public void test_string_to_primitive() throws Exception {
		assertEquals(long.class, Castors.me().castTo("long", Class.class));
		assertEquals(int.class, Castors.me().castTo("int", Class.class));
		assertEquals(short.class, Castors.me().castTo("short", Class.class));
		assertEquals(byte.class, Castors.me().castTo("byte", Class.class));
		assertEquals(float.class, Castors.me().castTo("float", Class.class));
		assertEquals(double.class, Castors.me().castTo("double", Class.class));
		assertEquals(char.class, Castors.me().castTo("char", Class.class));
		assertEquals(boolean.class, Castors.me().castTo("boolean", Class.class));

	}
}
