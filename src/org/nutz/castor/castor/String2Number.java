package org.nutz.castor.castor;

import org.nutz.castor.Castor;
import org.nutz.castor.FailToCastObjectException;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
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
public class String2Number extends Castor<String, Number> {

    @Override
    public Number cast(String src, Class<?> toType, String... args) {
        if (Strings.isBlank(src)) {
            if (toType.isPrimitive())
                return 0;
            else
                return null;
        }
        if (!toType.isPrimitive() && ("null".equals(src) || "NULL".equals(src) || "Null".equals(src))){
            return null;
        }
        try {
            return (Number) Mirror.me(toType)
                    .getWrapperClass()
                    .getConstructor(String.class)
                    .newInstance(src);
        }
        catch (Exception e) {
            throw new FailToCastObjectException(String.format(    "Fail to cast '%s' to <%s>",
                    src,
                    toType.getName()), Lang.unwrapThrow(e));
        }
    }
}
