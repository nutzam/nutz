package org.nutz.lang;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.nutz.lang.Maths.bit;
import static org.nutz.lang.Maths.isMask;
import static org.nutz.lang.Maths.isMaskAll;
import static org.nutz.lang.Maths.isNoMask;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class MathsTest {

    private int[] nums = new int[]{12, 5, 26, 33, -4, 11, 8};

    @Test
    public void testIsMask() {
        assertTrue(isMask(bit("100"), bit("110")));
        assertFalse(isMask(bit("100"), bit("10010")));
    }

    @Test
    public void testIsMaskAll() {
        assertFalse(isMaskAll(bit("100"), bit("110")));
        assertTrue(isMaskAll(bit("100101111"), bit("001110")));
    }

    @Test
    public void test_extract_int() {
        assertEquals(3, Maths.extract(7, 1, 3));
        assertEquals(1, Maths.extract(7, 0, 1));
        assertEquals(3, Maths.extract(255, 4, 6));
    }

    @Test
    public void test_is_not_mask_all() {
        assertFalse(isNoMask(bit("0110"), bit("1100")));
        assertFalse(isNoMask(bit("0100"), bit("1100")));
        assertFalse(isNoMask(bit("1000"), bit("1100")));
        assertTrue(isNoMask(bit("110011"), bit("1100")));
        assertFalse(isNoMask(bit("111011"), bit("1100")));
    }

    @Test
    public void test_max() throws Exception {
        assertEquals(Maths.max(nums), 33);
    }

    @Test
    public void test_min() throws Exception {
        assertEquals(Maths.min(nums), -4);
    }

    @Test
    public void test_permutation_length_is_zero() throws Exception {
        String[] slist = Maths.permutation(0, '1', '2', '3');
        assertNull(slist);
    }

    @Test
    public void test_permutation_length_is_out() throws Exception {
        String[] slist = Maths.permutation(4, '1', '2', '3');
        assertNull(slist);
    }

    @Test
    public void test_permutation_nums() throws Exception {
        String[] slist = Maths.permutation(new char[]{'1', '2', '3'});
        assertTrue(slist.length == 6);
        assertFalse(hasSameString(slist));
        assertTrue(hasString("123", slist));
        assertTrue(hasString("132", slist));
        assertTrue(hasString("321", slist));
        assertFalse(hasString("221", slist));
        assertFalse(hasString("223", slist));
        assertFalse(hasString("323", slist));
    }

    @Test
    public void test_permutation_chars() throws Exception {
        String[] slist = Maths.permutation(2, 'a', 'b', 'c', 'd');
        assertTrue(slist.length == 12);
        assertFalse(hasSameString(slist));
        assertTrue(hasString("ab", slist));
        assertTrue(hasString("bd", slist));
        assertTrue(hasString("cd", slist));
        assertFalse(hasString("aa", slist));
        assertFalse(hasString("be", slist));
        assertFalse(hasString("d2", slist));
    }

    public boolean hasSameString(String... arr) {
        Set<String> strSet = new HashSet<String>();
        for (String astr : arr) {
            if (strSet.contains(astr)) {
                return true;
            }
            strSet.add(astr);
        }
        return false;
    }

    public boolean hasString(String vstr, String... arr) {
        for (String astr : arr) {
            if (vstr.equals(astr)) {
                return true;
            }
        }
        return false;
    }

}
