package org.nutz.dao.entity;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import org.nutz.dao.FieldMatcher;
import org.nutz.dao.sql.Pojo;
import org.nutz.lang.Mirror;
import org.nutz.lang.util.Context;

/**
 * 描述了一个实体
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface Entity<T> {

    /**
     * @return 实体的 Java 类型`
     */
    Class<T> getType();

    /**
     * @return 实体的 Java 类型
     */
    Mirror<T> getMirror();

    /**
     * @return 实体索引列表
     */
    List<EntityIndex> getIndexes();

    /**
     * 获取实体的表名
     * 
     * @return 实体表名
     */
    String getTableName();

    /**
     * 获取实体视图名
     * 
     * @return 实体视图名
     */
    String getViewName();

    /**
     * 按名称获取一个实体的索引
     * 
     * @param name
     *            索引名称
     * @return 实体索引
     */
    EntityIndex getIndex(String name);

    /**
     * 从结果集中生成一个实体实例
     * 
     * @param rs
     *            结果集
     * @param matcher
     *            字段匹配器。如果为null，则获取实体的全部字段
     * @return Java 对象
     */
    T getObject(ResultSet rs, FieldMatcher matcher);

    /**
     * 从一个记录中生成一个实体实例
     * 
     * @param rec
     *            结果集
     * 
     * @return Java 对象
     */
    T getObject(Record rec);

    /**
     * 根据实体的 Java 字段名获取一个实体字段对象
     * 
     * @param name
     *            实体字段的 Java 对象名
     * @return 实体字段
     */
    MappingField getField(String name);

    /**
     * 增加一个插入前字段宏
     * 
     * @param pojo
     *            Pojo 语句
     * @return 是否增加成功
     */
    boolean addBeforeInsertMacro(Pojo pojo);

    /**
     * 增加一个插入后字段宏
     * 
     * @param pojo
     *            Pojo 语句
     * @return 是否增加成功
     */
    boolean addAfterInsertMacro(Pojo pojo);

    /**
     * 获取实体所有自动执行的字段宏列表
     * <p>
     * 这些自动执行宏，在实体被插入到数据库前调用
     * <p>
     * 比如程序员可以为某个字段定义值的自动生成规则
     * 
     * @return 预执行宏列表
     */
    List<Pojo> cloneBeforeInsertMacroes();

    /**
     * 获取实体所有自动执行的字段宏列表
     * <p>
     * 这些自动执行宏，在实体被插入到数据库后调用
     * <p>
     * 比如程序员可以为数据库自动生成的字段获取生成后的值
     * 
     * @return 后执行字段宏列表
     */
    List<Pojo> cloneAfterInsertMacroes();

    /**
     * 根据实体的数据库字段名获取一个实体字段对象
     * 
     * @param name
     *            实体字段数据库字段名
     * @return 实体字段
     */
    MappingField getColumn(String name);

    /**
     * @return 实体所有的映射字段
     */
    List<MappingField> getMappingFields();

    /**
     * 获取实体所有匹配上正则表达是的关联字段，如果正则表达是为 null，则表示获取全部关联字段
     * 
     * @param regex
     *            正则表达式
     * 
     * @return 实体所有匹配上正则表达是的关联字段
     */
    List<LinkField> getLinkFields(String regex);

    /**
     * 访问所有一对一映射。即 '@One' 声明的字段
     * 
     * @param obj
     *            映射的宿主对象
     * @param visitor
     *            处理器
     * @param regex
     *            正则表达式匹配 Java 字段名。null 表示匹配所有一对一映射字段
     * @return 匹配上的映射字段
     */
    List<LinkField> visitOne(Object obj, String regex, LinkVisitor visitor);

    /**
     * 访问所有一对多映射。即 '@Many' 声明的字段
     * 
     * @param obj
     *            映射的宿主对象
     * @param visitor
     *            处理器
     * @param regex
     *            正则表达式匹配 Java 字段名。null 表示匹配所有一对多映射字段
     * @return 匹配上的映射字段
     */
    List<LinkField> visitMany(Object obj, String regex, LinkVisitor visitor);

    /**
     * 访问所有多对多映射。即 '@ManyMany' 声明的字段
     * 
     * @param obj
     *            映射的宿主对象
     * @param visitor
     *            处理器
     * @param regex
     *            正则表达式匹配 Java 字段名。null 表示匹配所有多对多映射字段
     * @return 匹配上的映射字段
     */
    List<LinkField> visitManyMany(Object obj, String regex, LinkVisitor visitor);

    /**
     * 如果实体采用了复合主键，调用这个函数能返回所有的复合主键，顺序就是复合主键的顺序
     * <p>
     * 如果没有复合主键，那么将返回 null
     * 
     * @return 实体所复合主键字段
     */
    List<MappingField> getCompositePKFields();

    /**
     * @return 实体唯一字符类型主键
     */
    MappingField getNameField();

    /**
     * @return 实体唯一数字类型主键
     */
    MappingField getIdField();

    /**
     * 根据，"数字主键 > 字符主键 > 复合主键" 的优先顺序，返回主键列表
     * 
     * @return 实体的主键列表
     */
    List<MappingField> getPks();

    /**
     * @return 当前实体首选主键类型
     */
    PkType getPkType();

    /**
     * 将一个实体对象的实例包裹成 Context 接口
     * 
     * @param obj
     *            实体对象的实例
     * @return Context
     */
    Context wrapAsContext(Object obj);

    /**
     * 获取一个实体补充描述
     * 
     * @param key
     *            实体补充描述的键值
     * @return 实体补充描述的内容
     */
    Object getMeta(String key);

    /**
     * 实体是否包含某一种 meta
     * 
     * @param key
     *            meta 的键值
     * @return 是否包含
     */
    boolean hasMeta(String key);

    /**
     * @return 实体补充描述的集合
     */
    Map<String, Object> getMetas();

    /**
     * @return 表是否有注释
     */
    boolean hasTableComment();

    /**
     * @return 字段是否注释
     */
    boolean hasColumnComment();

    /**
     * @return 表注释
     */
    String getTableComment();

    /**
     * 根据字段名获得注释
     * 
     * @param columnName
     *            字段名称
     * @return 注释
     */
    String getColumnComent(String columnName);

}
