package org.nutz.dao.impl.entity.field;

import java.util.Collection;
import java.util.Map;

import org.nutz.castor.Castors;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.LinkField;
import org.nutz.dao.entity.MappingField;
import org.nutz.dao.impl.EntityHolder;
import org.nutz.dao.impl.entity.info.LinkInfo;
import org.nutz.dao.impl.sql.pojo.PojoFetchEntityCallback;
import org.nutz.dao.impl.sql.pojo.PojoQueryEntityCallback;
import org.nutz.dao.sql.PojoCallback;
import org.nutz.lang.Mirror;

public abstract class AbstractLinkField extends AbstractEntityField implements LinkField {

    protected EntityHolder holder;

    protected Class<?> targetType;

    protected Entity<?> target;

    private PojoCallback callback;

    protected String mapKey;

    protected MappingField hostField;

    protected MappingField linkedField;
    
    private boolean[] lock = new boolean[0];//最小化的锁对象

    public AbstractLinkField(Entity<?> entity, EntityHolder holder, LinkInfo info) {
        super(entity);
        this.holder = holder;

        this.setName(info.name);
        this.setInjecting(info.injecting);
        this.setEjecting(info.ejecting);

        this.setType(info.fieldType);

        if (getTypeMirror().isOf(Collection.class)) {
            callback = new PojoQueryEntityCallback();
        } else if (getTypeMirror().isOf(Map.class)) {
            callback = new PojoQueryEntityCallback();
        } else if (getTypeClass().isArray()) {
            callback = new PojoQueryEntityCallback();
        } else {
            callback = new PojoFetchEntityCallback();
        }
    }

    @Override
    public void setValue(Object obj, Object value) {
        if (null != value) {
            if (!Mirror.me(value).canCastToDirectly(this.getTypeClass()))
                value = Castors.me().cast(value, value.getClass(), this.getTypeClass(), mapKey);
        }
        super.setValue(obj, value);
    }

    public Entity<?> getLinkedEntity() {
        if (null == target) {
            synchronized (lock) {
                if (null == target) {
                    if (targetType.equals(getEntity().getType()))
                        target = getEntity();
                    else
                        target = holder.getEntity(targetType);
                }
            }
        }
        return target;
    }

    public PojoCallback getCallback() {
        return callback;
    }

    public MappingField getHostField() {
        return hostField;
    }

    public MappingField getLinkedField() {
        return linkedField;
    }

}
