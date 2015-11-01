package org.nutz.castor.castor;

import org.nutz.lang.Nums;

public class String2Integer extends String2Number<Integer> {

    @Override
    protected Integer getPrimitiveDefaultValue() {
        return 0;
    }

    @Override
    protected Integer valueOf(String str) {
        Nums.Radix ni = Nums.evalRadix(str);
        return Integer.valueOf(ni.val, ni.radix);
    }

}
