package org.nutz.dao;

import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.Record;
import org.nutz.dao.impl.EntityHolder;
import org.nutz.dao.jdbc.JdbcExpert;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.PojoMaker;
import org.nutz.dao.sql.Sql;
import org.nutz.lang.Each;

import java.sql.ResultSet;
import java.util.List;

/**
 * Nutz.Dao 核心接口。 封装了所有的数据库操作
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface Dao {

    /**
     * @return 数据源的元数据
     */
    DatabaseMeta meta();

    /**
     * @return 一个 Sql 管理接口，你可以通过这个接口管理你自定义的 SQL
     * 
     * @see org.nutz.dao.SqlManager
     */
    SqlManager sqls();

    PojoMaker pojoMaker();

    /**
     * 执行一组 Sql，这些 Sql 将会一起被提交
     * 
     * @param sqls
     *            要被执行的 Sql 数组
     */
    void execute(Sql... sqls);

    /**
     * 这个方法试图给你最大的灵活性，因为你的 ConnCallback 实现类将得到一个 Connection 接口
     * 的实例。请注意，你不需要关闭这个连接，这个函数在退出时会替你关闭连接。
     * <p>
     * 如果你从当前连接对象中创建了ResultSet对象或者 Statement对象，请自行关闭。Nutz.Dao 的原则是：
     * <ul>
     * <li>Nutz.Dao 创建维护的东西 Nutz.Dao 来维护其生命周期
     * <li>你创建的东西 （比如 ResultSet） 你来维护其生命周期
     * </ul>
     * 
     * @param callback
     */
    void run(ConnCallback callback);

    /**
     * 从一个 ResultSet 中获取一个对象。
     * <p>
     * 因为 Dao 接口可以知道一个 POJO 的映射细节，这个函数可以帮你节省一点体力。
     * 
     * @param classOfT
     *            对象类型
     * @param rs
     *            结果集
     * @param fm
     *            字段过滤器
     * @return 对象
     */
    <T> T getObject(Class<T> classOfT, ResultSet rs, FieldMatcher fm);

    <T> T getObject(Class<T> classOfT, ResultSet rs, FieldMatcher fm, String prefix);

    /**
     * 将一个对象插入到一个数据源。
     * <p>
     * 声明了 '@Id'的字段会在插入数据库时被忽略，因为数据库会自动为其设值。如果想手动设置，请设置 '@Id(auto=false)'
     * <p>
     * <b>插入之前</b>，会检查声明了 '@Default(@SQL("SELECT ..."))' 的字段，预先执行 SQL 为字段设置。
     * <p>
     * <b>插入之后</b>，会检查声明了 '@Next(@SQL("SELECT ..."))' 的字段，通过执行 SQL 将值取回
     * <p>
     * 如果你的字段仅仅声明了 '@Id(auto=true)'，没有声明 '@Next'，则认为你还是想取回插入后最新的 ID 值，因为
     * 自动为你添加类似 @Next(@SQL("SELECT MAX(id) FROM tableName")) 的设置
     * 
     * 
     * 
     * @param obj
     *            要被插入的对象
     *            <p>
     *            它可以是：
     *            <ul>
     *            <li>普通 POJO
     *            <li>集合
     *            <li>数组
     *            <li>Map
     *            </ul>
     *            <b style=color:red>注意：</b> 如果是集合，数组或者 Map，所有的对象必须类型相同，否则可能会出错
     * 
     * @return 插入后的对象
     * 
     * @see org.nutz.dao.entity.annotation.Id
     * @see org.nutz.dao.entity.annotation.Default
     * @see org.nutz.dao.entity.annotation.Next
     */
    <T> T insert(T obj);

    /**
     * 将一个对象按FieldFilter过滤后,插入到一个数据源。
     * <p/>
     * <code>dao.insert(pet, FieldFilter.create(Pet.class, FieldMatcher.create(false)));</code>
     * 
     * @param obj
     *            要被插入的对象
     * @param filter
     *            字段过滤器, 其中FieldMatcher.isIgnoreId生效
     * @return 插入后的对象
     * @see org.nutz.dao.Dao#insert(Object)
     */
    <T> T insert(T obj, FieldFilter filter);
    
    <T> T insert(T obj, String actived);

    /**
     * 自由的向一个数据表插入一条数据。数据用名值链描述
     * 
     * @param tableName
     *            数据表名
     * @param chain
     *            数据名值链
     */
    void insert(String tableName, Chain chain);

    /**
     * 与 insert(String tableName, Chain chain) 一样，不过，数据表名，将取自 POJO 的数据表声明，请参看
     * '@Table' 注解的详细说明
     * 
     * @param classOfT
     *            实体类型
     * @param chain
     *            数据名值链
     * 
     * @see org.nutz.dao.entity.annotation.Table
     */
    void insert(Class<?> classOfT, Chain chain);

    /**
     * 快速插入一个对象。 对象的 '@Prev' 以及 '@Next' 在这个函数里不起作用。
     * <p>
     * 即，你必须为其设置好值，它会统一采用 batch 的方法插入
     * 
     * @param obj
     *            要被插入的对象
     *            <p>
     *            它可以是：
     *            <ul>
     *            <li>普通 POJO
     *            <li>集合
     *            <li>数组
     *            <li>Map
     *            </ul>
     *            <b style=color:red>注意：</b> 如果是集合，数组或者 Map，所有的对象必须类型相同，否则可能会出错
     * 
     */
    <T> T fastInsert(T obj);

    /**
     * 将对象插入数据库同时，也将符合一个正则表达式的所有关联字段关联的对象统统插入相应的数据库
     * <p>
     * 关于关联字段更多信息，请参看 '@One' | '@Many' | '@ManyMany' 更多的描述
     * 
     * @param obj
     *            数据对象
     * @param regex
     *            正则表达式，描述了什么样的关联字段将被关注。如果为 null，则表示全部的关联字段都会被插入
     * @return 数据对象本身
     * 
     * @see org.nutz.dao.entity.annotation.One
     * @see org.nutz.dao.entity.annotation.Many
     * @see org.nutz.dao.entity.annotation.ManyMany
     */
    <T> T insertWith(T obj, String regex);

    /**
     * 根据一个正则表达式，仅将对象所有的关联字段插入到数据库中，并不包括对象本身
     * 
     * @param obj
     *            数据对象
     * @param regex
     *            正则表达式，描述了什么样的关联字段将被关注。如果为 null，则表示全部的关联字段都会被插入
     * @return 数据对象本身
     * 
     * @see org.nutz.dao.entity.annotation.One
     * @see org.nutz.dao.entity.annotation.Many
     * @see org.nutz.dao.entity.annotation.ManyMany
     */
    <T> T insertLinks(T obj, String regex);

    /**
     * 将对象的一个或者多个，多对多的关联信息，插入数据表
     * 
     * @param obj
     *            对象
     * @param regex
     *            正则表达式，描述了那种多对多关联字段将被执行该操作
     * 
     * @return 对象自身
     * 
     * @see org.nutz.dao.entity.annotation.ManyMany
     */
    <T> T insertRelation(T obj, String regex);

    /**
     * 更新一个对象。对象必须有 '@Id' 或者 '@Name' 或者 '@PK' 声明。
     * <p>
     * 并且调用这个函数前， 主键的值必须保证是有效，否则会更新失败
     * <p>
     * 这个对象所有的字段都会被更新，即，所有的没有被设值的字段，都会被置成 NULL，如果遇到 NOT NULL 约束，则会引发异常。
     * 如果想有选择的更新个别字段，请使用 org.nutz.dao.FieldFilter
     * <p>
     * 如果仅仅想忽略所有的 null 字段，请使用 updateIgnoreNull 方法更新对象
     * 
     * @param obj
     *            要被更新的对象
     *            <p>
     *            它可以是：
     *            <ul>
     *            <li>普通 POJO
     *            <li>集合
     *            <li>数组
     *            <li>Map
     *            </ul>
     *            <b style=color:red>注意：</b> 如果是集合，数组或者 Map，所有的对象必须类型相同，否则可能会出错
     * 
     * @return 返回实际被更新的记录条数，一般的情况下，如果更新成功，返回 1，否则，返回 0
     * 
     * @see org.nutz.dao.FieldFilter
     */
    int update(Object obj);

    /**
     * 更新对象一部分字段
     * 
     * @param obj
     *            对象
     * @param actived
     *            正则表达式描述要被更新的字段
     * @return 返回实际被更新的记录条数，一般的情况下，如果更新成功，返回 1，否则，返回 0
     */
    int update(Object obj, String actived);

    /**
     * 更新对象一部分字段
     * 
     * @param obj
     *            对象
     * @param actived
     *            正则表达式描述要被更新的字段
     * @return 返回实际被更新的记录条数，一般的情况下，如果更新成功，返回 1，否则，返回 0
     */
    int update(Object obj, String actived, String locked, boolean ignoreNull);

    int update(Object obj, FieldFilter fieldFilter);

    int update(Object obj, FieldFilter fieldFilter, Condition cnd);

    int update(Object obj, Condition cnd);

    /**
     * 更新一个对象，并且忽略所有 null 字段。
     * <p>
     * 注意: 基本数据类型都是不可能为null的,这些字段肯定会更新
     * 
     * @param obj
     *            要被更新的对象
     *            <p>
     *            它可以是：
     *            <ul>
     *            <li>普通 POJO
     *            <li>集合
     *            <li>数组
     *            <li>Map
     *            </ul>
     *            <b style=color:red>注意：</b> 如果是集合，数组或者 Map，所有的对象必须类型相同，否则可能会出错
     * 
     * @return 返回实际被更新的记录条数，一般的情况下，如果是单一Pojo,更新成功，返回 1，否则，返回 0
     */
    int updateIgnoreNull(Object obj);

    /**
     * 自由的更新多条数据
     * 
     * @param tableName
     *            数据表名
     * @param chain
     *            数据名值链。
     * @param cnd
     *            WHERE 条件
     * 
     * @return 有多少条记录被更新了
     */
    int update(String tableName, Chain chain, Condition cnd);

    /**
     * 与 update(String tableName, Chain chain, Condition cnd) 一样，不过，数据表名，将取自
     * POJO 的数据表声明，请参看 '@Table' 注解的详细说明
     * 
     * @param classOfT
     *            实体类型
     * @param chain
     *            数据名值链
     * @param cnd
     *            WHERE 条件
     * 
     * @return 有多少条记录被更新了
     * 
     * @see org.nutz.dao.entity.annotation.Table
     */
    int update(Class<?> classOfT, Chain chain, Condition cnd);

    /**
     * 将对象更新的同时，也将符合一个正则表达式的所有关联字段关联的对象统统更新
     * <p>
     * 关于关联字段更多信息，请参看 '@One' | '@Many' | '@ManyMany' 更多的描述
     * 
     * @param obj
     *            数据对象
     * @param regex
     *            正则表达式，描述了什么样的关联字段将被关注。如果为 null，则表示全部的关联字段都会被更新
     * @return 数据对象本身
     * 
     * @see org.nutz.dao.entity.annotation.One
     * @see org.nutz.dao.entity.annotation.Many
     * @see org.nutz.dao.entity.annotation.ManyMany
     */
    <T> T updateWith(T obj, String regex);

    /**
     * 根据一个正则表达式，仅更新对象所有的关联字段，并不包括对象本身
     * 
     * @param obj
     *            数据对象
     * @param regex
     *            正则表达式，描述了什么样的关联字段将被关注。如果为 null，则表示全部的关联字段都会被更新
     * @return 数据对象本身
     * 
     * @see org.nutz.dao.entity.annotation.One
     * @see org.nutz.dao.entity.annotation.Many
     * @see org.nutz.dao.entity.annotation.ManyMany
     */
    <T> T updateLinks(T obj, String regex);

    /**
     * 多对多关联是通过一个中间表将两条数据表记录关联起来。
     * <p>
     * 而这个中间表可能还有其他的字段，比如描述关联的权重等
     * <p>
     * 这个操作可以让你一次更新某一个对象中多个多对多关联的数据
     * 
     * @param classOfT
     *            对象类型
     * @param regex
     *            正则表达式，描述了那种多对多关联字段将被执行该操作
     * @param chain
     *            针对中间关联表的名值链。
     * @param cnd
     *            针对中间关联表的 WHERE 条件
     * 
     * @return 共有多少条数据被更新
     * 
     * @see org.nutz.dao.entity.annotation.ManyMany
     */
    int updateRelation(Class<?> classOfT, String regex, Chain chain, Condition cnd);

    <T> List<T> query(Class<T> classOfT, Condition cnd, Pager pager, FieldMatcher matcher);

    <T> List<T> query(Class<T> classOfT, Condition cnd, Pager pager, String regex);

    /**
     * 查询一组对象。你可以为这次查询设定条件，并且只获取一部分对象（翻页）
     * 
     * @param classOfT
     *            对象类型
     * @param cnd
     *            WHERE 条件。如果为 null，将获取全部数据，顺序为数据库原生顺序
     * @param pager
     *            翻页信息。如果为 null，则一次全部返回. 不会使用cnd中的pager!!!
     * @return 对象列表
     */
    <T> List<T> query(Class<T> classOfT, Condition cnd, Pager pager);

    /**
     * 查询一组对象。你可以为这次查询设定条件
     * 
     * @param classOfT
     *            对象类型
     * @param cnd
     *            WHERE 条件。如果为 null，将获取全部数据，顺序为数据库原生顺序<br>
     *            只有在调用这个函数的时候， cnd.limit 才会生效
     * @return 对象列表
     */
    <T> List<T> query(Class<T> classOfT, Condition cnd);

    /**
     * 查询出一组记录。
     * 
     * @param tableName
     *            表名 - 格式为 <b>tableName[:idName]</b> 比如 ： <b>t_pet</b> 或者
     *            <b>t_pet:id</b> 尤其在 SqlServer2005 的环境下，需要用 t_pet:id 的形式来指明 ID
     *            字段，否则 不能分页
     * @param cnd
     *            条件 - <b style=color:red>请注意：</b> 你传入的 Criteria 实现必须考虑到 没有
     *            'Entity<?>' 传入。即 toSql 函数的参数永远为 null。
     * @param pager
     *            翻页信息
     * @return Record 对象。实际上是一个 Map 的包裹类
     * 
     * @see org.nutz.dao.Condition
     */
    List<Record> query(String tableName, Condition cnd, Pager pager);

    /**
     * 查询出一组记录。
     * 
     * @param tableName
     *            表名 - 格式为 <b>tableName[:idName]</b> 比如 ： <b>t_pet</b> 或者
     *            <b>t_pet:id</b> 尤其在 SqlServer2005 的环境下，需要用 t_pet:id 的形式来指明 ID
     *            字段，否则 不能分页
     * @param cnd
     *            条件 - <b style=color:red>请注意：</b> 你传入的 Criteria 实现必须考虑到 没有
     *            'Entity<?>' 传入。即 toSql 函数的参数永远为 null。
     * @param pager
     *            翻页信息
     * @param fields
     *            需要查询的列, 将直接拼入SQL中.由于不会提前探知表结构,所以这fields并非正则表达式,务必留意.
     * @return 数据库记录
     */
    List<Record> query(String tableName, Condition cnd, Pager pager, String fields);

    /**
     * 查询出一组记录。
     * 
     * @param tableName
     *            表名 - 格式为 <b>tableName[:idName]</b> 比如 ： <b>t_pet</b> 或者
     *            <b>t_pet:id</b> 尤其在 SqlServer2005 的环境下，需要用 t_pet:id 的形式来指明 ID
     *            字段，否则 不能分页
     * @param cnd
     *            条件 - <b style=color:red>请注意：</b> 你传入的 Criteria 实现必须考虑到 没有
     *            'Entity<?>' 传入。即 toSql 函数的参数永远为 null。
     * @return Record 对象。实际上是一个 Map 的包裹类
     * 
     * @see org.nutz.dao.Condition
     */
    List<Record> query(String tableName, Condition cnd);

    /**
     * 对一组对象进行迭代，这个接口函数非常适用于很大的数据量的集合，因为你不可能把他们都读到内存里
     * 
     * @param classOfT
     *            对象类型
     * @param cnd
     *            WHERE 条件。如果为 null，将获取全部数据，顺序为数据库原生顺序
     * @param pager
     *            翻页信息。如果为 null，则一次全部返回
     * @param callback
     *            处理回调
     * @return 一共迭代的数量
     */
    <T> int each(Class<T> classOfT, Condition cnd, Pager pager, Each<T> callback);

    /**
     * 对一组对象进行迭代，这个接口函数非常适用于很大的数据量的集合，因为你不可能把他们都读到内存里
     * 
     * @param classOfT
     *            对象类型
     * @param cnd
     *            WHERE 条件。如果为 null，将获取全部数据，顺序为数据库原生顺序
     * @param callback
     *            处理回调
     * @return 一共迭代的数量
     */
    <T> int each(Class<T> classOfT, Condition cnd, Each<T> callback);

    /**
     * 对一组对象进行迭代，这个接口函数非常适用于很大的数据量的集合，因为你不可能把他们都读到内存里
     * 
     * @param tableName
     *            表名 - 格式为 <b>tableName[:idName]</b> 比如 ： <b>t_pet</b> 或者
     *            <b>t_pet:id</b> 尤其在 SqlServer2005 的环境下，需要用 t_pet:id 的形式来指明 ID
     *            字段，否则 不能分页
     * @param cnd
     *            WHERE 条件。如果为 null，将获取全部数据，顺序为数据库原生顺序
     * @param pager
     *            翻页信息。如果为 null，则一次全部返回
     * @param callback
     *            处理回调
     * @return 一共迭代的数量
     */
    int each(String tableName, Condition cnd, Pager pager, Each<Record> callback);

    /**
     * 对一组对象进行迭代，这个接口函数非常适用于很大的数据量的集合，因为你不可能把他们都读到内存里
     * 
     * @param tableName
     *            表名 - 格式为 <b>tableName[:idName]</b> 比如 ： <b>t_pet</b> 或者
     *            <b>t_pet:id</b> 尤其在 SqlServer2005 的环境下，需要用 t_pet:id 的形式来指明 ID
     *            字段，否则 不能分页
     * @param cnd
     *            WHERE 条件。如果为 null，将获取全部数据，顺序为数据库原生顺序
     * @param callback
     *            处理回调
     * @return 一共迭代的数量
     */
    int each(String tableName, Condition cnd, Each<Record> callback);

    /**
     * 对一组对象进行迭代，这个接口函数非常适用于很大的数据量的集合，因为你不可能把他们都读到内存里
     * 
     * @param tableName
     *            表名 - 格式为 <b>tableName[:idName]</b> 比如 ： <b>t_pet</b> 或者
     *            <b>t_pet:id</b> 尤其在 SqlServer2005 的环境下，需要用 t_pet:id 的形式来指明 ID
     *            字段，否则 不能分页
     * @param cnd
     *            WHERE 条件。如果为 null，将获取全部数据，顺序为数据库原生顺序
     * @param callback
     *            处理回调
     * @param fields
     *            需要查询的列, 将直接拼入SQL中.由于不会提前探知表结构,所以这fields并非正则表达式,务必留意.
     * @return 一共迭代的数量
     */
    int each(String tableName, Condition cnd, Pager pager, Each<Record> callback, String fields);

    /**
     * 根据对象 ID 删除一个对象。它只会删除这个对象，关联对象不会被删除。
     * <p>
     * 你的对象必须在某个字段声明了注解 '@Id'，否则本操作会抛出一个运行时异常
     * <p>
     * 如果你设定了外键约束，没有正确的清除关联对象会导致这个操作失败
     * 
     * 
     * @param classOfT
     *            对象类型
     * @param id
     *            对象 ID
     * 
     * @return 影响的行数
     * @see org.nutz.dao.entity.annotation.Id
     * 
     */
    int delete(Class<?> classOfT, long id);

    /**
     * 根据对象 Name 删除一个对象。它只会删除这个对象，关联对象不会被删除。
     * <p>
     * 你的对象必须在某个字段声明了注解 '@Name'，否则本操作会抛出一个运行时异常
     * <p>
     * 如果你设定了外键约束，没有正确的清除关联对象会导致这个操作失败
     * 
     * @param classOfT
     *            对象类型
     * @param name
     *            对象 Name
     * 
     * @return 影响的行数
     * @see org.nutz.dao.entity.annotation.Name
     */
    int delete(Class<?> classOfT, String name);

    /**
     * 根据复合主键，删除一个对象。该对象必须声明 '@PK'，并且，给定的参数顺序 必须同 '@PK' 中声明的顺序一致，否则会产生不可预知的错误。
     * 
     * @param classOfT
     * @param pks
     *            复合主键需要的参数，必须同 '@PK'中声明的顺序一致
     */
    <T> int deletex(Class<T> classOfT, Object... pks);

    /**
     * 自动判断如何删除一个对象。
     * <p>
     * 如果声明了 '@Id' 则相当于 delete(Class<T>,long)<br>
     * 如果声明了 '@Name'，则相当于 delete(Class<T>,String)<br>
     * 如果声明了 '@PK'，则 deletex(Class<T>,Object ...)<br>
     * 如果没声明任何上面三个注解，则会抛出一个运行时异常
     * 
     * @param obj
     *            要被删除的对象
     */
    int delete(Object obj);

    /**
     * 将对象删除的同时，也将符合一个正则表达式的所有关联字段关联的对象统统删除
     * <p>
     * <b style=color:red>注意：</b>
     * <p>
     * Java 对象的字段会被保留，这里的删除，将只会删除数据库中的记录
     * <p>
     * 关于关联字段更多信息，请参看 '@One' | '@Many' | '@ManyMany' 更多的描述
     * 
     * @param obj
     *            数据对象
     * @param regex
     *            正则表达式，描述了什么样的关联字段将被关注。如果为 null，则表示全部的关联字段都会被删除
     * 
     * @return 被影响的记录行数
     * 
     * @see org.nutz.dao.entity.annotation.One
     * @see org.nutz.dao.entity.annotation.Many
     * @see org.nutz.dao.entity.annotation.ManyMany
     */
    int deleteWith(Object obj, String regex);

    /**
     * 根据一个正则表达式，仅删除对象所有的关联字段，并不包括对象本身。
     * <p>
     * <b style=color:red>注意：</b>
     * <p>
     * Java 对象的字段会被保留，这里的删除，将只会删除数据库中的记录
     * <p>
     * 关于关联字段更多信息，请参看 '@One' | '@Many' | '@ManyMany' 更多的描述
     * 
     * @param obj
     *            数据对象
     * @param regex
     *            正则表达式，描述了什么样的关联字段将被关注。如果为 null，则表示全部的关联字段都会被删除
     * 
     * @return 被影响的记录行数
     * 
     * @see org.nutz.dao.entity.annotation.One
     * @see org.nutz.dao.entity.annotation.Many
     * @see org.nutz.dao.entity.annotation.ManyMany
     */
    int deleteLinks(Object obj, String regex);

    /**
     * 根据对象 ID 获取一个对象。它只会获取这个对象，关联对象不会被获取。
     * <p>
     * 你的对象必须在某个字段声明了注解 '@Id'，否则本操作会抛出一个运行时异常
     * 
     * @param classOfT
     *            对象类型
     * @param id
     *            对象 ID
     * 
     * @see org.nutz.dao.entity.annotation.Id
     */
    <T> T fetch(Class<T> classOfT, long id);

    /**
     * 根据对象 Name 获取一个对象。它只会获取这个对象，关联对象不会被获取。
     * <p>
     * 你的对象必须在某个字段声明了注解 '@Name'，否则本操作会抛出一个运行时异常
     * 
     * @param classOfT
     *            对象类型
     * @param name
     *            对象 Name
     * @return 对象本身
     * 
     * @see org.nutz.dao.entity.annotation.Name
     */
    <T> T fetch(Class<T> classOfT, String name);

    /**
     * 根据复合主键，获取一个对象。该对象必须声明 '@PK'，并且，给定的参数顺序 必须同 '@PK' 中声明的顺序一致，否则会产生不可预知的错误。
     * 
     * @param classOfT
     * @param pks
     *            复合主键需要的参数，必须同 '@PK'中声明的顺序一致
     */
    <T> T fetchx(Class<T> classOfT, Object... pks);

    /**
     * 根据 WHERE 条件获取一个对象。如果有多个对象符合条件，将只获取 ResultSet 第一个记录
     * 
     * @param classOfT
     *            对象类型
     * @param cnd
     *            WHERE 条件
     * @return 对象本身
     * 
     * @see org.nutz.dao.Condition
     * @see org.nutz.dao.entity.annotation.Name
     */
    <T> T fetch(Class<T> classOfT, Condition cnd);

    /**
     * 根据条件获取一个 Record 对象
     * 
     * @param tableName
     *            表名
     * @param cnd
     *            条件
     * @return Record 对象
     */
    Record fetch(String tableName, Condition cnd);

    /**
     * 
     * @param tableName
     *            表名
     * @param cnd
     *            条件
     * @param fields
     *            需要select的属性, 不可以是null!!!
     * @return Record 对象
     */
    Record fetch(String tableName, Condition cnd, String fields);

    /**
     * 随便获取一个对象。某些时候，你的数据表永远只有一条记录，这个操作就很适合你
     * 
     * @param classOfT
     *            对象类型
     * @return 对象本身
     */
    <T> T fetch(Class<T> classOfT);

    /**
     * 根据一个参考对象自动判断如何获取一个对象。
     * <p>
     * 如果声明了 '@Id' 则相当于 fetch(Class<T>,long)<br>
     * 如果声明了 '@Name'，则相当于 fetch(Class<T>,String)<br>
     * 如果声明了 '@PK'，则 fetchx(Class<T>,Object ...)<br>
     * 如果没声明任何上面三个注解，则会抛出一个运行时异常
     * 
     * @param obj
     *            参考对象
     * 
     * @return 对象本身
     */
    <T> T fetch(T obj);

    /**
     * 根据一个正则表达式，获取对象所有的关联字段
     * 
     * @param obj
     *            数据对象,不可以是Class啊!!!传对象或集合啊!!!
     * @param regex
     *            正则表达式，描述了什么样的关联字段将被关注。如果为 null，则表示全部的关联字段都会被查询
     * @return 更新后的数据对象本身
     * 
     * @see org.nutz.dao.entity.annotation.One
     * @see org.nutz.dao.entity.annotation.Many
     * @see org.nutz.dao.entity.annotation.ManyMany
     */
    <T> T fetchLinks(T obj, String regex);

    /**
     * 根据一个正则表达式，获取对象所有的关联字段, 并按Condition进行数据过滤排序
     * <p/>
     * <b>严重提醒,当使用Condition进行数据过滤排序时,应当使regex只匹配特定的映射字段</b>
     * 
     * @param obj
     *            数据对象,可以是普通对象或集合,但不是类
     * @param regex
     *            正则表达式，描述了什么样的关联字段将被关注。如果为 null，则表示全部的关联字段都会被查询
     * @param cnd
     *            关联字段的过滤(排序,条件语句,分页等)
     * @return 传入的数据对象
     */
    <T> T fetchLinks(T obj, String regex, Condition cnd);

    /**
     * 根据一个 WHERE 条件，清除一组对象。只包括对象本身，不包括关联字段
     * 
     * @param classOfT
     *            对象类型
     * @param cnd
     *            查询条件，如果为 null，则全部清除
     * @return 影响的行数
     */
    int clear(Class<?> classOfT, Condition cnd);

    /**
     * 根据一个 WHERE 条件，清除一组记录
     * 
     * @param tableName
     * @param cnd
     * @return 影响的行数
     */
    int clear(String tableName, Condition cnd);

    /**
     * 清除对象所有的记录
     * 
     * @param classOfT
     *            对象类型
     * @return 影响的行数
     */
    int clear(Class<?> classOfT);

    /**
     * 清除数据表所有记录
     * 
     * @param tableName
     *            表名
     * @return 影响的行数
     */
    int clear(String tableName);

    /**
     * <pre>
     * It will delete @One @Many entity records
     * clear the @ManyMany relations
     * </pre>
     * 
     * @param obj
     * @param regex
     * @return
     */
    /**
     * 根据正则表达式，清除对象的关联。
     * <p>
     * 对于 '@One' 和 '@Many'，对应的记录将会删除<br>
     * 而 '@ManyMay' 对应的字段，只会清除关联表中的记录
     * 
     * @param obj
     *            数据对象
     * @param regex
     *            正则表达式，描述了什么样的关联字段将被关注。如果为 null，则表示全部的关联字段都会被清除
     * @return 数据对象本身
     */
    <T> T clearLinks(T obj, String regex);

    /**
     * 获取实体描述, 其中包含了Java Pojo<-->数据库的全部映射信息
     * 
     * @param classOfT
     *            对象类型
     * @return 实体描述
     */
    <T> Entity<T> getEntity(Class<T> classOfT);

    /**
     * 根据条件，计算某个对象在数据库中有多少条记录
     * 
     * @param classOfT
     *            对象类型
     * @param cnd
     *            WHERE 条件
     * @return 数量
     */
    int count(Class<?> classOfT, Condition cnd);

    /**
     * 计算某个对象在数据库中有多少条记录
     * 
     * @param classOfT
     *            对象类型
     * @return 数量
     */
    int count(Class<?> classOfT);

    /**
     * 根据条件，计算某个数据表或视图中有多少条记录
     * 
     * @param tableName
     *            表名
     * @param cnd
     *            WHERE 条件
     * @return 数量
     */
    int count(String tableName, Condition cnd);

    /**
     * 计算某个数据表或视图中有多少条记录
     * 
     * @param tableName
     *            表名
     * @return 数量
     */
    int count(String tableName);

    /**
     * 获取某个对象，最大的 ID 值。这个对象必须声明了 '@Id'
     * 
     * @param classOfT
     * @return 最大 ID 值
     */
    int getMaxId(Class<?> classOfT);

    /**
     * 对某一个对象字段，进行计算。
     * 
     * @param classOfT
     *            对象类型
     * @param funcName
     *            计算函数名，请确保你的数据库是支持这个函数的
     * @param fieldName
     *            对象 java 字段名
     * @return 计算结果
     */
    int func(Class<?> classOfT, String funcName, String fieldName);
    
    /**
     * 对某一个对象字段，进行计算。
     * 
     * @param classOfT
     *            对象类型
     * @param funcName
     *            计算函数名，请确保你的数据库是支持这个函数的
     * @param fieldName
     *            对象 java 字段名
     * @return 计算结果
     */
    Object func2(Class<?> classOfT, String funcName, String fieldName);

    /**
     * 对某一个数据表字段，进行计算。
     * 
     * @param tableName
     *            表名
     * @param funcName
     *            计算函数名，请确保你的数据是支持库这个函数的
     * @param colName
     *            数据库字段名
     * @return 计算结果
     */
    int func(String tableName, String funcName, String colName);

    /**
     * 对某一个数据表字段，进行计算。
     * 
     * @param tableName
     *            表名
     * @param funcName
     *            计算函数名，请确保你的数据是支持库这个函数的
     * @param colName
     *            数据库字段名
     * @return 计算结果
     */
    Object func2(String tableName, String funcName, String colName);

    /**
     * 对某一个对象字段，进行计算。
     * 
     * @param classOfT
     *            对象类型
     * @param funcName
     *            计算函数名，请确保你的数据库是支持这个函数的
     * @param fieldName
     *            对象 java 字段名
     * @param cnd
     *            过滤条件
     * @return 计算结果
     */
    int func(Class<?> classOfT, String funcName, String fieldName, Condition cnd);

    /**
     * 对某一个对象字段，进行计算。
     * 
     * @param classOfT
     *            对象类型
     * @param funcName
     *            计算函数名，请确保你的数据库是支持这个函数的
     * @param fieldName
     *            对象 java 字段名
     * @param cnd
     *            过滤条件
     * @return 计算结果
     */
    Object func2(Class<?> classOfT, String funcName, String fieldName, Condition cnd);

    /**
     * 对某一个数据表字段，进行计算。
     * 
     * @param tableName
     *            表名
     * @param funcName
     *            计算函数名，请确保你的数据库是支持这个函数的
     * @param colName
     *            数据库字段名
     * @param cnd
     *            过滤条件
     * @return 计算结果
     */
    int func(String tableName, String funcName, String colName, Condition cnd);

    /**
     * 对某一个数据表字段，进行计算。
     * 
     * @param tableName
     *            表名
     * @param funcName
     *            计算函数名，请确保你的数据库是支持这个函数的
     * @param colName
     *            数据库字段名
     * @param cnd
     *            过滤条件
     * @return 计算结果
     */
    Object func2(String tableName, String funcName, String colName, Condition cnd);

    /**
     * 根据数据源的类型，创建一个翻页对象
     * 
     * @param pageNumber
     *            第几页 ，从 1 开始。
     * @param pageSize
     *            每页可以有多少条记录
     * @return 翻页对象
     */
    Pager createPager(int pageNumber, int pageSize);

    /**
     * @param classOfT
     *            对象类型
     * @return 该类型对象是否在数据库中存在数据表
     */
    boolean exists(Class<?> classOfT);

    /**
     * @param tableName
     *            表名
     * @return 数据库中是否存在这张数据表
     */
    boolean exists(String tableName);

    /**
     * 根据一个实体的配置信息为其创建一张表
     * 
     * @param classOfT
     *            实体类型
     * @param dropIfExists
     *            如果表存在是否强制移除
     * @return 实体对象
     */
    <T> Entity<T> create(Class<T> classOfT, boolean dropIfExists);

    /**
     * 如果一个实体的数据表存在，移除它
     * 
     * @param classOfT
     *            实体类型
     * @return 是否移除成功
     */
    boolean drop(Class<?> classOfT);

    /**
     * 如果一个数据表存在，移除它
     * 
     * @param tableName
     *            表名
     * @return 是否移除成功
     */
    boolean drop(String tableName);

    /**
     * 执行单条自定义SQL
     * 
     * @param sql
     *            自定义SQL对象
     * @return 传入的SQL对象,方便链式调用
     */
    Sql execute(Sql sql);

    /**
     * 设置数据库方言.
     * 
     * @param obj
     *            可以是类名,JdbcExpert实例,或者数据库名称
     * @throws Exception
     *             类名不合法/不存在的时抛出异常
     */
    void setExpert(Object obj) throws Exception;

    /**
     * 获取数据库方言的实现实例
     * 
     * @return 数据库方言的实现实例
     */
    JdbcExpert getJdbcExpert();

    /**
     * 以特殊规则执行insert
     * 
     * @param t
     *            实例对象
     * @param ignoreNull
     *            忽略空值
     * @param ignoreZero
     *            忽略0值
     * @param ignoreBlankStr
     *            忽略空白字符串
     * @return 传入的实例变量
     */
    <T> T insert(T t, boolean ignoreNull, boolean ignoreZero, boolean ignoreBlankStr);

    /**
     * 根据对象的主键(@Id/@Name/@Pk)先查询, 如果存在就更新, 不存在就插入
     * 
     * @param t
     *            对象
     * @return 原对象
     */
    <T> T insertOrUpdate(T t);
    
    /**
     * 根据对象的主键(@Id/@Name/@Pk)先查询, 如果存在就更新, 不存在就插入
     * 
     * @param t
     *            对象
     * @param insertFieldFilter
     *            插入时的字段过滤, 可以是null
     * @param updateFieldFilter
     *            更新时的字段过滤,可以是null
     * @return 原对象
     */
    <T> T insertOrUpdate(T t, FieldFilter insertFieldFilter, FieldFilter updateFieldFilter);

    /**
     * 乐观锁, 以特定字段的值作为限制条件,更新对象,并自增该字段.
     * <p/>
     * 执行的sql如下:
     * <p/>
     * <code>update t_user set age=30, city="广州", version=version+1 where name="wendal" and version=124;</code>
     * 
     * @param obj
     *            需要更新的对象, 必须带@Id/@Name/@Pk中的其中一种.
     * @param fieldFilter
     *            需要过滤的属性. 若设置了哪些字段不更新,那务必确保过滤掉fieldName的字段
     * @param fieldName
     *            参考字段的Java属性名.默认是"version",可以是任意数值字段
     * @return 若更新成功,返回值大于0, 否则小于等于0
     */
    int updateAndIncrIfMatch(Object obj, FieldFilter fieldFilter, String fieldName);

    /**
     * 基于版本的更新，版本不一样无法更新到数据
     * @param obj 需要更新的对象, 必须有version属性
     * @return 若更新成功,大于0, 否则小于0
     */
    int updateWithVersion(Object obj);
    
    /**
     * 基于版本的更新，版本不一样无法更新到数据
     * @param obj 需要更新的对象, 必须有version属性
     * @param fieldFilter 需要过滤的字段设置
     * @return 若更新成功,大于0, 否则小于0
     */
    int updateWithVersion(Object obj, FieldFilter fieldFilter);
    
    /**
     * 根据查询条件获取一个对象.<b>注意: 条件语句需要加上表名!!!</b>
     * <p/>
     * 这个方法是让@One关联的属性,通过left join一次性取出. 与fetch+fetchLinks是等价的
     * @param classOfT 实体类
     * @param regex 需要过滤的关联属性,可以是null,取出全部关联属性.
     * @param cnd 查询条件,必须带表名!!!
     * @return 实体对象,符合regex的关联属性也会取出
     */
    <T> T fetchByJoin(Class<T> classOfT, String regex, Condition cnd);

    /**
     * 根据对象 ID 获取一个对象。它只会获取这个对象，关联对象不会被获取。
     * <p>
     * 你的对象必须在某个字段声明了注解 '@Id'，否则本操作会抛出一个运行时异常
     * <p/>
     * 这个方法是让@One关联的属性,通过left join一次性取出. 与fetch+fetchLinks是等价的
     * @param classOfT 实体类
     * @param regex 需要取出的关联属性,是正则表达式哦,匹配的是Java属性名
     * @param id 对象id
     * @return 实体
     */
    <T> T fetchByJoin(Class<T> classOfT, String regex, long id);
    
    /**
     * 根据对象 NAME 获取一个对象。它只会获取这个对象，关联对象不会被获取。
     * <p>
     * 你的对象必须在某个字段声明了注解 '@Name'，否则本操作会抛出一个运行时异常
     * <p/>
     * 这个方法是让@One关联的属性,通过left join一次性取出. 与fetch+fetchLinks是等价的
     * 
     * @param classOfT 实体类
     * @param regex 需要取出的关联属性,是正则表达式哦,匹配的是Java属性名
     * @param name 对象name
     * @return 实体
     */
    <T> T fetchByJoin(Class<T> classOfT, String regex, String name);
    
    /**
     * 根据查询条件获取所有对象.<b>注意: 条件语句需要加上表名!!!</b>
     * <p/>
     * 这个方法是让@One关联的属性,通过left join一次性取出. 与query+fetchLinks是等价的
     * @param classOfT 实体类
     * @param regex 需要过滤的关联属性,可以是null,取出全部关联属性.
     * @param cnd 查询条件,必须带表名!!!
     * @return 实体对象的列表,符合regex的关联属性也会取出
     */
    <T> List<T> queryByJoin(Class<T> classOfT, String regex, Condition cnd);
    
    /**
     * 根据查询条件获取分页对象.<b>注意: 条件语句需要加上表名!!!</b>
     * <p/>
     * 这个方法是让@One关联的属性,通过left join一次性取出. 与query+fetchLinks是等价的
     * @param classOfT 实体类
     * @param regex 需要过滤的关联属性,可以是null,取出全部关联属性.
     * @param cnd 查询条件,必须带表名!!!
     * @param pager 分页对象 <b>注意: 分页不要在cnd中传入!</b>
     * @return 实体对象的列表,符合regex的关联属性也会取出
     */
    <T> List<T> queryByJoin(Class<T> classOfT, String regex, Condition cnd, Pager pager);
    
    EntityHolder getEntityHolder();
}
