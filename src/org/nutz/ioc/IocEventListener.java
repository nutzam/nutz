package org.nutz.ioc;

public interface IocEventListener {

    /**
     * 对象已创建后,属性未注入
     * @param obj 新建好的对象
     * @param beanName 对象的名称
     * @return 可以是新对象,也可以是被替换的新对象
     */
    Object afterBorn(Object obj, String beanName);
    
    /**
     * 对象已创建,属性已经注入,准备返回给调用方法
     * @param obj 新建好的对象
     * @param beanName 对象的名称
     * @return 可以是新对象,也可以是被替换的新对象
     */
    Object afterCreate(Object obj, String beanName);
    
//    /**
//     * 对象调用depose方法后
//     * @param obj 新建好的对象
//     * @param beanName 对象的名称
//     */
//    void afterDepose(Object obj, String beanName);
    
    int getOrder();
}
