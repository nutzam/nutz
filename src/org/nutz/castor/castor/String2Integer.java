package org.nutz.castor.castor;

public class String2Integer extends String2Number<Integer> {

    @Override
    protected Integer getPrimitiveDefaultValue() {
        return 0;
    }

    @Override
    protected Integer valueOf(String str) {
        _N_Info ni = _eval_radix(str);
        return Integer.valueOf(ni.val, ni.radix);
    }

}
