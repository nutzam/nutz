package org.nutz.service;

import org.nutz.dao.Dao;

/**
 * 抽象的Service类. <b>辅助类,并非强制要求继承</b><p/>
 * <b>子类不应该也切勿再声明一个dao属性,以避免重复的属性,导致Ioc注入时混乱</b>
 * @author wendal(wendal1985@gmail.com)
 *
 */
public abstract class Service {

    /**
     * 新建Service,仍需要在调用setDao传入Dao实例才算完整
     */
    public Service() {}

    /**
     * 新建Service并同时传入Dao实例
     * @param dao Dao实例,不应该为null
     */
    public Service(Dao dao) {
        this.dao = dao;
    }

    private Dao dao;

    /**
     * 设置Dao实例
     * @param dao
     */
    public void setDao(Dao dao) {
        this.dao = dao;
    }

    /**
     * 获取Dao实例
     * @return Dao 实例
     */
    public Dao dao() {
        return dao;
    }

}
