package org.nutz.dao.impl.entity;

import org.nutz.dao.TableName;
import org.nutz.lang.segment.CharSegment;

public abstract class EntityName {

    public static EntityName create(String s) {
        CharSegment seg = new CharSegment(s);
        if (seg.keys().size() > 0)
            return new DynamicEntityName(seg);
        return new StaticEntityName(s);
    }

    public static class DynamicEntityName extends EntityName {

        private CharSegment segment;

        private DynamicEntityName(CharSegment seg) {
            this.segment = seg;
        }

        public String value() {
            return TableName.render(segment);
        }

        public String getOrignalString() {
            return segment.getOrginalString();
        }

    }

    public static class StaticEntityName extends EntityName {

        private String value;

        private StaticEntityName(String s) {
            this.value = s;
        }

        public String value() {
            return value;
        }

        public String getOrignalString() {
            return value;
        }

    }

    public abstract String value();

    public abstract String getOrignalString();

    public String toString() {
        return value();
    }

}
