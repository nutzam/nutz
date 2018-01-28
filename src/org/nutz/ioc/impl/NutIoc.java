package org.nutz.ioc.impl;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.Ioc2;
import org.nutz.ioc.IocContext;
import org.nutz.ioc.IocEventListener;
import org.nutz.ioc.IocException;
import org.nutz.ioc.IocLoader;
import org.nutz.ioc.IocLoading;
import org.nutz.ioc.IocMaking;
import org.nutz.ioc.ObjectMaker;
import org.nutz.ioc.ObjectProxy;
import org.nutz.ioc.ValueProxyMaker;
import org.nutz.ioc.annotation.InjectName;
import org.nutz.ioc.aop.MirrorFactory;
import org.nutz.ioc.aop.impl.DefaultMirrorFactory;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.ioc.loader.combo.ComboIocLoader;
import org.nutz.ioc.meta.IocObject;
import org.nutz.lang.Strings;
import org.nutz.lang.Times;
import org.nutz.lang.util.LifeCycle;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.repo.LevenshteinDistance;

/**
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 */
public class NutIoc implements Ioc2 {

    private static final Log log = Logs.get();

    private Object lock_get = new Object();

    private static final String DEF_SCOPE = "app";

    protected Date createTime;

    /**
     * 读取配置文件的 Loader
     */
    private ComboIocLoader loader;
    /**
     * 缓存对象上下文环境
     */
    private IocContext context;
    /**
     * 装配对象的逻辑
     */
    private ObjectMaker maker;
    /**
     * 可扩展的"字段值"生成器
     */
    private List<ValueProxyMaker> vpms;
    /**
     * 反射工厂，封装 AOP 的逻辑
     */
    private MirrorFactory mirrors;
    /**
     * 对象默认生命周期范围名
     */
    private String defaultScope;
    /**
     * <ul>
     * <li>缓存支持的对象值类型
     * <li>如果 addValueProxyMaker() 被调用，这个缓存会被清空
     * <li>createLoading() 将会检查这个缓存
     * </ul>
     */
    private Set<String> supportedTypes;
    
    protected List<IocEventListener> listeners;
    
    protected ThreadLocal<Object> listenerH = new ThreadLocal<Object>();

    public NutIoc(IocLoader loader) {
        this(loader, new ScopeContext(DEF_SCOPE), DEF_SCOPE);
    }

    public NutIoc(IocLoader loader, IocContext context, String defaultScope) {
        this(new ObjectMakerImpl(), loader, context, defaultScope);
    }

    protected NutIoc(ObjectMaker maker, IocLoader loader, IocContext context, String defaultScope) {
        this(maker, loader, context, defaultScope, null);
    }

