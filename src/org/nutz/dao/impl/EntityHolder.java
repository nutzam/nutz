package org.nutz.dao.impl;

import java.sql.Connection;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.nutz.dao.ConnCallback;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.EntityMaker;
import org.nutz.dao.impl.entity.NutEntity;
import org.nutz.dao.impl.entity.field.NutMappingField;
import org.nutz.dao.jdbc.JdbcExpert;
import org.nutz.dao.jdbc.Jdbcs;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.eject.EjectFromMap;
import org.nutz.lang.inject.InjectToMap;
import org.nutz.lang.util.Callback;

/**
 * 封装一些获取实体对象的帮助函数
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class EntityHolder {

    // DaoSupport 会设置这个值
    public EntityMaker maker;

    protected JdbcExpert expert;

    private Map<Class<?>, Entity<?>> map;
    
    protected Callback<ConnCallback> connCallback;

    public EntityHolder(JdbcExpert expert, Callback<ConnCallback> connCallback) {
        this.expert = expert;
        this.connCallback = connCallback;
        this.map = new ConcurrentHashMap<Class<?>, Entity<?>>();
    }

    public void set(Entity<?> en) {
        synchronized (map) {
            this.map.put(en.getType(), en);
        }
    }

    public void remove(Entity<?> en) {
        if (en == null || en.getType() == null)
            return;
        synchronized (map) {
            this.map.remove(en.getType());
        }
    }

    /**
     * 根据类型获取实体
     * 
     * @param classOfT
     *            实体类型
     * @return 实体
     */
    @SuppressWarnings("unchecked")
    public <T> Entity<T> getEntity(Class<T> classOfT) {
        Entity<?> re = map.get(classOfT);
        if (null == re || !re.isComplete()) {
            synchronized (map) {
                re = map.get(classOfT);
                if (null == re) {
                    re = maker.make(classOfT);
                }
            }
        }
        return (Entity<T>) re;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public <T extends Map<String, ?>> Entity<T> makeEntity(String tableName, T map) {
        final NutEntity<T> en = new NutEntity(map.getClass());
        en.setTableName(tableName);
        en.setViewName(tableName);
        boolean check = false;
        for (Entry<String, ?> entry : map.entrySet()) {
            String key = entry.getKey();
            // 是实体补充描述吗？
            if (key.startsWith("#")) {
                en.getMetas().put(key.substring(1), entry.getValue().toString());
                continue;
            }
            // 以 "." 开头的字段，不是实体字段
            else if (key.startsWith(".")) {
                continue;
            }

            // 是实体字段
            Object value = entry.getValue();
            Mirror<?> mirror = Mirror.me(value);
            NutMappingField ef = new NutMappingField(en);

            while (true) {
                if (key.startsWith("+")) {
                    ef.setAsAutoIncreasement();
                    if (mirror != null && mirror.isIntLike())
                        ef.setAsId();
                    key = key.substring(1);
                }
                else if (key.startsWith("!")) {
                    ef.setAsNotNull();
                    key = key.substring(1);
                }
                else if (key.startsWith("*")) {
                    key = key.substring(1);
                    if (mirror != null && mirror.isIntLike())
                        ef.setAsId();
                    else
                        ef.setAsName();
                } else {
                    break;
                }
            }
            ef.setName(key);

            ef.setType(null == value ? Object.class : value.getClass());
            ef.setColumnName(key);

            // 猜测一下数据库类型
            Jdbcs.guessEntityFieldColumnType(ef);
            ef.setAdaptor(expert.getAdaptor(ef));
            if (mirror != null)
                ef.setType(mirror.getType());
            ef.setInjecting(new InjectToMap(key)); // 这里比较纠结,回设的时候应该用什么呢?
            ef.setEjecting(new EjectFromMap(entry.getKey()));

            if (ef.isAutoIncreasement()
                && ef.isId()
                && expert.isSupportAutoIncrement()
                && !expert.isSupportGeneratedKeys()) {
                en.addAfterInsertMacro(expert.fetchPojoId(en, ef));
            }

            en.addMappingField(ef);

            if (mirror != null && !check)
                check = mirror.isEnum();
        }
        en.checkCompositeFields(null);

        // 最后在数据库中验证一下实体各个字段
        if (check)
            connCallback.invoke(new ConnCallback() {
                public void invoke(Connection conn) throws Exception {
                    expert.setupEntityField(conn, en);
                }
            });

        // 搞定返回
        return en;
    }

    /**
     * 根据一个对象获取实体
     * <p>
     * 对象如果是集合或者数组，则取其第一个元素进行判断
     * 
     * @param obj
     *            对象
     * @return 实体
     */
    @SuppressWarnings("unchecked")
    public Entity<?> getEntityBy(Object obj) {
        // 正常的构建一个 Entity
        Object first = Lang.first(obj);
        // 对象为空，不能构建实体
        if (first == null)
            return null;

        // 这是一个 Map,试图构建一个 entity
        if (first instanceof Map<?, ?>) {
            Object tableName = ((Map<String, ?>) first).get(".table");
            if (null == tableName)
                throw Lang.makeThrow("Can not insert map without key '.table' : \n%s",
                                     Json.toJson(first, JsonFormat.forLook()));
            return makeEntity(tableName.toString(), (Map<String, ?>) first);
        }
        // 作为 POJO 构建
        return getEntity(first.getClass());
    }

    public boolean hasType(Class<?> typeName) {
        synchronized (map) {
            return map.containsKey(typeName);
        }
    }

    public void clear() {
        map.clear();
    }

    public void remove(String className) {
        Set<Class<?>> keys = new HashSet<Class<?>>(map.keySet());
        for (Class<?> klass : keys) {
            if (klass.getName().equals(className))
                map.remove(klass);
        }
    }
}
