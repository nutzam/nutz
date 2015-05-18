package org.nutz.castor.castor;

public class String2Short extends String2Number<Short> {

    @Override
    protected Short getPrimitiveDefaultValue() {
        return 0;
    }

    @Override
    protected Short valueOf(String str) {
        _N_Info ni = _eval_radix(str);
        return Short.valueOf(ni.val, ni.radix);
    }

}
