package org.nutz.castor.castor;

import java.math.BigDecimal;

import org.nutz.castor.Castor;
import org.nutz.castor.FailToCastObjectException;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

public class String2BigDecimal extends Castor<String, BigDecimal > {

    public BigDecimal cast(String src, Class<?> toType, String... args)
            throws FailToCastObjectException {
        if (Strings.isBlank(src)) {
                return null;
        }
        try {
            return new BigDecimal(src);
        }
        catch (Exception e) {
            throw new FailToCastObjectException(String.format(    "Fail to cast '%s' to <%s>",
                                                                src,
                                                                toType.getName()), Lang.unwrapThrow(e));
        }
    }

}
