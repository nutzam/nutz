package org.nutz.dao.entity;

import org.nutz.dao.TableName;
import org.nutz.lang.segment.CharSegment;

public abstract class EntityName {

	public static EntityName create(String s) {
		CharSegment seg = new CharSegment(s);
		if (seg.keys().size() > 0)
			return new DynamicEntityName(seg);
		return new StaticEntityName(s);
	}

	static class DynamicEntityName extends EntityName {

		private CharSegment segment;

		private DynamicEntityName(CharSegment seg) {
			this.segment = seg;
		}

		String value() {
			return TableName.render(segment);
		}

		String orignalString() {
			return segment.getOrginalString();
		}

	}

	static class StaticEntityName extends EntityName {

		private String value;

		private StaticEntityName(String s) {
			this.value = s;
		}

		String value() {
			return value;
		}

		String orignalString() {
			return value;
		}

	}

	abstract String value();

	abstract String orignalString();

	public String toString() {
		return value();
	}

}
