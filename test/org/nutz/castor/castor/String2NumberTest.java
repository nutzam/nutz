package org.nutz.castor.castor;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class String2NumberTest {

    private String2Number<Integer> string2Number = new String2Number<Integer>() {

        @Override
        protected Integer getPrimitiveDefaultValue() {
            return 0;
        }

        @Override
        protected Integer getFalseValue() {
            return falseValue;
        }

        @Override
        protected Integer getTrueValue() {
            return trueValue;
        }

        @Override
        protected Integer valueOf(final String str) {
            return Integer.valueOf(str);
        }

    };
    private Integer falseValue = Integer.valueOf(0);
    private Integer trueValue = Integer.valueOf(1);

    @Test
    public void testTrueValue() {

        assertEquals(string2Number.cast("true", Integer.class), this.trueValue);

    }

    @Test
    public void testFalseValue() {

        assertEquals(string2Number.cast("false", Integer.class), this.falseValue);

    }
}
