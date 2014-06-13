package org.nutz.castor;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Assert;

import org.junit.Test;
import org.nutz.NutzEnum;
import org.nutz.castor.castor.Datetime2String;
import org.nutz.castor.castor.String2Array;
import org.nutz.lang.Lang;
import org.nutz.lang.Times;
import org.nutz.lang.meta.Email;

import static org.junit.Assert.*;

public class CastorTest {

    class Dummy {
        int id;
        String msg;
        Date date;

        public Dummy(int id, String msg, Date date) {
            this.id = id;
            this.msg = msg;
            this.date = date;
        }
    }

    @Test
    public void Object2Array() {
        Castors c = Castors.me();

        assertArrayEquals(new int[]{Integer.MIN_VALUE},
                          c.cast(Integer.MIN_VALUE, int.class, int[].class));
        assertArrayEquals(new String[]{"First"},
                          c.cast("First", String.class, String[].class));

        Dummy dummy = new Dummy(123, "abc", new Date());
        assertArrayEquals(new Dummy[]{dummy},
                          c.cast(dummy, Dummy.class, Dummy[].class));

        assertTrue(c.canCast(int.class, int[].class));
        assertTrue(c.canCast(String.class, String[].class));
        assertTrue(c.canCast(Dummy.class, Dummy[].class));

        Castor<String, Object> string2Array = new String2Array();

        assertArrayEquals((String[])string2Array.cast("[\"a\",\"b\",\"c\",123,456]", String[].class),
                          c.cast("[\"a\",\"b\",\"c\",123,456]", String.class, String[].class));
    }

    @Test
    public void test_Double_to_int() {
        Castors cts = Castors.me();
        Double d = new Double(1.0);
        int i = cts.castTo(d, int.class);
        assertEquals(1, i);
    }

    /**
     * 参见 Issue #435
     */
    @Test
    public void test_yyMMdd_to_Timestamp() {
        Castors cts = Castors.me();
        String str = "2013-04-17";
        Timestamp t = cts.castTo(str, Timestamp.class);
        String s0 = cts.castToString(t);
        assertEquals("2013-04-17 00:00:00", s0);
    }

    @Test
    public void test_null_to_byte_and_short() {
        Castors cts = Castors.me();
        Assert.assertEquals((byte) 0, cts.castTo(null, byte.class).byteValue());
        Assert.assertEquals((short) 0, cts.castTo(null, short.class)
                                          .shortValue());
    }

    /**
     * 根据 Issue 272，如果为空串，原生类型的外覆类应该返回 null
     */
    @Test
    public void test_cast_blank_to_Integer() {
        assertNull(Castors.me().castTo("", Integer.class));
        assertEquals(0, (int) Castors.me().castTo("", int.class));
    }

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

        Calendar c2 = Castors.me().cast("2008-06-20 05:46:26",
                                        String.class,
                                        Calendar.class);

