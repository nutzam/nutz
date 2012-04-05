package org.nutz.dao.impl.ext;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

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
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * 支持简单的懒加载机制的AnnotationEntityMaker
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
	
	class LazyNutEntity<T> extends NutEntity<T>{

		public LazyNutEntity(Class<T> type) {
			super(type);
		}
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public void init() {
			//仅支持通过默认构造进行构建的类, TODO 支持Result方式生成对象的构造方法
			if (this.bornByDefault != null) {
				//检查是否需要进行Lazy处理
				if (!ones.getAll().isEmpty() || !manys.getAll().isEmpty() || !manymanys.getAll().isEmpty()) {
					if (log.isDebugEnabled())
						log.debug("Found links , enable lazy!! -->" + type);
					try {
						Class klass = cd.load(type.getName() + ClassAgent.CLASSNAME_SUFFIX);
						this.bornByDefault = Mirror.me(klass).getBorning();
						return;
					}
					catch (ClassNotFoundException e) {}//事实上,这里肯定是ClassNotFound的
					
					Mirror<T> mirror = Mirror.me(type);
					List<InterceptorPair> interceptorPairs = new ArrayList<InterceptorPair>();
					List<LinkField> lfs = new ArrayList<LinkField>();
					lfs.addAll(ones.getAll());
					lfs.addAll(manys.getAll());
					lfs.addAll(manymanys.getAll());
					//准备拦截器
					for (LinkField lf : lfs) {
						String fieldName = lf.getName();
						try {
							Method setter = mirror.getSetter(mirror.getField(fieldName));
							LazyMethodInterceptor lmi = new LazyMethodInterceptor(setter, fieldName);
							interceptorPairs.add(new InterceptorPair(lmi, MethodMatcherFactory.matcher("^(get|set)"+Strings.capitalize(fieldName)+"$")));
						} catch (Throwable e) {
							if (log.isWarnEnabled())
								log.warn("Not setter found for LazyLoading ?!", e);
						}
					}
					//生成Aop化的类
					ClassAgent agent = new AsmClassAgent();
					for (InterceptorPair interceptorPair : interceptorPairs)
						agent.addInterceptor(	interceptorPair.getMethodMatcher(),
												interceptorPair.getMethodInterceptor());
					Class lazyClass = agent.define(cd, type);
					this.bornByDefault = Mirror.me(lazyClass).getBorning();
				} 
			}
		}
	}
	
	class LazyMethodInterceptor implements MethodInterceptor {

		private LazyStatus status = LazyStatus.CAN_FETCH;
		
		private Method setter;
		private String fieldName;
		
		public LazyMethodInterceptor(Method setter, String fieldName) {
			this.setter = setter;
			this.fieldName = fieldName;
		}

		
		public void filter(InterceptorChain chain) throws Throwable {
			if (status == LazyStatus.CAN_FETCH) {
				if (chain.getCallingMethod() != setter) {
					dao.fetchLinks(chain.getCallingObj(), fieldName);//这里会触发setter被调用
					status = LazyStatus.FETCHED;
				} else
					status = LazyStatus.NO_NEED; //如果setter被调用,那么也就不再需要懒加载了
			}
			chain.doChain();
		}
		
	}
	
	enum LazyStatus {
		CAN_FETCH,
		FETCHED,
		NO_NEED
	}
}
