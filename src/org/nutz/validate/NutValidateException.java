package org.nutz.validate;

@SuppressWarnings("serial")
public class NutValidateException extends Exception {

    private Object expect;

    private Object value;

    private String reason;

    public NutValidateException(String reason) {
        this.reason = reason;
    }

    public NutValidateException(String reason, Object expect, Object value) {
        this.reason = reason;
        this.expect = expect;
        this.value = value;
    }

    public Object getExpect() {
        return expect;
    }

    public void setExpect(Object expect) {
        this.expect = expect;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String getLocalizedMessage() {
        return this.toString();
    }

    @Override
    public String getMessage() {
        return this.toString();
    }

    @Override
    public String toString() {
        String str = this.reason;
        if (null != expect) {
            str += ", expect [" + expect + "]";
        }
        if (null != value) {
            str += ", but [" + value + ']';
        }
        return str;
    }

}
