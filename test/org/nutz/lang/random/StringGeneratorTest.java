package org.nutz.lang.random;

import static org.junit.Assert.*;
import org.junit.Test;

public class StringGeneratorTest {
    @Test
    public void test_next_with_invalid_arguments() {
        assertNull(new StringGenerator(0).next());
        assertNull(new StringGenerator(-1, 2).next());
        assertNull(R.sg(-4, -2).next());
        StringGenerator invalid_sg = new StringGenerator(8, 4);
        assertNull(invalid_sg.next());
        invalid_sg.setup(4, 2);
        assertNull(invalid_sg.next());
    }

    @Test
    public void test_next_with_valid_arguments() {
        StringGenerator valid_sg = new StringGenerator(1);
        assertEquals(1, valid_sg.next().length());
        valid_sg = R.sg(2, 2);
        assertEquals(2, valid_sg.next().length());
        valid_sg.setup(2, 5);
        String generate_str = valid_sg.next();
        int len = generate_str.length();
        assertTrue(len <= 5 && len >= 2);
    }
}
