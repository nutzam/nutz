package org.nutz.castor.castor;

public class String2Long extends String2Number<Long> {

    @Override
    protected Long getPrimitiveDefaultValue() {
        return 0L;
    }

    @Override
    protected Long valueOf(String str) {
        return Long.valueOf(str);
    }

}
