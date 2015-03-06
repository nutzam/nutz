package org.nutz.castor;

public enum AlipayNotifyType {
    StatusSync("trade_status_sync");

    private String state;

    private AlipayNotifyType(String s) {
        state = s;
    }

    @Override
    public String toString() {
        return state;
    }
}
