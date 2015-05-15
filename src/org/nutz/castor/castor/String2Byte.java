package org.nutz.castor.castor;

public class String2Byte extends String2Number<Byte> {

    @Override
    protected Byte getPrimitiveDefaultValue() {
        return (byte) 0;
    }

    @Override
    protected Byte valueOf(String str) {
        _N_Info ni = _eval_radix(str);
        return Byte.valueOf(ni.val, ni.radix);
    }

}
