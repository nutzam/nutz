package org.nutz.dao.impl.ext;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

import javax.sql.DataSource;

import org.nutz.aop.ClassAgent;
import org.nutz.aop.ClassDefiner;
import org.nutz.aop.DefaultClassDefiner;
import org.nutz.aop.InterceptorChain;
import org.nutz.aop.MethodInterceptor;
import org.nutz.aop.asm.AsmClassAgent;
import org.nutz.aop.matcher.MethodMatcherFactory;
import org.nutz.dao.Dao;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.LinkField;
import org.nutz.dao.impl.EntityHolder;
import org.nutz.dao.impl.entity.AnnotationEntityMaker;
import org.nutz.dao.impl.entity.NutEntity;
import org.nutz.dao.jdbc.JdbcExpert;
import org.nutz.ioc.aop.config.InterceptorPair;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.lang.born.BornContext;
import org.nutz.lang.born.Borns;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * 支持简单的懒加载机制的AnnotationEntityMaker
 * 
 * @author wendal(wendal1985@gmail.com)
 * 
 */
public class LazyAnnotationEntityMaker extends AnnotationEntityMaker {

    private static final Log log = Logs.get();

    private Dao dao;

    private ClassDefiner cd;

    public LazyAnnotationEntityMaker(DataSource datasource, JdbcExpert expert,
            EntityHolder holder, Dao dao) {
        super(datasource, expert, holder);
        this.dao = dao;
        cd = new DefaultClassDefiner(getClass().getClassLoader());
    }

    protected <T> NutEntity<T> _createNutEntity(Class<T> type) {
        return new LazyNutEntity<T>(type);
    }

    @Override
    public <T> Entity<T> make(Class<T> type) {
        LazyNutEntity<T> en = (LazyNutEntity<T>) super.make(type);
        en.init();
        return en;
    }

    class LazyNutEntity<T> extends NutEntity<T> {

        public LazyNutEntity(Class<T> type) {
            super(type);
        }

        @SuppressWarnings({ "rawtypes", "unchecked" })
        public void init() {
            List<LinkField> lfs = new ArrayList<LinkField>();
            lfs.addAll(ones.getAll());
            lfs.addAll(manys.getAll());
            lfs.addAll(manymanys.getAll());
            if (lfs.isEmpty())
                return;
            if (log.isDebugEnabled())
                log.debug("Found links , enable lazy!! -->" + type);

            Mirror<T> mirror = Mirror.me(type);
            List<InterceptorPair> interceptorPairs = new ArrayList<InterceptorPair>();
            // 准备拦截器
            for (LinkField lf : lfs) {
                String fieldName = lf.getName();
                try {
                    Method setter = mirror
                            .getSetter(mirror.getField(fieldName));
                    LazyMethodInterceptor lmi = new LazyMethodInterceptor(
                            setter, fieldName);
                    interceptorPairs.add(new InterceptorPair(lmi,
                            MethodMatcherFactory.matcher("^(get|set)"
                                    + Strings.capitalize(fieldName) + "$")));
                } catch (Throwable e) {
                    if (log.isWarnEnabled())
                        log.warn("Not setter found for LazyLoading ?!", e);
                }
            }
            // 生成Aop化的类
            ClassAgent agent = new AsmClassAgent();
            for (InterceptorPair interceptorPair : interceptorPairs)
                agent.addInterceptor(interceptorPair.getMethodMatcher(),
                        interceptorPair.getMethodInterceptor());
            Class lazyClass = agent.define(cd, type);

            // 检查对象的创建方法
            BornContext<T> bc = Borns.evalByArgTypes(type, ResultSet.class);
            if (null == bc)
                this.bornByDefault = Mirror.me(lazyClass)
                        .getBorningByArgTypes();
            else
                this.bornByRS = bc.getBorning();
        }
    }

    class LazyMethodInterceptor implements MethodInterceptor {

        private WeakHashMap<Object, LazyStatus> status = new WeakHashMap<Object, LazyAnnotationEntityMaker.LazyStatus>();

        private Method setter;
        private String fieldName;

        public LazyMethodInterceptor(Method setter, String fieldName) {
            this.setter = setter;
            this.fieldName = fieldName;
        }

        public void filter(InterceptorChain chain) throws Throwable {
            LazyStatus stat = status.get(chain.getCallingObj());
            if (stat == null) {
                if (chain.getCallingMethod() != setter)
                    dao.fetchLinks(chain.getCallingObj(), fieldName);// 这里会触发setter被调用
                status.put(chain.getCallingObj(), LazyStatus.NO_NEED); // 如果setter被调用,那么也就不再需要懒加载了
            }
            chain.doChain();
        }

    }

    enum LazyStatus {
        CAN_FETCH, FETCHED, NO_NEED
    }
}
