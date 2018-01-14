package org.nutz.dao.util.tables;

import org.nutz.dao.entity.annotation.Table;

/**
 * 通过Daos辅助函数自动创建表时,对不需要自动创建得表进行过滤
 *
 * @author threefish(306955302@qq.com)
 */
public interface TablesFilter {

    /**
     * 效验表信息判断是否执行过滤
     * @param klass
     *            实体类
     * @param table
     *            实体类的@Table信息
     * @return
     */
    boolean match(Class<?> klass,Table table);
}
