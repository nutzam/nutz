package org.nutz.lang.segment;

import static org.junit.Assert.*;

import org.junit.Test;
import org.nutz.lang.Lang;
import org.nutz.lang.util.Context;

public class SegmentsTest {

    @Test
    public void test_simple_replace() {
        String ptn = "1${A}2${B}3${C}4";
        Context context = Lang.context();
        context.set("B", "haha");
        String str = Segments.replace(ptn, context);

        assertEquals("1${A}2haha3${C}4", str);
    }

}
