package org.nutz.dao.impl.entity.field;

import org.nutz.dao.Cnd;
import org.nutz.dao.Condition;
import org.nutz.dao.DaoException;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.LinkType;
import org.nutz.dao.entity.MappingField;
import org.nutz.dao.impl.EntityHolder;
import org.nutz.dao.impl.entity.info.LinkInfo;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

public class OneLinkField extends AbstractLinkField {

    public OneLinkField(Entity<?> entity,
                        EntityHolder holder,
                        LinkInfo info,
                        Class<?> target,
                        MappingField field,
                        MappingField key) {
        super(entity, holder, info);
        this.targetType = target;
        this.hostField = field;
        this.linkedField = key;
    }

    public OneLinkField(Entity<?> entity, EntityHolder holder, LinkInfo info) {
        super(entity, holder, info);
        this.targetType = guessTargetClass(info, info.one.target());
        if (Strings.isBlank(info.one.field()))
            throw new DaoException("Invalid @One(field=\"\") at class="
                                   + getEntity().getType().getName());
        String hostFieldName = "_".equals(info.one.field()) ? info.name + "Id" : info.one.field();
        // 宿主实体的字段
        hostField = entity.getField(hostFieldName);
        if (null == hostField)
            throw new DaoException(String.format("Invalid @One(field=%s) '%s' : %s<=>%s",
                                                 hostFieldName,
                                                 getName(),
                                                 getEntity().getType(),
                                                 getLinkedEntity().getType()));

        if (!Strings.isBlank(info.one.key())) {
            linkedField = this.getLinkedEntity().getField(info.one.key());
            if (linkedField == null)
                throw new DaoException(String.format("Fail to find linkedField for @One(field=%s) '%s' : %s<=>%s By key=%s",
                                                     hostFieldName,
                                                     getName(),
                                                     getEntity().getType(),
                                                     getLinkedEntity().getType(),
                                                     info.one.key()));
            return;
        }

        // 链接实体的字段
        linkedField = hostField.getMirror().isIntLike() ? this.getLinkedEntity().getIdField()
                                                            : this.getLinkedEntity().getNameField();
        if (null == linkedField)
            throw Lang.makeThrow("Fail to find linkedField for @One(field=%s) '%s' : %s<=>%s By %s",
                                 hostFieldName,
                                 getName(),
                                 getEntity().getType(),
                                 getLinkedEntity().getType(),
                                 hostField.getMirror().isIntLike() ? "@Id" : "@Name");

    }

    public Condition createCondition(Object host) {
        return Cnd.where(linkedField.getColumnName(), "=", hostField.getValue(host));
    }

    public void updateLinkedField(Object obj, Object linked) {
        if (hostField.isId()) {
            Object val = linkedField.getValue(linked);
            if (val != null && val instanceof Number) {
                if (((Number) val).doubleValue() == 0.0) {
                    linkedField.setValue(linked, hostField.getValue(obj));
                }
            }
        }
    }

    public void saveLinkedField(Object obj, Object linked) {
        Object v = linkedField.getValue(linked);
        hostField.setValue(obj, v);
    }

    public LinkType getLinkType() {
        return LinkType.ONE;
    }

}
