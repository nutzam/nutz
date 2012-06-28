package org.nutz.lang.random;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;
import org.nutz.lang.Lang;

public class EnumRandomTest {
    public enum Color {
        RED, BLUE, YELLOW, WHITE
    };

    @Ignore("EnumRandom是可预知顺序的??")
    @Test
    public void test_enum() {
        Random<Color> r = new EnumRandom<Color>() {};
        Set<Color> re = new HashSet<Color>(Arrays.asList(Color.values()));
        Set<Color> rs = new HashSet<Color>();
        for (int j = 0; j < 4; j++) {
            for (int i = 0; i < 4; i++)
                rs.add(r.next());
            assertTrue(Lang.equals(re, rs));
        }
    }
}