        assertEquals(c.getTimeInMillis() / 1000, c2.getTimeInMillis() / 1000);
    }

    @Test
    public void testInteger2int() throws FailToCastObjectException {
        int x = 23;
        int n = Castors.me().cast(Integer.valueOf(x), Integer.class, int.class);
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
        Long l = Long.valueOf(59);
        int x = Castors.me().cast(l, Long.class, int.class);
        assertEquals(59, x);
    }

    @Test
    public void testLong2Float() throws FailToCastObjectException {
        Long l = Long.valueOf(59);
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
        assertEquals(Long.valueOf(89L), Castors.me().castTo("89", Long.class));
    }

    @Test
    public void testLong2String() throws FailToCastObjectException {
        String s = Castors.me().castTo(34L, String.class);
        assertEquals("34", s);
        s = Castors.me().castTo(Long.valueOf(89L), String.class);
        assertEquals("89", s);
    }

    @Test
    public void testString2bool() throws FailToCastObjectException {
        assertTrue(Castors.me().castTo("true", boolean.class));
        assertTrue(Castors.me().castTo("abc", boolean.class));
        assertTrue(Castors.me().castTo("1", boolean.class));
        assertTrue(Castors.me().castTo("-1", boolean.class));
        assertTrue(Castors.me().castTo("15", boolean.class));

        assertFalse(Castors.me().castTo("0", boolean.class));
        assertFalse(Castors.me().castTo("", boolean.class));
        assertFalse(Castors.me().castTo("oFf", boolean.class));
        assertFalse(Castors.me().castTo("No", boolean.class));
        assertFalse(Castors.me().castTo("faLsE", boolean.class));
        assertFalse(Castors.me().castTo(" ", boolean.class));
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
        assertEquals(Float.valueOf(34.67f),
                     Castors.me().castTo("34.67", float.class));
        assertEquals(new Float(34.67), Castors.me()
                                              .castTo("34.67", Float.class));
    }

    @Test
    public void testFloat2String() throws FailToCastObjectException {
        String s = Castors.me().castTo(4.978f, String.class);
        assertEquals("4.978", s);
        s = Castors.me().castTo(new Float(4.978f), String.class);
        assertEquals("4.978", s);
    }

    @Test
    public void testString2JavaDate() throws FailToCastObjectException {
        java.util.Date date = Castors.me().castTo("2008-6-12",
                                                  java.util.Date.class);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        assertEquals(2008, cal.get(Calendar.YEAR));
        assertEquals(5, cal.get(Calendar.MONTH));
        assertEquals(12, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(0, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(0, cal.get(Calendar.MINUTE));
        assertEquals(0, cal.get(Calendar.SECOND));
    }

    @Test
    public void testString2Date() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(Castors.me().cast("1977-9-21",
                                      String.class,
                                      java.sql.Date.class));
        assertEquals(1977, cal.get(Calendar.YEAR));
        assertEquals(8, cal.get(Calendar.MONTH));
        assertEquals(21, cal.get(Calendar.DAY_OF_MONTH));
    }

    @Test
    public void testString2Time() throws FailToCastObjectException {
        Calendar cal = Calendar.getInstance();
        cal.setTime(Castors.me().cast("15:17:23",
                                      String.class,
                                      java.sql.Time.class));

        assertEquals(15, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(17, cal.get(Calendar.MINUTE));
        assertEquals(23, cal.get(Calendar.SECOND));
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

    @Test
    public void testIntArray2List() throws Exception {
        int[] inAry = {34, 78};
        List<?> list = Castors.me().castTo(inAry, List.class);
        assertEquals(2, list.size());
        assertEquals(34, list.get(0));
        assertEquals(78, list.get(1));
    }

    @Test
    public void testStringList2Arry() throws Exception {
        String[] inAry = {"e1", "e2"};
        List<?> list = Castors.me().cast(inAry, String[].class, List.class);
        String[] reAry = Castors.me().castTo(list, String[].class);
        assertTrue(Arrays.equals(inAry, reAry));
    }

    @Test
    public void testIntList2Array() throws Exception {
        int[] inAry = {34, 78};
        List<?> list = Castors.me().castTo(inAry, List.class);
        int[] reAry = Castors.me().castTo(list, int[].class);
        assertTrue(Arrays.equals(inAry, reAry));
    }

    @Test
    public void testArray2String() throws Exception {
        Email[] mails = {new Email("zzh@263.net"),
                         new Email("zozohtnt@yahoo.com.cn")};
        String done = Castors.me().castToString(mails);
        Email[] mails2 = Castors.me().castTo(done, Email[].class);
        assertTrue(Lang.equals(mails, mails2));
    }

    @Test
    public void testString2Array() throws Exception {
        String orgs = "zzh@263.net,zozohtnt@yahoo.com.cn";
        Email[] exp = {new Email("zzh@263.net"),
                       new Email("zozohtnt@yahoo.com.cn")};
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

    @Test
    public void testTimestamp2sqlDate() throws Exception {
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        java.sql.Date date = Castors.me().castTo(ts, java.sql.Date.class);
        test_date_equal(ts, date);
    }

    @Test
    public void testSqlDate2Timestamp() throws Exception {
        java.sql.Date date = new java.sql.Date(System.currentTimeMillis());
        Timestamp ts = Castors.me().castTo(date, Timestamp.class);
        test_date_equal(ts, date);
    }

    @Test
    public void testTimestamp2sqlTime() throws Exception {
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        java.sql.Time time = Castors.me().castTo(ts, java.sql.Time.class);
        test_date_equal(ts, time);
    }

    @Test
    public void testSqlTime2Timestamp() throws Exception {
        java.sql.Time time = new java.sql.Time(System.currentTimeMillis());
        Timestamp ts = Castors.me().castTo(time, Timestamp.class);
        test_date_equal(ts, time);
    }

    @Test
    public void testDatetime2sqlDate() throws Exception {
        java.util.Date ts = new java.util.Date(System.currentTimeMillis());
        java.sql.Date date = Castors.me().castTo(ts, java.sql.Date.class);
        test_date_equal(ts, date);
    }

    @Test
    public void testSqlDate2Datetime() throws Exception {
        java.sql.Date date = new java.sql.Date(System.currentTimeMillis());
        java.util.Date ts = Castors.me().castTo(date, java.util.Date.class);
        test_date_equal(ts, date);
    }

    @Test
    public void testDatetime2sqlTime() throws Exception {
        java.util.Date ts = new java.util.Date(System.currentTimeMillis());
        java.sql.Time time = Castors.me().castTo(ts, java.sql.Time.class);
        test_date_equal(ts, time);
    }

    @Test
    public void testSqlTime2Datetime() throws Exception {
        java.sql.Time time = new java.sql.Time(System.currentTimeMillis());
        java.util.Date ts = Castors.me().castTo(time, java.util.Date.class);
        test_date_equal(ts, time);
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

    @Test
    public void testDatetime2String() {
        Date dt = new Date();
        String strDt = Times.sDT(dt);
        assertEquals(Castors.me().castToString(dt), strDt);

    }

    @Test
    public void testSetSetting() {
        // 测试设置
        Date dt = new Date();
        String strD = Times.sD(dt);
        Castors cas = Castors.create();
        cas.setSetting(new TestCastorSetting());
        assertEquals(cas.castToString(dt), strD);
    }

    @Test
    public void testAddCastor() {
        Date dt = new Date();
        String strD = Times.sD(dt);
        Castors cas = Castors.create();
        // cas.setSetting(new TestCastorSetting());
        cas.addCastor(Date2String.class);
        assertEquals(cas.castToString(dt), strD);

    }

    private void test_date_equal(java.util.Date d1, java.util.Date d2) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(d1);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(d2);
        assertEquals(c1.get(Calendar.YEAR), c2.get(Calendar.YEAR));
        assertEquals(c1.get(Calendar.MONTH), c2.get(Calendar.MONTH));
        assertEquals(c1.get(Calendar.DAY_OF_MONTH),
                     c1.get(Calendar.DAY_OF_MONTH));
        assertEquals(c1.get(Calendar.HOUR_OF_DAY), c2.get(Calendar.HOUR_OF_DAY));
        assertEquals(c1.get(Calendar.MINUTE), c2.get(Calendar.MINUTE));
        assertEquals(c1.get(Calendar.SECOND), c2.get(Calendar.SECOND));
    }

    class TestCastorSetting {
        public void setup(Datetime2String c) {
            c.setFormat("yyyy-MM-dd");
        }
    }

    // @Test
    // public void load_form_nowhere() {
    // Castors castors = Castors.create().setPaths(new ArrayList<Class<?>>(0));
    // castors.castTo(1, Long.class);
    // }
}
