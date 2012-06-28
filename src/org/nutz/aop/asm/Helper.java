package org.nutz.aop.asm;


/**
 * 
 * @author wendal(wendal1985@gmail.com)
 *
 */
public final class Helper {
    
    public static byte valueOf(Byte value) {
        if (value == null)
            return 0;
        return value.byteValue();
    }

    public static short valueOf(Short value) {
        if (value == null)
            return 0;
        return value.shortValue();
    }

    public static int valueOf(Integer value) {
        if (value == null)
            return 0;
        return value.intValue();
    }

    public static long valueOf(Long value) {
        if (value == null)
            return 0;
        return value.longValue();
    }

    public static double valueOf(Double value) {
        if (value == null)
            return 0;
        return value.doubleValue();
    }

    public static float valueOf(Float value) {
        if (value == null)
            return 0;
        return value.floatValue();
    }

    public static boolean valueOf(Boolean value) {
        if (value == null)
            return false;
        return value.booleanValue();
    }

    public static char valueOf(Character value) {
        if (value == null)
            return 0;
        return value.charValue();
    }
    
}
