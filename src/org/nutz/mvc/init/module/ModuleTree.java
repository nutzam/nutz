package org.nutz.mvc.init.module;

import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.List;

import org.nutz.ioc.Ioc;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.Context;
import org.nutz.mvc.ActionInvoker;
import org.nutz.mvc.ViewMaker;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.Encoding;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Filters;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.init.PathNode;
import org.nutz.mvc.invoker.ActionInvokerImpl2;

/**
 * 模块树
 * @author juqkai(juqkai@gmail.com)
 *
 */
public abstract class ModuleTree<T extends AnnotatedElement> {

	protected T module;
	protected List<ModuleTree<?>> child;
	protected ModuleTree<?> father;
	protected Ioc ioc;
	protected Context context;
	
	protected Ok ok;
	protected Fail fail;
	protected AdaptBy adaptBy;
	protected Filters filters;
	protected Encoding encoding;
	
	public ModuleTree(Context context, T module, ModuleTree<?> father, Ioc ioc){
		this.context = context;
		this.module = module;
		this.father = father;
		this.ioc = ioc;
		AnnotatedElement ae = (AnnotatedElement) module;
		this.ok = ae.getAnnotation(Ok.class);
		this.fail = ae.getAnnotation(Fail.class);
		this.adaptBy = ae.getAnnotation(AdaptBy.class);
		this.filters = ae.getAnnotation(Filters.class);
		this.encoding = ae.getAnnotation(Encoding.class);
		
	}
	/**
	 * 扫描子模块
	 */
	public abstract void scan();
	
	/**
	 * 添加子模块
	 * @param mt
	 */
	public void addChild(ModuleTree<?> mt){
		if(child == null){
			child = new ArrayList<ModuleTree<?>>();
		}
		child.add(mt);
		mt.scan();
	}
	
	/**
	 * 将模块结构解析成URI->Invoker对，保存到root中
	 * @param root
	 * @throws Throwable 
	 */
	public abstract void parse(PathNode<ActionInvoker> root) throws Throwable;
	/**
	 * 初始化invoker
	 * @param invoker
	 * @throws Throwable
	 */
	protected abstract void initInvoker(ActionInvokerImpl2 invoker) throws Throwable;
	
	/**
	 * 取得入口函数的URI
	 * @return
	 */
	protected abstract String[] getPath();
	
	/**
	 * 取得视图工厂
	 * @return
	 * @throws Throwable
	 */
	protected List<ViewMaker> fetchMakers() throws Throwable{
		return father.fetchMakers();
	}
	
	/**
	 * 取得过滤器
	 * @return
	 */
	protected Filters fetchFilters(){
		if(filters == null){
			if(father == null){
				return null;
			}
			return father.fetchFilters();
		}
		return filters;
	}
	
	protected AdaptBy fetchAdaptBy(){
		if(adaptBy == null){
			if(father == null){
				return null;
			}
			return father.fetchAdaptBy();
		}
		return adaptBy;
	}
	
	protected Ok fetchOk(){
		if(ok == null){
			if(father == null){
				return null;
			}
			return father.fetchOk();
		}
		return ok;
	}
	protected Fail fetchFail(){
		if(fail == null){
			if(father == null){
				return null;
			}
			return father.fetchFail();
		}
		return fail;
	}
	
	protected Encoding fetchEncoding(){
		if(encoding == null){
			if(father == null){
				return null;
			}
			return father.fetchEncoding();
		}
		return encoding;
	}

	protected static final String getExceptionMessage(Throwable e) {
		e = Lang.unwrapThrow(e);
		return Strings.isBlank(e.getMessage()) ? e.getClass().getSimpleName()
				: e.getMessage();
	}
	
}
