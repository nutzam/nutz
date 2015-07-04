package org.nutz.lang.born;

import java.lang.reflect.Modifier;

import org.nutz.lang.Lang;

@SuppressWarnings("serial")
public class BorningException extends RuntimeException {

    public BorningException(Class<?> type, Object[] args) {
        this(null, type, args);
    }

    public BorningException(Class<?> type, Class<?>[] argTypes) {
        this(null, type, argTypes);
    }

    public BorningException(Throwable e, Class<?> type, Object[] args) {
        super(makeMessage(e, type, args), Lang.unwrapThrow(e));
    }

    public BorningException(Throwable e, Class<?> type, Class<?>[] argTypes) {
        super(makeMessage(e, type, argTypes), Lang.unwrapThrow(e));
    }

    private static String makeMessage(Throwable e, Class<?> type, Class<?>[] argTypes) {
        StringBuilder sb = new StringBuilder();
        String name = null == type ? "unknown" : type.getName();
        sb.append("Fail to born '").append(name).append('\'');
        if (null != argTypes && argTypes.length > 0) {
            sb.append("\n by args: [");
            for (Object argType : argTypes)
                sb.append("\n  @(").append(argType).append(')');
            sb.append("]");
        }
        if (null != e) {
            sb.append(" becasue: ").append(getExceptionMessage(e));
        } else if (type != null){
            if (type.isInterface()) {
                sb.append(" becasue: ").append(type.getName()).append("is interface!!");
            } else if (Modifier.isAbstract(type.getModifiers())) {
                sb.append(" becasue: ").append(type.getName()).append("is abstract class and can't found static factory method!");
            }
        }
        return sb.toString();
    }

    private static String makeMessage(Throwable e, Class<?> type, Object[] args) {
        StringBuilder sb = new StringBuilder();
        String name = null == type ? "unknown" : type.getName();
        sb.append("Fail to born '").append(name).append('\'');
        if (null != args) {
            sb.append("\n by args: [");
            for (Object arg : args)
                sb.append("\n  @(").append(arg).append(')');
            sb.append("]");
        } else {
        	sb.append("\n by args: []");
        }
        if (null != e) {
            sb.append(" becasue:\n").append(getExceptionMessage(e));
        }
        return sb.toString();
    }

    private static String getExceptionMessage(Throwable e) {
    	e = Lang.unwrapThrow(e);
        return e.toString();
    }

}
