package org.nutz.lang.random;

import static org.junit.Assert.*;

import org.junit.Test;

import org.nutz.lang.Lang;
import org.nutz.lang.random.ArrayRandom;
import org.nutz.lang.random.Random;

public class ArrayRandomTest {

    @Test
    public void testString() {
        Random<String> r = new ArrayRandom<String>(Lang.array("A", "B", "C"));
        int i = 0;
        while (null != r.next()) {
            i++;
        }
        assertEquals(3, i);
    }

}
