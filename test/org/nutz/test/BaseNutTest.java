package org.nutz.test;

import java.lang.reflect.Field;

import org.junit.After;
import org.junit.Before;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.IocLoader;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.combo.ComboIocLoader;
import org.nutz.lang.Strings;
import org.nutz.mvc.annotation.IocBy;

/**
 * Nutz测试基类<p/>
 * 用法: 继承本类,覆盖getMainModule或getIocConfigure方法(二选一)
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class BaseNutTest {
    
    /**
     * 持有Ioc容器,避免被GC, 及完成测试后需要关闭ioc容器
     */
    protected Ioc ioc;

    /**
     * 初始化Ioc容器
     * @throws Exception 初始化过程出错的话抛错
     */
    @Before
    public void init() throws Exception {
        ioc = new NutIoc(getIocLoader()); // 生成Ioc容器
        _init_fields(); // 注入自身字段
        _init(); // 执行用户自定义初始化过程
    }
    
    /**
     * 用户自定义初始化过程, 在ioc容器初始化完成后及本对象的属性注入完成后执行
     */
    public void _init() throws Exception {}
    
    /**
     * 用户自定义销毁过程, 在ioc容器销毁前执行
     */
    public void _depose() throws Exception {}
    
    /**
     * 遍历当前对象中的属性,如果标注了@Inject则从ioc容器取出对象,注入进去
     * @throws Exception 注入过程中如果抛出异常
     */
    public void _init_fields() throws Exception {
        Field[] fields = getClass().getDeclaredFields();
        for (Field field : fields) {
            Inject inject = field.getAnnotation(Inject.class);
            if (inject == null)
                continue;
            String val = inject.value();
            Object v = null;
            if (Strings.isBlank(val)) {
                v = ioc.get(field.getType(), field.getName());
            } else {
                if (val.startsWith("refer:"))
                    val = val.substring("refer:".length());
                v = ioc.get(field.getType(), val);
            }
            field.setAccessible(true);
            field.set(this, v);
        }
    }
    
    /**
     * 获取IocLoader,默认是ComboIocLoader实例, 子类可以自定义
     */
    protected IocLoader getIocLoader() throws Exception {
        return new ComboIocLoader(getIocConfigure());
    }
    
    /**
     * 子类可覆盖本方法,以配置项目的MainModule,可选项
     */
    protected Class<?> getMainModule() throws Exception  {
        return null;
    }
    
    /**
     * 子类可覆盖本方法,以配置项目的ioc配置,可选项
     */
    protected String[] getIocConfigure() throws Exception  {
        Class<?> klass = getMainModule();
        if (klass == null)
            return new String[]{};
        IocBy iocBy = klass.getAnnotation(IocBy.class);
        if (iocBy == null)
            return new String[]{};
        return iocBy.args();
    }
    
    @After
    public void depose() throws Exception  {
        try {
            _depose();
        } finally {
            if (ioc != null)
                ioc.depose();
        }
    }
}
