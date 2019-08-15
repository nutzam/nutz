package org.nutz.castor.castor;

import org.nutz.lang.Nums;

public class String2Long extends String2Number<Long> {

    @Override
    protected Long getPrimitiveDefaultValue() {
        return 0L;
    }

    @Override
    protected Long getFalseValue() {
        return 0L;
    }

    @Override
    protected Long getTrueValue() {
        return 1L;
    }

    @Override
    protected Long valueOf(String str) {
        try {
            Nums.Radix ni = Nums.evalRadix(str);
            return Long.valueOf(ni.val, ni.radix);
        }
        catch (NumberFormatException e) {
            return Long.valueOf(str);
        }
    }

}
