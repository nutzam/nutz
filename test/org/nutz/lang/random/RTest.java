package org.nutz.lang.random;

import static org.junit.Assert.*;

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

}
