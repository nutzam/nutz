package org.nutz.dao;

@SuppressWarnings("serial")
public class DaoException extends RuntimeException {

    public DaoException(String message) {
        super(message);
    }

    public DaoException(Throwable cause) {
        super(cause);
    }

    public DaoException(String message, Throwable cause) {
        super(message, cause);
    }

    public static <T> DaoException create(T obj, String fieldName, String name, Exception e) {
        if (e instanceof DaoException)
            return (DaoException) e;
        return new DaoException(String.format(    "Fail to %s [%s]->[%s], because: '%s'",
                                                name,
                                                obj == null ? "NULL object" : obj    .getClass()
                                                                                    .getName(),
                                                fieldName,
                                                null == e ? "" : e.getMessage()));
    }

}
