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

public class ManyLinkField extends AbstractLinkField  {

    public ManyLinkField(Entity<?> entity, EntityHolder holder, LinkInfo info) {
        super(entity, holder, info);
        this.targetType = guessTargetClass(info, info.many.target());
        this.mapKey = info.many.key();

        Entity<?> ta = this.getLinkedEntity();

        // 链接对方全部内容
        if (Strings.isBlank(info.many.field())) {
            hostField = null;
            linkedField = null;
        }
        // 根据一个字段的值链接
        else {
            linkedField = ta.getField(info.many.field());

            if (null == linkedField)
                throw Lang.makeThrow(    "Invalid @Many(field=%s) '%s' : %s<=>%s",
                                        info.many.field(),
                                        this.getName(),
                                        this.getEntity().getType(),
                                        ta.getType());

            // 宿主实体的字段 - 应该是主键
            boolean intLike = linkedField.getTypeMirror().isIntLike();
            if (Strings.isBlank(mapKey) || Mirror.me(info.fieldType).isMap()) {
                
                hostField = intLike ? getEntity().getIdField() : getEntity().getNameField();
                if (hostField == null) {
                    throw new DaoException(String.format("host class=%s, props=%s @Many(field=\"%s\",key=\"%s\")) expect %s at target class=%s", 
                                                         getEntity().getType().getName(),
                                                         info.name,
                                                         info.many.field(),
                                                         info.many.key(),
                                                         intLike ? "@Id" : "@Name",
                                                         targetType.getName()));
                }
            } else {
                hostField = this.getEntity().getField(mapKey);
                if (hostField == null) {
                    throw new DaoException(String.format("host class=%s, props=%s @Many(field=\"%s\",key=\"%s\")) expect prop=%s at target class=%s", 
                                                         getEntity().getType().getName(),
                                                         info.name,
                                                         info.many.field(),
                                                         info.many.key(),
                                                         mapKey,
                                                         targetType.getName()));
                }
            }
        }
    }

    public ManyLinkField(NutEntity<?> en, EntityHolder holder, LinkInfo info, Class<?> klass, MappingField mf, MappingField mfKey) {
    	super(en, holder, info);
    	this.targetType = klass;
    	this.hostField = mf;
    	this.linkedField = mfKey;
	}

	public Condition createCondition(Object host) {
        return null == linkedField ? null : Cnd.where(    linkedField.getName(),
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
