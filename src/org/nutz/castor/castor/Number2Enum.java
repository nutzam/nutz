package org.nutz.castor.castor;

import java.lang.reflect.Field;

import org.nutz.castor.Castor;
import org.nutz.castor.FailToCastObjectException;
import org.nutz.lang.Lang;

@SuppressWarnings({"rawtypes"})
public class Number2Enum extends Castor<Number, Enum> {

    @Override
    public Enum cast(Number src, Class<?> toType, String... args) throws FailToCastObjectException {
        try {
            for (Field field : toType.getFields()) {
                if (field.getType() == toType) {
                    Enum em = (Enum) field.get(null);
                    if (em.ordinal() == src.intValue())
                        return em;
                }
            }
            throw Lang.makeThrow(    FailToCastObjectException.class,
                                    "Can NO find enum value in [%s] by int value '%d'",
                                    toType.getName(),
                                    src.intValue());
        }
        catch (Exception e) {
            throw Lang.wrapThrow(e, FailToCastObjectException.class);
        }
    }

}
