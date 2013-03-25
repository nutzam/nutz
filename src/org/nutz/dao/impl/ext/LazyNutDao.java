package org.nutz.dao.impl.ext;

import javax.sql.DataSource;

import org.nutz.aop.AopCallback;
import org.nutz.dao.SqlManager;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.EntityMaker;
import org.nutz.dao.impl.EntityHolder;
import org.nutz.dao.impl.NutDao;

/**
 * 支持简单的懒加载机制的NutDao<p/>
 * <b>注意: 如果存在双向关联,且你打算使用基于getter/setter的序列化工具来序列化这些对象,那么必须设置cycle=false,使关联对象的字段使用普通加载,而非懒加载</b>
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class LazyNutDao extends NutDao {
	
	private boolean cycle = true;
    
    public void setDataSource(DataSource ds) {
        super.setDataSource(ds);
        this.holder = new EntityHolder(this) {
            @SuppressWarnings("unchecked")
            public <T> Entity<T> getEntity(Class<T> classOfT) {
                if (AopCallback.class.isAssignableFrom(classOfT))
                    return (Entity<T>) getEntity(classOfT.getSuperclass());
                return super.getEntity(classOfT);
            }
        };
        this.holder.maker = createEntityMaker();
    }

    protected EntityMaker createEntityMaker() {
    	if (cycle)
    		return new LazyAnnotationEntityMaker(dataSource, expert, holder, this);
        return new LazyAnnotationEntityMaker(dataSource, expert, holder, new NutDao(dataSource));
    }
    
    public LazyNutDao() {
        super();
    }

    public LazyNutDao(DataSource dataSource) {
        super(dataSource);
    }

    public LazyNutDao(DataSource dataSource, SqlManager sqlManager) {
        super(dataSource, sqlManager);
    }
    
    public void setCycle(boolean cycle) {
    	if (this.cycle != cycle) {
    		this.cycle = cycle;
    		this.holder.maker = createEntityMaker();
    	}
	}
}
