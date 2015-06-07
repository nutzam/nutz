package org.nutz.castor.castor;

import org.nutz.lang.Nums;

public class String2Byte extends String2Number<Byte> {

    @Override
    protected Byte getPrimitiveDefaultValue() {
        return (byte) 0;
    }

    @Override
    protected Byte valueOf(String str) {
        Nums.Radix ni = Nums.evalRadix(str);
        return Byte.valueOf(ni.val, ni.radix);
    }

}
