package org.nutz.castor.castor;

public class String2Float extends String2Number<Float> {

    @Override
    protected Float getPrimitiveDefaultValue() {
        return 0.0f;
    }

    @Override
    protected Float getFalseValue() {
        return (float) 0;
    }

    @Override
    protected Float getTrueValue() {
        return (float)1;
    }

    @Override
    protected Float valueOf(String str) {
        return Float.valueOf(str);
    }

}
