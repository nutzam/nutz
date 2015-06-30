package org.nutz.service;

import java.util.List;

import org.nutz.dao.Chain;
import org.nutz.dao.Condition;
import org.nutz.dao.Dao;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.pager.Pager;
import org.nutz.lang.Each;
import org.nutz.lang.Mirror;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * 实体Service抽象类. 属于辅助类. 任何方法被调用前,必须确保Dao实例已经传入
 * @author wendal(wendal1985@gmail.com)
 *
 * @param <T> 实体类型
 */
public abstract class EntityService<T> extends Service {

    private Mirror<T> mirror;

    private static final Log log = Logs.get();

    /**
     * 本抽象类能提供一些帮助方法,减少重复写实体类型的麻烦
     */
    @SuppressWarnings("unchecked")
    public EntityService() {
        try {
            Class<T> entryClass = (Class<T>) Mirror.getTypeParam(getClass(), 0);
            mirror = Mirror.me(entryClass);
            if (log.isDebugEnabled())
                log.debugf("Get TypeParams for self : %s", entryClass.getName());
        }
        catch (Throwable e) {
            if (log.isWarnEnabled())
                log.warn("!!!Fail to get TypeParams for self!", e);
        }
    }

    /**
     * 新建并传入Dao实例
     * @param dao Dao实例
     */
    public EntityService(Dao dao) {
        this();
        this.setDao(dao);
    }

    /**
     * 新建并传入Dao实例,同时指定实体类型
     * @param dao Dao实例
     * @param entityType 实体类型
     */
    public EntityService(Dao dao, Class<T> entityType) {
        setEntityType(entityType);
        setDao(dao);
    }

    /**
     * 获取实体类型的反射封装类实例
     * @return 反射封装类实例
     */
    public Mirror<T> mirror() {
        return mirror;
    }

    /**
     * 设置新的实体类型, 极少调用
     * @param classOfT
     */
    @SuppressWarnings("unchecked")
    public <C extends T> void setEntityType(Class<C> classOfT) {
        mirror = (Mirror<T>) Mirror.me(classOfT);
    }

    /**
     * 获取实体的Entity
     * @return 实体的Entity
     */
    public Entity<T> getEntity() {
        return dao().getEntity(mirror.getType());
    }

    /**
     * 获取实体类型
     * @return 实体类型
     */
    public Class<T> getEntityClass() {
        return mirror.getType();
    }

    /**
     * 批量删除
     * @param cnd 条件
     * @return 删除的条数
     */
    public int clear(Condition cnd) {
        return dao().clear(getEntityClass(), cnd);
    }

    /**
     * 全表删除
     * @return 删除的条数
     */
    public int clear() {
        return dao().clear(getEntityClass(), null);
    }

    /**
     * 根据条件分页查询
     * @param cnd 查询条件
     * @param pager 分页
     * @return 查询结果
     */
    public List<T> query(Condition cnd, Pager pager) {
        return (List<T>) dao().query(getEntityClass(), cnd, pager);
    }

    /**
     * 遍历条件分页查询结果
     * @param cnd 查询条件
     * @param pager 分页
     * @param callback 遍历回调
     * @return 遍历的总条数
     */
    public int each(Condition cnd, Pager pager, Each<T> callback) {
        return dao().each(getEntityClass(), cnd, pager, callback);
    }

    /**
     * 根据条件统计符合条件的记录数
     * @param cnd 查询条件
     * @return 记录数
     */
    public int count(Condition cnd) {
        return dao().count(getEntityClass(), cnd);
    }

    /**
     * 全表的总记录数
     * @return 总记录数
     */
    public int count() {
        return dao().count(getEntityClass());
    }

    /**
     * 查出符合条件的第一条记录
     * @param cnd 查询条件
     * @return 实体,如不存在则为null
     */
    public T fetch(Condition cnd) {
        return dao().fetch(getEntityClass(), cnd);
    }

    /**
     * 复合主键专用
     * 
     * @param pks
     *            键值
     * @return 对象 T
     */
    public T fetchx(Object... pks) {
        return dao().fetchx(getEntityClass(), pks);
    }

    /**
     * 复合主键专用
     * 
     * @param pks
     *            键值
     * @return 对象 T
     */
    public boolean exists(Object... pks) {
        return null != fetchx(pks);
    }

    /**
     * 批量更新
     * @param chain 设置值的键值对
     * @param cnd 需要更新的条件语句
     */
    public int update(Chain chain, Condition cnd) {
        return dao().update(getEntityClass(), chain, cnd);
    }

    /**
     * 更新@ManyMany关联表中的数据
     * @param regex 关联字段的匹配正则表达式,如果为null则代表全部
     * @param chain 键值对
     * @param cnd 条件语句
     */
    public int updateRelation(String regex, Chain chain, Condition cnd) {
        return dao().updateRelation(getEntityClass(), regex, chain, cnd);
    }

    /**
     * 根据复合主键删除记录
     * @param pks 复合主键,必须按@Pk的声明顺序传入
     * @return 删除的记录数
     */
    public int deletex(Object... pks) {
        return dao().deletex(getEntityClass(), pks);
    }
}
