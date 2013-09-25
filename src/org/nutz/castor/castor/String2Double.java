package org.nutz.castor.castor;

public class String2Double extends String2Number<Double> {

    @Override
    protected Double getPrimitiveDefaultValue() {
        return 0.0;
    }

    @Override
    protected Double valueOf(String str) {
        return Double.valueOf(str);
    }

}
