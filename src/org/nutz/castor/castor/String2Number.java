package org.nutz.castor.castor;

import org.nutz.castor.Castor;
import org.nutz.castor.FailToCastObjectException;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

/**
 * 根据一个字符串将其转换成 Number 类型。这里有几个规则
 * <ul>
 * <li>如果 Number 为原生类型，空白串将被转换成 0
 * <li>如果 Number 为外覆类，空白串将被转换成 null
 * </ul>
 * 
 * 如果转换失败，将抛出 FailToCastObjectException
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public abstract class String2Number<T> extends Castor<String, T> {

    protected boolean _isNull(String str) {
        return Strings.isBlank(str) || "null".equalsIgnoreCase(str);
    }

    protected abstract T getPrimitiveDefaultValue();

    protected abstract T valueOf(String str);

    @Override
    public T cast(String src, Class<?> toType, String... args) {
        if (Strings.isBlank(src) || "null".equalsIgnoreCase(src)) {
            return toType.isPrimitive() ? getPrimitiveDefaultValue() : null;
        }
        if (!toType.isPrimitive()
            && ("null".equals(src) || "NULL".equals(src) || "Null".equals(src))) {
            return null;
        }
        try {
            return valueOf(src);
        }
        catch (Exception e) {
            throw new FailToCastObjectException(String.format("Fail to cast '%s' to <%s>",
                                                              src,
                                                              toType.getName()),
                                                Lang.unwrapThrow(e));
        }
    }

}
