package org.nutz.dao.impl.entity.field;

import org.nutz.dao.Cnd;
import org.nutz.dao.Condition;
import org.nutz.dao.DaoException;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.LinkType;
import org.nutz.dao.entity.MappingField;
import org.nutz.dao.impl.EntityHolder;
import org.nutz.dao.impl.entity.NutEntity;
import org.nutz.dao.impl.entity.info.LinkInfo;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;

public class ManyLinkField extends AbstractLinkField {

    public ManyLinkField(Entity<?> entity, EntityHolder holder, LinkInfo info) {
        super(entity, holder, info);
        this.targetType = guessTargetClass(info, info.many.target());
        this.mapKey = info.many.key();

        Entity<?> ta = this.getLinkedEntity();
        
        if (Strings.isBlank(info.many.field())) {
            hostField = null;
            linkedField = null;
            return;
        }

        String targetFieldName = "_".equals(info.many.field()) ? Strings.lowerFirst(getEntity().getType()
                                                                                                    .getSimpleName())
                                                                      + "Id"
                                                                    : info.many.field();

        linkedField = ta.getField(targetFieldName);
        if (null == linkedField) {
            throw new DaoException(String.format("host class=%s, props=%s @Many(field=\"%s\",key=\"%s\")) expect prop=%s found at target class=%s",
                                                 getEntity().getType().getName(),
                                                 info.name,
                                                 targetFieldName,
                                                 info.many.key(),
                                                 targetFieldName,
                                                 targetType.getName()));
        }

        // 宿主实体的字段 - 应该是主键
        boolean intLike = linkedField.getMirror().isIntLike();
        if (Strings.isBlank(mapKey) || Mirror.me(info.fieldType).isMap()) {

            hostField = intLike ? getEntity().getIdField() : getEntity().getNameField();
            if (hostField == null) {
                throw new DaoException(String.format("host class=%s, prop=%s @Many(field=\"%s\",key=\"%s\")) expect any field %s found at target class=%s",
                                                     getEntity().getType().getName(),
                                                     info.name,
                                                     targetFieldName,
                                                     info.many.key(),
                                                     intLike ? "@Id" : "@Name",
                                                     targetType.getName()));
            }
        } else {
            hostField = this.getEntity().getField(mapKey);
            if (hostField == null) {
                throw new DaoException(String.format("host class=%s, prop=%s @Many(field=\"%s\",key=\"%s\")) expect prop=%s found at target class=%s",
                                                     getEntity().getType().getName(),
                                                     info.name,
                                                     targetFieldName,
                                                     info.many.key(),
                                                     mapKey,
                                                     targetType.getName()));
            }
        }
    }

    public ManyLinkField(NutEntity<?> en,
                         EntityHolder holder,
                         LinkInfo info,
                         Class<?> klass,
                         MappingField mf,
                         MappingField mfKey) {
        super(en, holder, info);
        this.targetType = klass;
        this.hostField = mf;
        this.linkedField = mfKey;
    }

    public Condition createCondition(Object host) {
        return null == linkedField ? null
                                   : Cnd.where(linkedField.getName(),
                                               "=",
                                               hostField.getValue(host));
    }

    public void updateLinkedField(Object obj, Object linked) {
        if (null != hostField) {
            final Object v = hostField.getValue(obj);
            Lang.each(linked, new Each<Object>() {
                public void invoke(int i, Object ele, int length) throws ExitLoop, LoopException {
                    linkedField.setValue(ele, v);
                }
            });
        }
    }

    public MappingField getHostField() {
        return hostField;
    }

    public MappingField getLinkedField() {
        return linkedField;
    }

    public void saveLinkedField(Object obj, Object linked) {}

    public LinkType getLinkType() {
        return LinkType.MANY;
    }

}
