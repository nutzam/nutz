package org.nutz.castor.castor;

import org.nutz.castor.Castor;
import org.nutz.castor.FailToCastObjectException;
import org.nutz.lang.Strings;

@SuppressWarnings({"rawtypes"})
public class String2Enum extends Castor<String, Enum> {

    @SuppressWarnings("unchecked")
    @Override
    public Enum cast(String src, Class<?> toType, String... args) throws FailToCastObjectException {
        if (Strings.isBlank(src))
            return null;
        try {
            return Enum.valueOf((Class<Enum>) toType, src);
        }
        catch (IllegalArgumentException e) {
            for (Object c : toType.getEnumConstants()) {
                if (c.toString().equals(src)) return (Enum) c;
            }

            throw e;
        }
    }

}
