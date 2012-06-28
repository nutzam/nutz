package org.nutz.lang.segment;

public class SegmentNode implements Cloneable {
    private boolean isKey;

    private String value;

    static SegmentNode key(String val) {
        SegmentNode node = new SegmentNode();
        node.isKey = true;
        node.value = val;
        return node;
    }

    static SegmentNode val(String val) {
        SegmentNode node = new SegmentNode();
        node.isKey = false;
        node.value = val;
        return node;
    }

    public SegmentNode clone() throws CloneNotSupportedException {
        SegmentNode node = new SegmentNode();
        node.isKey = this.isKey;
        node.value = this.value;
        return node;
    }

    public boolean isKey() {
        return isKey;
    }

    public String getValue() {
        return value;
    }
}
