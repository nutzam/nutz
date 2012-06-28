package org.nutz.dao.sql;

import java.util.List;

import org.nutz.dao.pager.Pager;

/**
 * 封装通过 POJO 生成的 SQL 语句
 * <p>
 * 各个数据库的处理类通过这个类能获得足够的信息来生产 SQL 语句
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface Pojo extends DaoStatement {

    /**
     * 设置语句执行前的操作
     * 
     * @param callback
     *            针对 POJO 语句的回调
     * @return 自身
     */
    Pojo setBefore(PojoCallback callback);

    /**
     * 设置语句执行后的操作
     * 
     * @param callback
     *            针对 POJO 语句的回调
     * @return 自身
     */
    Pojo setAfter(PojoCallback callback);

    /**
     * 为 POJO 语句设置分页对象
     * 
     * @param pager
     *            分页对象
     * @return 自身
     */
    Pojo setPager(Pager pager);

    /**
     * 通过普通Java对象为这个 POJO 语句的 SQL 参数赋值 <br>
     * 一个 POJO 实际上是一个 SQL 的语句模板，增加一个 Java 对象，实际上就是增加了一组参数 <br>
     * 因此如果你想为 POJO 里附加一个集合或者数组对象，对象必须是同样类型的，否则会出现不可预知的错误
     * <p>
     * 接口的实现类会根据你给出的对象类型不同，对你的对象做不通的解释，它考虑的方式为：
     * <ul>
     * <li>集合|数组 : 你打算增加一组对象作为本语句的参数，会为递归调用本函数
     * <li>迭带器(Iterator) : 你打算增加一组对象作为本语句的参数，会为递归调用本函数
     * <li>Map : 你打算增加一条记录（将键作为字段名）
     * <li>Chain : 你打算增加一条记录
     * <li>其他 : 你打算增加一条记录（将对象字段作为字段名）
     * </ul>
     * <p>
     * <b style="color:red">注意:</b><br>
     * 某些 SQL 语句（比如 CREATE|DROP）即使有参数，也是没有意义的，所以 Pojo 执行器会忽略它们
     * 
     * @param obj
     *            普通 Java 对象
     * 
     * @return 自身
     * 
     * @see org.nutz.dao.sql.Pojo#addParamsBy(Object)
     */
    Pojo addParamsBy(Object obj);

    /**
     * @return 语句最后一个参数行的参数对象
     */
    Object getLastParams();

    /**
     * 返回语句的参数表。
     * 
     * @return 语句的参数表
     */
    List<Object> params();

    /**
     * 一个 Pojo 语句正在操作的对象，就是你通过 Dao 接口传入的对象本身。
     * 
     * @return Pojo 正在操作的对象
     */
    Object getOperatingObject();

    /**
     * 设置一个 POJO 正在操作的对象
     * 
     * @param obj
     *            正在操作的对象
     */
    Pojo setOperatingObject(Object obj);

    /**
     * 清除已经存储的对象
     * 
     * @return 自身
     */
    Pojo clear();

    /**
     * 为POJO语句增加一个或多个语句元素
     * 
     * @param itemAry
     *            语句元素
     * @return 自身
     */
    Pojo append(PItem... itemAry);

    /**
     * 在 POJO 所有的语句前插入一组语句元素
     * 
     * @param itemAry
     *            语句元素
     * @return 自身
     */
    Pojo insertFirst(PItem... itemAry);

    /**
     * 置换 POJO 的一个语句元素
     * 
     * @param index
     *            位置下标
     * @param pi
     *            语句元素
     * @return 自身
     */
    Pojo setItem(int index, PItem pi);

    /**
     * 获取 POJO 的一个语句元素
     * 
     * @param index
     *            位置下标
     * @return 语句元素
     */
    PItem getItem(int index);

    /**
     * 删除 POJO 的一个语句元素
     * 
     * @param index
     *            位置下标
     * @return 自身
     */
    Pojo removeItem(int index);

    /**
     * 复制一份自己的实例
     * 
     * @return 一份新的自己
     */
    Pojo duplicate();

}
