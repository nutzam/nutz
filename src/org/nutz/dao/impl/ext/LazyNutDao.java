package org.nutz.dao.impl.ext;

import javax.sql.DataSource;

import org.nutz.aop.AopCallback;
import org.nutz.dao.SqlManager;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.EntityMaker;
import org.nutz.dao.impl.EntityHolder;
import org.nutz.dao.impl.NutDao;

/**
 * 支持简单的懒加载机制的NutDao
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class LazyNutDao extends NutDao {
    
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
        return new LazyAnnotationEntityMaker(dataSource, expert, holder, this);
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
}
