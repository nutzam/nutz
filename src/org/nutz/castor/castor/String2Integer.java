package org.nutz.castor.castor;

public class String2Integer extends String2Number<Integer> {

    @Override
    protected Integer getPrimitiveDefaultValue() {
        return 0;
    }

    @Override
    protected Integer valueOf(String str) {
        return Integer.valueOf(str);
    }

}
