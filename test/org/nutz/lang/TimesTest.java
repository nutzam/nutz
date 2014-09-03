package org.nutz.lang;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;

public class TimesTest {

    private static void AD(String b, String e, Date[] ds) {
        assertEquals(b, Times.sDT(ds[0]));
        assertEquals(e, Times.sDT(ds[1]));
    }

    /**
     * For issue #524
     */
    @Test
    public void test_with_timezone() {
        long ms0 = Times.ams("2013-09-14T12:33:14+08:00");
        long ms1 = Times.ams("2013-09-14T12:33:14-08:00");
        assertEquals(16 * 3600 * 1000, ms1 - ms0);
    }

    /**
     * For issue #524
     */
    @Test
    public void test_sep_by_T() {
        Date d0 = Times.D("2013-09-14 12:33:14");
        Date d1 = Times.D("2013-09-14T12:33:14");
        assertEquals(d0.getTime(), d1.getTime());
    }

    @Test
    public void test_1940() {
        Date d = Times.D("1940-8-15");
        assertEquals("1940-08-15", Times.sD(d));
    }

    @Test
    public void test_d() {
        Date d = new Date(System.currentTimeMillis());
        assertEquals(Times.now().getTime() / 1000, Times.D(Times.sDT(d)).getTime() / 1000);
    }

    @Test
    public void test_ztask_weeks() {
        long base = Times.D("2012-02-06 17:35:12").getTime();

        AD("2012-02-05 00:00:00", "2012-02-11 23:59:59", Times.week(base, 0));
        AD("2012-01-29 00:00:00", "2012-02-04 23:59:59", Times.week(base, -1));
        AD("2012-01-22 00:00:00", "2012-01-28 23:59:59", Times.week(base, -2));
        AD("2012-02-12 00:00:00", "2012-02-18 23:59:59", Times.week(base, 1));
        AD("2012-02-19 00:00:00", "2012-02-25 23:59:59", Times.week(base, 2));

        AD("2012-01-22 00:00:00", "2012-02-11 23:59:59", Times.weeks(base, -2, 0));
        AD("2012-01-22 00:00:00", "2012-02-25 23:59:59", Times.weeks(base, -2, 2));

        // 测测跨年
        base = Times.D("2012-01-04 17:35:12").getTime();

        AD("2011-12-25 00:00:00", "2012-01-14 23:59:59", Times.weeks(base, 1, -1));
    }

    @Test
    public void test_toTimeMillis() throws Exception {
        assertEquals(100000, Times.toMillis("100s"));
        assertEquals(120000, Times.toMillis("2m"));
        assertEquals(10800000, Times.toMillis("3h"));
        assertEquals(172800000, Times.toMillis("2d"));
        assertEquals(100000, Times.toMillis("100S"));
        assertEquals(120000, Times.toMillis("2M"));
        assertEquals(10800000, Times.toMillis("3H"));
        assertEquals(172800000, Times.toMillis("2D"));
        assertEquals(1000, Times.toMillis("1000"));
    }

}
