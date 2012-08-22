package org.nutz.dao;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.MappingField;
import org.nutz.dao.jdbc.ValueAdaptor;
import org.nutz.json.Json;
import org.nutz.lang.Mirror;

/**
 * 名值链。
 * <p>
 * 通过 add 方法，建立一条名值对的链表
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author Wendal(wendal1985@gmail.com)
 * @author lzxz1234
 */
public abstract class Chain {
    
    /**
     * 建立一条名值链开始的一环
     * 
     * @param name
     *            名称
     * @param value
     *            值
     * @return 链头
     */
    public static Chain make(String name, Object value) {
        DefaultChain chain = new DefaultChain(name, value);
        return chain;
    }

    /**
     * @return 链的长度
     */
    public abstract int size();

    /**
     * 改变当前节点的名称
     * 
     * @param name
     *            新名称
     * @return 当前节点
     */
    public abstract Chain name(String name);

    /**
     * 改变当前节点的值
     * 
     * @param value
     *            新值
     * @return 当前节点
     */
    public abstract Chain value(Object value);

    /**
     * 设置节点的参考适配器
     * 
     * @param adaptor
     *            适配器
     * @return 当前节点
     */
    public abstract Chain adaptor(ValueAdaptor adaptor);

    /**
     * @return 当前节点的参考适配器
     */
    public abstract ValueAdaptor adaptor();

    /**
     * 将一个名值对，添加为本链节点的下一环
     * 
     * @param name
     *            名
     * @param value
     *            值
     * @return 新增加的节点
     */
    public abstract Chain add(String name, Object value);
    
    /**
     * @return 当前节点的名称
     */
    public abstract String name();

    /**
     * @return 当前节点的值
     */
    public abstract Object value();

    /**
     * @return 往后移动一个结点，到达末尾返回空，否则返回当前对象
     */
    public abstract Chain next();

    /**
     * @return 整个链的第一环（头节点）
     */
    public abstract Chain head();

    /**
     * 根据 Entity 里的设定，更新整个链所有节点的名称。
     * <p>
     * 如果节点的名称是 Entity 的一个字段，则采用数据库字段的名称
     * 
     * @param entity
     *            实体
     * @return 链头节点
     */
    public abstract Chain updateBy(Entity<?> entity);

    /**
     * 由当前的名值链，生成一个对象
     * 
     * @param classOfT
     *            对象类型
     * @return 对象实例
     */
    public abstract <T> T toObject(Class<T> classOfT);

    /**
     * 由当前名值链，生成一个 Map
     * 
     * @return Map
     */
    public abstract Map<String, Object> toMap();


    /**
     * 整个Chain是否为特殊Chain，只要有一个特殊结点，就是特殊Chain
     * @see org.nutz.dao.Chain#addSpecial(String, Object)
     * @since 1.b.44
     */
    public abstract boolean isSpecial();
    
    /** 
     * 当前结点是不是特殊结点
     * @return
     */
    public abstract boolean special();
    
    /**
     * 由当前的值链生成一个可被实体化的 Map。 即有 '.table' 属性
     * 
     * @param tableName
     *            表名
     * @return 可被实体化的 Map
     */
    public Map<String, Object> toEntityMap(String tableName) {
        Map<String, Object> map = toMap();
        map.put(".table", tableName);
        return map;
    }

    /**
     * 生成一个 JSON 字符串
     */
    public String toString() {
        return Json.toJson(toMap());
    }

    /**
     * 根据一个对象的字段 生成一个 Chain 对象
     * <p>
     * 这个对象可以是一个 POJO 或者是一个 Map。
     * <p>
     * 支持 FieldMatcher，即你可以通过 FieldMatcher 来指定你需要哪些字段加入 Chain
     * 
     * @param obj
     *            对象，可以是一个 POJO 或者是一个 Map
     * @param fm
     *            指明可用字段，null 表示全部字段可用
     * @return Chain 对象，null 表示对象中没有可用字段
     * 
     * @see org.nutz.dao.FieldMatcher
     */
    public static Chain from(Object obj, FieldMatcher fm) {
        if (null == obj)
            return null;
        Chain c = null;
        /*
         * Is Map
         */
        if (obj instanceof Map<?, ?>) {
            for (Map.Entry<?, ?> en : ((Map<?, ?>) obj).entrySet()) {
                Object key = en.getKey();
                if (null == key)
                    continue;
                String name = key.toString();
                if (null != fm && !fm.match(name))
                    continue;
                Object v = en.getValue();
                if (null != fm && null == v && fm.isIgnoreNull())
                    continue;
                if (c == null) {
                    c = Chain.make(name, v);
                } else {
                    c = c.add(name, v);
                }
            }
        }
        /*
         * Is POJO
         */
        else {
            Mirror<?> mirror = Mirror.me(obj.getClass());
            for (Field f : mirror.getFields()) {
                if (null != fm && !fm.match(f.getName()))
                    continue;
                Object v = mirror.getValue(obj, f.getName());
                if (null != fm && null == v && fm.isIgnoreNull())
                    continue;
                if (c == null) {
                    c = Chain.make(f.getName(), v);
                } else {
                    c = c.add(f.getName(), v);
                }
            }
        }
        return c;
    }

