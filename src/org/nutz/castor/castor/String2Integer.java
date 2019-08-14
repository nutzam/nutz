package org.nutz.castor.castor;

import org.nutz.lang.Nums;

public class String2Integer extends String2Number<Integer> {

    @Override
    protected Integer getPrimitiveDefaultValue() {
        return 0;
    }

    @Override
    protected Integer getFalseValue() {
        return 0;
    }

    @Override
    protected Integer getTrueValue() {
        return 1;
    }

    @Override
    protected Integer valueOf(String str) {
        Nums.Radix ni = Nums.evalRadix(str);
        try {
            return Integer.valueOf(ni.val, ni.radix);
        }
        catch (NumberFormatException e) {
            return Integer.valueOf(str);
        }
    }

}
