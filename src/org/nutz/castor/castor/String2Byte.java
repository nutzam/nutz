package org.nutz.castor.castor;

public class String2Byte extends String2Number<Byte> {

    @Override
    protected Byte getPrimitiveDefaultValue() {
        return (byte) 0;
    }

    @Override
    protected Byte valueOf(String str) {
        return Byte.valueOf(str);
    }

}
