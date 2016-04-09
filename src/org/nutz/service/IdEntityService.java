package org.nutz.service;

import java.sql.ResultSet;
import java.util.List;

import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Condition;
import org.nutz.dao.Dao;
import org.nutz.dao.FieldMatcher;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.EntityField;
import org.nutz.dao.pager.Pager;
import org.nutz.lang.Each;

/**
 * 针对仅标注了@Id的实体的Service
 * @author wendal(wendal1985@gmail.com)
 *
 * @param <T> 实体的类型
 */
public abstract class IdEntityService<T> extends EntityService<T> {

    /**
     * @see EntityService
     */
    public IdEntityService() {
        super();
    }

    /**
     * @see EntityService
     */
    public IdEntityService(Dao dao) {
        super(dao);
    }

    /**
     * @see EntityService
     */
    public IdEntityService(Dao dao, Class<T> entityType) {
        super(dao, entityType);
    }

    /**
     * 根据@Id所在的属性的值获取一个实体对象
     * @param id 属性的值
     * @return 实体对象,如不存在则返回null
     */
    public T fetch(long id) {
        return dao().fetch(getEntityClass(), id);
    }
    
    /**
     * 根据@Id所在的属性的值删除一个实体对象
     * @param id 属性的值
     * @return 删除的记录数, 通常是0或者1
     */
    public int delete(long id) {
        return dao().delete(getEntityClass(), id);
    }

    /**
     * 根据@Id所在的属性在数据库中的最大值
     * @return 最大值,若数据库中没有数据,会抛出空指针异常
     */
    public int getMaxId() {
        return dao().getMaxId(getEntityClass());
    }

    /**
     * 是否存在@Id所在属性的值为指定值的记录
     * @param id 属性的值
     * @return true,如果存在的话
     */
    public boolean exists(long id) {
        EntityField ef = getEntity().getIdField();
        if (null == ef)
            return false;
        return dao().count(getEntityClass(), Cnd.where(ef.getName(), "=", id)) > 0;
    }
 
	/**
	 * 该类型对象是否在数据库中存在数据表
	 * @return true,如果存在的话
	 */
	public boolean exists() {
		return dao().exists(getEntityClass());
	}
	
    /**
     * 如果一个数据表存在，移除它
     * @return 是否移除成功
     */
	public boolean drop() {
		return dao().drop(getEntityClass());
	}
	
	/**
	 * 随便获取一个对象。某些时候，你的数据表永远只有一条记录，这个操作就很适合你
	 * @return 对象本身
	 */
	public T fetch() {
		return dao().fetch(getEntityClass());
	}
    
    /**
     * 根据一个实体的配置信息为其创建一张表
     * @param dropIfExists 如果表存在是否强制移除
     * @return 实体对象
     */
    public Entity<T> create(boolean dropIfExists) {
		return dao().create(getEntityClass(), dropIfExists);
	}
  
    /**
	 * 与 insert(String tableName, Chain chain) 一样，不过，数据表名，将取自 POJO 的数据表声明，请参看
	 * '@Table' 注解的详细说明
	 * @param chain 数据名值链
	 */
	public void insert(Chain chain) {
		dao().insert(getEntityClass(), chain);
	}
	
	/**
	 * 查询一组对象。你可以为这次查询设定条件
	 * @param cnd WHERE 条件。如果为 null，将获取全部数据，顺序为数据库原生顺序<br>
	 * 			    只有在调用这个函数的时候， cnd.limit 才会生效
	 * @return 对象列表
	 */
	public List<T> query(Condition cnd) {
		return dao().query(getEntityClass(), cnd);
	}
	
	/**
	 * 对一组对象进行迭代，这个接口函数非常适用于很大的数据量的集合，因为你不可能把他们都读到内存里
	 * @param cnd WHERE 条件。如果为 null，将获取全部数据，顺序为数据库原生顺序
	 * @param callback 处理回调
	 * @return 一共迭代的数量
	 */
	public int each(Condition cnd, Each<T> callback) {
		return dao().each(getEntityClass(), cnd, callback);
	}
	
	/**
	 * 对某一个对象字段，进行计算。
	 * @param funcName 计算函数名，请确保你的数据库是支持这个函数的
	 * @param fieldName 对象 java 字段名
	 * @return 计算结果
	 */
	public int func(String funcName, String fieldName) {
		return dao().func(getEntityClass(), funcName, fieldName);
	}
	
	/**
	 * 对某一个对象字段，进行计算。
	 * @param funcName 计算函数名，请确保你的数据库是支持这个函数的
	 * @param fieldName 对象 java 字段名
	 * @param cnd 过滤条件
	 * @return 计算结果
	 */
	public int func(String funcName, String fieldName, Condition cnd) {
		return dao().func(getEntityClass(), funcName, fieldName, cnd);
	}
	
	/**
	 * 从一个 ResultSet 中获取一个对象。
	 * <p>
	 * 因为 Dao 接口可以知道一个 POJO 的映射细节，这个函数可以帮你节省一点体力。
	 * @param rs 结果集
	 * @param fm 字段过滤器
	 * @return 对象
	 */
	public T getObject(ResultSet rs, FieldMatcher fm) {
		return dao().getObject(getEntityClass(), rs, fm);
	}
	
}
