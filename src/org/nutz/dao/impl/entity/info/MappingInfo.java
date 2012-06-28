package org.nutz.dao.impl.entity.info;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.Default;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Next;
import org.nutz.dao.entity.annotation.PK;
import org.nutz.dao.entity.annotation.Prev;
import org.nutz.dao.entity.annotation.Readonly;
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

    public Comment columnComment;

    public Class<?> getFieldTypeClass() {
        return Lang.getTypeClass(fieldType);
    }

    public Mirror<?> getFieldTypeMirror() {
        return Mirror.me(getFieldTypeClass());
    }

}