    protected NutIoc(ObjectMaker maker,
                     IocLoader loader,
                     IocContext context,
                     String defaultScope,
                     MirrorFactory mirrors) {
        this.createTime = new Date();
        this.maker = maker;
        this.defaultScope = defaultScope;
        this.context = context;
        if (loader instanceof ComboIocLoader)
            this.loader = (ComboIocLoader) loader;
        else
            this.loader = new ComboIocLoader(loader);
        vpms = new ArrayList<ValueProxyMaker>(5); // 预留五个位置，足够了吧
        addValueProxyMaker(new DefaultValueProxyMaker());

        // 初始化类工厂， 这是同 AOP 的连接点
        if (mirrors == null)
            this.mirrors = new DefaultMirrorFactory(this);
        else
            this.mirrors = mirrors;
        try {
            this.loader.init();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        log.info("... NutIoc init complete");
    }

    /**
     * @return 一个新创建的 IocLoading 对象
     */
    protected IocLoading createLoading() {
        if (null == supportedTypes) {
            synchronized (this) {
                if (null == supportedTypes) {
                    // TODO 看看可不可以改成更快的 Set
                    supportedTypes = new HashSet<String>();
                    for (ValueProxyMaker maker : vpms) {
                        String[] ss = maker.supportedTypes();
                        if (ss != null)
                            for (String s : ss)
                                supportedTypes.add(s);
                    }
                }
            }
        }
        return new IocLoading(supportedTypes);
    }

    public <T> T get(Class<T> type) throws IocException {
        InjectName inm = type.getAnnotation(InjectName.class);
        if (null != inm && (!Strings.isBlank(inm.value())))
            return get(type, inm.value());
        IocBean iocBean = type.getAnnotation(IocBean.class);
        if (iocBean != null && (!Strings.isBlank(iocBean.name())))
            return get(type, iocBean.name());
        return get(type, Strings.lowerFirst(type.getSimpleName()));
    }

    public <T> T get(Class<T> type, String name, IocContext context) throws IocException {
        if (log.isDebugEnabled())
            log.debugf("Get '%s'<%s>", name, type == null ? "" : type);
        try {
            if (this.mirrors instanceof LifeCycle)
                ((LifeCycle) this.mirrors).init();
        }
        catch (Exception e) {
            throw new IocException("_mirror_factory_init", e, "Mirror Factory init fail");
        }

        // 创建对象创建时
        IocMaking ing = makeIocMaking(context, name);
        IocContext cntx = ing.getContext();

        // 从上下文缓存中获取对象代理
        ObjectProxy op = cntx.fetch(name);

        // 如果未发现对象
        if (null == op) {
            // 线程同步
            synchronized (lock_get) {
                // 再次读取
                op = cntx.fetch(name);
                // 如果未发现对象
                if (null == op) {
                    try {
                        if (log.isDebugEnabled())
                            log.debug("\t >> Load definition name=" + name);

                        // 读取对象定义
                        IocObject iobj = loader.load(createLoading(), name);
                        if (null == iobj) {
                            for (String iocBeanName : loader.getName()) {
                                // 相似性少于3 --> 大小写错误,1-2个字符调换顺序或写错
                                // 感觉没必要..没有就没有呗
                                if (3 > LevenshteinDistance.computeLevenshteinDistance(name.toLowerCase(),
                                                                                       iocBeanName.toLowerCase())) {
                                    throw new IocException(name,
                                                           "Undefined object '%s' but found similar name '%s'",
                                                           name,
                                                           iocBeanName);
                                }
                            }
                            throw new IocException(name, "Undefined object '%s'", name);
                        }

                        // 修正对象类型
                        if (null == iobj.getType())
                            if (null == type && Strings.isBlank(iobj.getFactory()))
                                throw new IocException(name, "NULL TYPE object '%s'", name);
                            else
                                iobj.setType(type);
                        // 检查对象级别
                        if (Strings.isBlank(iobj.getScope()))
                            iobj.setScope(defaultScope);

                        // 根据对象定义，创建对象，maker 会自动的缓存对象到 context 中
                        if (log.isDebugEnabled())
                            log.debugf("\t >> Make...'%s'<%s>", name, type == null ? "" : type);
                        if (iobj.getType() != null && IocEventListener.class.isAssignableFrom(iobj.getType())) {
                            if (listenerH.get() != null) {
                                op = maker.make(ing, iobj);
                            }
                            else {
                                try {
                                    listenerH.set(Boolean.TRUE);
                                    op = maker.make(ing, iobj);
                                }
                                finally {
                                    listenerH.remove();
                                }
                            }
                        }
                        else {
                            _checkIocEventListeners();
                            ing.setListeners(listeners);
                            op = maker.make(ing, iobj);
                        }
                    }
                    // 处理异常
                    catch (IocException e) {
                        ((IocException) e).addBeanNames(name);
                        throw e;
                    }
                    catch (Throwable e) {
                        throw new IocException(name,
                                               e,
                                               "For object [%s] - type:[%s]",
                                               name,
                                               type == null ? "" : type);
                    }
                }
            }
        }

        synchronized (lock_get) {
            T re = op.get(type, ing);

            if (!name.startsWith("$") && re instanceof IocLoader) {
                loader.addLoader((IocLoader) re);
            }
            return re;
        }
    }

    public <T> T get(Class<T> type, String name) {
        return this.get(type, name, null);
    }

    public boolean has(String name) {
        return loader.has(name) || context.fetch(name) != null;
    }

    private boolean deposed = false;

    public void depose() {
        if (deposed) {
            if (log.isInfoEnabled())
                log.info("You can't depose a Ioc twice!");
            return;
        }
        if (log.isInfoEnabled())
            log.infof("%s@%s is closing. startup date [%s]",
                      getClass().getName(),
                      hashCode(),
                      Times.sDTms2(this.createTime));
        try {
            this.loader.depose();
        }
        catch (Exception e) {
            log.warn("something happen when depose IocLoader", e);
        }
        context.depose();
        loader.clear();
        deposed = true;
        if (log.isInfoEnabled())
            log.infof("%s@%s is deposed. startup date [%s]",
                      getClass().getName(),
                      hashCode(),
                      Times.sDTms2(this.createTime));
    }

    public void reset() {
        context.clear();
    }

    public String[] getNames() {
        LinkedHashSet<String> list = new LinkedHashSet<String>();
        list.addAll(Arrays.asList(loader.getName()));
        if (context != null)
            list.addAll(context.names());
        return list.toArray(new String[list.size()]);
    }

    public void addValueProxyMaker(ValueProxyMaker vpm) {
        vpms.add(0, vpm);// 优先使用最后加入的ValueProxyMaker
        supportedTypes = null;
        loader.clear();
    }

    public IocContext getIocContext() {
        return context;
    }

    public void setMaker(ObjectMaker maker) {
        this.maker = maker;
    }

    public void setMirrorFactory(MirrorFactory mirrors) {
        this.mirrors = mirrors;
    }

    public void setDefaultScope(String defaultScope) {
        this.defaultScope = defaultScope;
    }

    /**
     * 暴露IocMaking的创建过程
     */
    public IocMaking makeIocMaking(IocContext context, String name) {
        // 连接上下文
        IocContext cntx;
        if (null == context || context == this.context)
            cntx = this.context;
        else {
            if (log.isTraceEnabled())
                log.trace("Link contexts");
            cntx = new ComboContext(context, this.context);
        }
        return new IocMaking(this, mirrors, cntx, maker, vpms, name);
    }

    @Override
    public String toString() {
        return "/*NutIoc*/\n{\nloader:" + loader + ",\n}";
    }

    @Override
    protected void finalize() throws Throwable {
        if (!deposed) {
            log.error("Ioc depose tigger by GC!!!\n"
                      + "Common Reason for that is YOUR code call 'new NutIoc(...)',"
                      + " and then get some beans(most is Dao) from it and abandon it!!!\n"
                      + "If using nutz.mvc, call Mvcs.ctx().getDefaultIoc() to get ioc container.\n"
                      + "Not nutz.mvc? use like this:     public static Ioc ioc;");
            depose();
        }
        super.finalize();
    }

    public String[] getNamesByType(Class<?> klass) {
        return this.getNamesByType(klass, null);
    }

    public String[] getNamesByType(Class<?> klass, IocContext context) {
        List<String> names = new ArrayList<String>(loader.getNamesByTypes(createLoading(), klass));
        IocContext cntx;
        if (null == context || context == this.context)
            cntx = this.context;
        else
            cntx = new ComboContext(context, this.context);
        for (String name : cntx.names()) {
            ObjectProxy op = cntx.fetch(name);
            if (op.getObj() != null && klass.isAssignableFrom(op.getObj().getClass()))
                names.add(name);
        }
        LinkedHashSet<String> re = new LinkedHashSet<String>();
        for (String name : names) {
            if (Strings.isBlank(name) || "null".equals(name))
                continue;
            re.add(name);
        }
        return re.toArray(new String[re.size()]);
    }
    
    public String[] getNamesByAnnotation(Class<? extends Annotation> klass) {
        return this.getNamesByAnnotation(klass, null);
    }

    public String[] getNamesByAnnotation(Class<? extends Annotation> klass, IocContext context) {
        List<String> names = new ArrayList<String>(loader.getNamesByAnnotation(createLoading(), klass));
        IocContext cntx;
        if (null == context || context == this.context)
            cntx = this.context;
        else
            cntx = new ComboContext(context, this.context);
        for (String name : cntx.names()) {
            ObjectProxy op = cntx.fetch(name);
            if (op.getObj() != null && klass.getAnnotation(klass) != null)
                names.add(name);
        }
        LinkedHashSet<String> re = new LinkedHashSet<String>();
        for (String name : names) {
            if (Strings.isBlank(name) || "null".equals(name))
                continue;
            re.add(name);
        }
        return re.toArray(new String[re.size()]);
    }

    public <K> K getByType(Class<K> klass) {
        return this.getByType(klass, null);
    }

    public <K> K getByType(Class<K> klass, IocContext context) {
        String _name = null;
        IocContext cntx;
        if (null == context || context == this.context)
            cntx = this.context;
        else
            cntx = new ComboContext(context, this.context);
        for (String name : cntx.names()) {
            ObjectProxy op = cntx.fetch(name);
            if (op.getObj() != null && klass.isAssignableFrom(op.getObj().getClass())) {
                _name = name;
                break;
            }
        }
        if (_name != null)
            return get(klass, _name, context);
        for (String name : getNames()) {
            try {
                IocObject iobj = loader.load(createLoading(), name);
                if (iobj != null
                    && iobj.getType() != null
                    && klass.isAssignableFrom(iobj.getType()))
                    _name = name;
            }
            catch (Exception e) {
                continue;
            }
            if (_name != null)
                return get(klass, name, context);
        }
        throw new IocException("class:"
                               + klass.getName(),
                               "none ioc bean match class=" + klass.getName());
    }
    
    protected void _checkIocEventListeners() {
        if (listeners != null)
            return;
        List<IocEventListener> listeners = new ArrayList<IocEventListener>();
        for (String beanName : this.loader.getNamesByTypes(createLoading(), IocEventListener.class)) {
            listeners.add(get(IocEventListener.class, beanName));
        }
        if (listeners.size() > 0) {
            Collections.sort(listeners, new Comparator<IocEventListener>() {
                public int compare(IocEventListener prev, IocEventListener next) {
                    if (prev.getOrder() == next.getOrder())
                        return 0;
                    return prev.getOrder() > next.getOrder() ? -1 : 1;
                }
            });
        }
        this.listeners = listeners;
    }

    public Ioc addBean(String name, Object obj) {
        if (obj == null)
            throw new RuntimeException("can't add bean=null!!");
        if (Strings.isBlank(name))
            throw new RuntimeException("can't add bean name is blank!!");
        if (obj instanceof ObjectProxy)
            getIocContext().save("app", name, (ObjectProxy)obj);
        else
            getIocContext().save("app", name, new ObjectProxy(obj));
        return this;
    }
}
