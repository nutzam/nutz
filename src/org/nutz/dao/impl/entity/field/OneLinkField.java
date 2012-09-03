package org.nutz.dao.impl.entity.field;

import org.nutz.dao.Cnd;
import org.nutz.dao.Condition;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.LinkType;
import org.nutz.dao.impl.EntityHolder;
import org.nutz.dao.impl.entity.info.LinkInfo;
import org.nutz.lang.Lang;

public class OneLinkField extends AbstractLinkField  {

    public OneLinkField(Entity<?> entity, EntityHolder holder, LinkInfo info) {
        super(entity, holder, info);
        this.targetType = info.one.target();
        // 宿主实体的字段
        hostField = entity.getField(info.one.field());
        if (null == hostField)
            throw Lang.makeThrow(    "Invalid @One(field=%s) '%s' : %s<=>%s",
                                    info.one.field(),
                                    this.getName(),
                                    this.getEntity().getType(),
                                    this.getLinkedEntity().getType());

        // 链接实体的字段 - 应该是主键
        linkedField = hostField.getTypeMirror().isIntLike()    ? this.getLinkedEntity().getIdField()
                                                            : this.getLinkedEntity().getNameField();
        if (null == linkedField)
            throw Lang.makeThrow(    "Fail to find linkedField for @One(field=%s) '%s' : %s<=>%s",
                                    info.one.field(),
                                    this.getName(),
                                    this.getEntity().getType(),
                                    this.getLinkedEntity().getType());

    }

    public Condition createCondition(Object host) {
        return Cnd.where(linkedField.getColumnName(), "=", hostField.getValue(host));
    }

    public void updateLinkedField(Object obj, Object linked) {}

    public void saveLinkedField(Object obj, Object linked) {
        Object v = linkedField.getValue(linked);
        hostField.setValue(obj, v);
    }

    public LinkType getLinkType() {
        return LinkType.ONE;
    }

}
