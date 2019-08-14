package org.nutz.castor.castor;

import org.nutz.lang.Nums;

public class String2Short extends String2Number<Short> {

    @Override
    protected Short getPrimitiveDefaultValue() {
        return 0;
    }

    @Override
    protected Short getFalseValue() {
        return 0;
    }

    @Override
    protected Short getTrueValue() {
        return 1;
    }

    @Override
    protected Short valueOf(String str) {
        Nums.Radix ni = Nums.evalRadix(str);
        return Short.valueOf(ni.val, ni.radix);
    }

}
