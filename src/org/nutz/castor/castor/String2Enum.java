package org.nutz.castor.castor;

import org.nutz.castor.Castor;
import org.nutz.castor.Castors;
import org.nutz.castor.FailToCastObjectException;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@SuppressWarnings({"rawtypes"})
public class String2Enum extends Castor<String, Enum> {

    @SuppressWarnings("unchecked")
    @Override
    public Enum cast(String src, Class<?> toType, String... args) throws FailToCastObjectException {
        if (Strings.isBlank(src))
            return null;
        try {
            return Enum.valueOf((Class<Enum>) toType, src);
        } catch (IllegalArgumentException e) {
            try {
                Mirror<?> me = Mirror.me(toType);
                Field value = me.getField("value");
                Method from = toType.getMethod("from", value.getType());
                return (Enum) from.invoke(null, Castors.me().castTo(src, value.getType()));
            } catch (Exception e1) {
                for (Object c : toType.getEnumConstants()) {
                    if (c.toString().equals(src)) return (Enum) c;
                }
                throw e;
            }
        }
    }

}