    /**
     * 根据一个 POJO 对象的字段 生成一个 Chain 对象
     * <p>
     * 相当于 Chain.from(obj,null)
     * 
     * @param obj
     *            POJO 对象
     * @return Chain 对象
     */
    public static Chain from(Object obj) {
        return from(obj, null);
    }
    
    //=============================================================
    //===========update语句使用特定的值,例如+1 -1 toDate()等========
    //=============================================================
    
    /**
     * 添加一个特殊节点, 如果value非空,则有3个情况:<p>
     * <li>+1 效果如age=age+1</li>
     * <li>-1 效果如count=count-1</li>
     * <li>其他值, 则对value.toString(),效果如 time=todate("XXXXX")</li>
     * 
     * @since 1.b.44
     */
    public abstract Chain addSpecial(String name, Object value);
    
    /**
     * @see org.nutz.dao.Chain#addSpecial(String, Object)
     * @since 1.b.44
     */
    public static Chain makeSpecial(String name, Object value) {
        DefaultChain chain = new DefaultChain(name, value);
        chain.head.special = true;
        return chain;
    }
    
    private static class DefaultChain extends Chain {
        private Entry head;
        private Entry current;
        private Entry tail;
        private int size;
        
        public DefaultChain(String name, Object value) {
            
            this.head = new Entry(name, value);
            this.current = head;
            this.tail = head;
            this.size = 1;
        }
        public int size() {
            return size;
        }
        public Chain name(String name) {
            current.name = name;
            return this;
        }
        public Chain value(Object value) {
            current.value = value;
            return this;
        }
        public Chain adaptor(ValueAdaptor adaptor) {
            current.adaptor = adaptor;
            return this;
        }
        public ValueAdaptor adaptor() {
            return current.adaptor;
        }
        public Chain add(String name, Object value) {
            tail.next = new Entry(name, value);
            tail = tail.next;
            size ++;
            return this;
        }
        public String name() {
            return current.name;
        }
        public Object value() {
            return current.value;
        }
        public Chain next() {
            current = current.next;
            return current == null ? null : this;
        }
        public Chain head() {
            current = head;
            return this;
        }
        public Chain addSpecial(String name, Object value) {
            add(name, value);
            tail.special = true;
            return this;
        }
        public boolean special() {
            return current.special;
        }
        public boolean isSpecial() {
            Entry entry = head;
            do {
                if(entry.special) return true;
            } while ((entry = entry.next) != null);
            return false;
        }
        public Map<String, Object> toMap() {
            Map<String, Object> map = new HashMap<String, Object>();
            Entry current = head;
            while (current != null) {
                map.put(current.name, current.value);
                current = current.next;
            }
            return map;
        }
        public Chain updateBy(Entity<?> entity) {
            if (null != entity) {
                Entry current = head;
                while (current != null) {
                    MappingField ef = entity.getField(current.name);
                    if (null != ef) {
                        current.name = ef.getColumnName();
                    }
                    current = current.next;
                }
            }
            return head();
        }
        public <T> T toObject(Class<T> classOfT) {
            Mirror<T> mirror = Mirror.me(classOfT);
            T re = mirror.born();
            Entry current = head;
            while (current != null) {
                mirror.setValue(re, current.name, current.value);
                current = current.next;
            }
            return re;
        }
        
        private static class Entry {
            protected String name;
            Object value;
            ValueAdaptor adaptor;
            boolean special;
            Entry next;
            public Entry(String name, Object value) {
                this.name = name;
                this.value = value;
            }
        }
    }
}
