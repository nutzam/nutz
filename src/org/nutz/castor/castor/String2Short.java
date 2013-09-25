package org.nutz.castor.castor;

public class String2Short extends String2Number<Short> {

    @Override
    protected Short getPrimitiveDefaultValue() {
        return 0;
    }

    @Override
    protected Short valueOf(String str) {
        return Short.valueOf(str);
    }

}
