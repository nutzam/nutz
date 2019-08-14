package org.nutz.castor.castor;

import java.math.BigDecimal;

public class String2BigDecimal extends String2Number<BigDecimal> {

    @Override
    protected BigDecimal getPrimitiveDefaultValue() {
        return new BigDecimal(0);
    }

    @Override
    protected BigDecimal getFalseValue() {
        return BigDecimal.ZERO;
    }

    @Override
    protected BigDecimal getTrueValue() {
        return BigDecimal.ONE;
    }

    @Override
    protected BigDecimal valueOf(String str) {
        return new BigDecimal(str);
    }

}
