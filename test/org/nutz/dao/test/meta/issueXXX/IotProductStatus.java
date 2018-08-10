package org.nutz.dao.test.meta.issueXXX;
public enum IotProductStatus {
    DEVELOP(100, "开发"),
    PRODUCT(200, "生产");

    private final int value;
    private final String text;

    IotProductStatus(int value, String text) {
        this.value = value;
        this.text = text;
    }

    public int getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

    public int value() {
        return value;
    }

    public String text() {
        return text;
    }

    public static IotProductStatus from(int value) {
        for (IotProductStatus t : values()) {
            if (t.value == value) {
                return t;
            }
        }
        throw new IllegalArgumentException("unknown IotProductStatus: " + value);
    }
}
