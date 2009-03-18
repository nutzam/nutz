package com.zzh.dao.entity;

import com.zzh.dao.TableName;
import com.zzh.lang.segment.CharSegment;

abstract class EntityName {

	static EntityName create(String s) {
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

		@Override
		String value() {
			return TableName.getName(segment);
		}

	}

	static class StaticEntityName extends EntityName {

		private String value;

		private StaticEntityName(String s) {
			this.value = s;
		}

		@Override
		String value() {
			return value;
		}

	}

	abstract String value();
}
