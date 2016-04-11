package org.nutz.lang.random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.UUID;

import org.junit.Test;

public class RTest {

    @Test
    public void test_uu64_and_uu16() {
        for (int i = 0; i < 100000; i++) {
            UUID uu = UUID.randomUUID();
            String uu64 = R.UU64(uu);
            String uu16 = R.UU16(uu);
            UUID reUU = R.fromUU64(uu64);
            String re16 = R.UU16FromUU64(uu64);
            assertTrue(uu.equals(reUU));
            assertTrue(uu16.equals(re16));
        }
    }

    @Test
    public void test_uu32() {
        for (int i = 0; i < 100000; i++) {
            UUID uuid = UUID.randomUUID();
            // System.out.println(uuid);
            // System.out.println(R.UU32(uuid));
            // System.out.println(R.fromUU32(R.UU32(uuid)));
            assertEquals(uuid, R.fromUU32(R.UU32(uuid)));
        }
    }

    @Test
    public void test_captcha_length() throws Exception {
        assertEquals(0, R.captchaNumber(0).length());
        assertEquals(2, R.captchaNumber(2).length());
        assertEquals(4, R.captchaNumber(4).length());
        assertEquals(10, R.captchaNumber(10).length());
        assertEquals(2, R.captchaChar(2).length());
        assertEquals(4, R.captchaChar(4).length());
        assertEquals(10, R.captchaChar(10).length());
    }

    @Test
    public void test_captcha_content() throws Exception {
        String c1 = R.captchaNumber(100);
        assertTrue(hasNumber(c1));
        assertFalse(hasUpperLetter(c1));
        assertFalse(hasLowerLetter(c1));

        String c2 = R.captchaChar(1000);
        assertTrue(hasNumber(c2));
        assertTrue(hasLowerLetter(c2));
        assertFalse(hasUpperLetter(c2));

        // 1000个字符里肯定得有个大写的
        String c3 = R.captchaChar(1000, true);
        assertTrue(hasNumber(c3));
        assertTrue(hasLowerLetter(c3));
        assertTrue(hasUpperLetter(c3));
    }

    // 48~57
    public boolean hasNumber(String str) {
        for (int i = 0; i < str.length(); i++) {
            int c = (int) str.charAt(i);
            if (c >= 48 && c <= 57) {
                return true;
            }
        }
        return false;
    }

    // 65-90
    public boolean hasUpperLetter(String str) {
        for (int i = 0; i < str.length(); i++) {
            int c = (int) str.charAt(i);
            if (c >= 65 && c <= 90) {
                return true;
            }
        }
        return false;
    }

    // 97~122
    public boolean hasLowerLetter(String str) {
        for (int i = 0; i < str.length(); i++) {
            int c = (int) str.charAt(i);
            if (c >= 97 && c <= 122) {
                return true;
            }
        }
        return false;
    }
}
