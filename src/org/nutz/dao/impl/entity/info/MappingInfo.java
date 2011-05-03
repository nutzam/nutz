package org.nutz.dao.impl.entity.info;

import org.nutz.dao.entity.annotation.*;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;

public class MappingInfo extends FieldInfo {

	public PK annPK;

	public Column annColumn;

	public ColDefine annDefine;

	public Default annDefault;

	public Id annId;

	public Name annName;

	public Next annNext;

	public Prev annPrev;

	public Readonly annReadonly;

	public Class<?> getFieldTypeClass() {
		return Lang.getTypeClass(fieldType);
	}

	public Mirror<?> getFieldTypeMirror() {
		return Mirror.me(getFieldTypeClass());
	}

}
