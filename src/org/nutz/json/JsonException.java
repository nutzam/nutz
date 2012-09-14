package org.nutz.json;

@SuppressWarnings("serial")
public class JsonException extends RuntimeException {

    public JsonException(Throwable cause) {
        super(cause);
    }

    public JsonException(String msg) {
        super(msg);
    }

    public JsonException(int row, int col, char cursor, String message) {
        super(String.format("!Json syntax error nearby [row:%d,col:%d char '%c'], reason: '%s'",
                            row,
                            col,
                            cursor,
                            message));
    }

    public JsonException(int row, int col, char cursor, String message, Throwable cause) {
        super(String.format("!Json syntax error nearby [row:%d,col:%d char '%c'], reason: '%s'",
                            row,
                            col,
                            cursor,
                            message), cause);
    }

}
