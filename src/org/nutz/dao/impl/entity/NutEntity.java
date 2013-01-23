package org.nutz.dao.impl.entity;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.dao.DaoException;
import org.nutz.dao.FieldMatcher;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.EntityIndex;
import org.nutz.dao.entity.LinkField;
import org.nutz.dao.entity.LinkVisitor;
import org.nutz.dao.entity.MappingField;
import org.nutz.dao.entity.PkType;
import org.nutz.dao.entity.Record;
import org.nutz.dao.sql.Pojo;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.born.BornContext;
import org.nutz.lang.born.Borning;
import org.nutz.lang.born.Borns;
import org.nutz.lang.util.Context;

/**
 * 记录一个实体
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class NutEntity<T> implements Entity<T> {

    /**
     * 按照 Java 字段名索引映射字段
     */
    private Map<String, MappingField> byJava;

    /**
     * 按照数据库字段名索引映射字段
     */
    private Map<String, MappingField> byDB;

    /**
     * 按照增加顺序，记录所有映射字段
     */
    private List<MappingField> fields;

    /**
     * 按照顺序存储实体所有的索引
     */
    private List<EntityIndex> indexes;

    /**
     * 实体索引按照名称方式的散列
     */
    private Map<String, EntityIndex> indexMap;

    /**
     * 按顺序记录复合主键
     */
    private List<MappingField> theComposites;

    /**
     * 预执行宏列表
     */
    private List<Pojo> beforeInsertMacroes;

    /**
     * 后执行字段宏列表
     */
    private List<Pojo> afterInsertMacroes;

    /**
     * 所有一对一映射字段
     */
    protected LinkFieldSet ones;

    /**
     * 所有一对多映射字段
     */
    protected LinkFieldSet manys;

    /**
     * 所有多对多映射字段
     */
    protected LinkFieldSet manymanys;

    /**
     * 数字型主键
     */
    private MappingField theId;

    /**
     * 字符型主键
     */
    private MappingField theName;

    /**
     * 实体 Java 类型
     */
    protected Class<T> type;

    /**
     * 实体 Java 类型：通过 Mirror 增强
     */
    private Mirror<T> mirror;

    /**
     * 根据 ResultSet 创建实体的方法
     */
    protected Borning<T> bornByRS;

    /**
     * 根据默认构造函数或者工厂方法创建实体的方法
     */
    protected Borning<T> bornByDefault;

    /**
     * 实体表名
     */
    private EntityName tableName;

    /**
     * 实体表注释
     */
    private String tableComment;

    /**
     * 字段注释
     */
    private Map<String, String> columnComments;

    /**
     * 是否有表注释
     */
    private boolean hasTableComment;

    /**
     * 是否有字段注释
     */
    private boolean hasColumnComment;

    /**
     * 实体视图名
     */
    private EntityName viewName;

    /**
     * 实体补充描述
     */
    private Map<String, Object> metas;

    /**
     * 实体的主键类型
     */
    private PkType pkType;

    public NutEntity(Class<T> type) {
        this.type = type;
        this.mirror = Mirror.me(type);
        this.byJava = new HashMap<String, MappingField>();
        this.byDB = new HashMap<String, MappingField>();
        this.indexMap = new HashMap<String, EntityIndex>();
        this.fields = new ArrayList<MappingField>(5);
        this.indexes = new ArrayList<EntityIndex>(3);
        this.theComposites = new ArrayList<MappingField>(3);
        this.metas = new HashMap<String, Object>();
        this.columnComments = new HashMap<String, String>();

        this.pkType = PkType.UNKNOWN;

        // 字段宏
        beforeInsertMacroes = new ArrayList<Pojo>(3);
        afterInsertMacroes = new ArrayList<Pojo>(3);

        // 获得默认的构造方法
        try {
            bornByDefault = mirror.getBorningByArgTypes();
        }
        catch (Exception e) {}

        // 检查对象的创建方法
        BornContext<T> bc = Borns.evalByArgTypes(type, ResultSet.class);
        if (null != bc)
            this.bornByRS = bc.getBorning();
        else if (null == bornByDefault)
        	throw new DaoException("Need non-arg constructor : " + type);

        // 映射
        this.ones = new LinkFieldSet();
        this.manys = new LinkFieldSet();
        this.manymanys = new LinkFieldSet();
    }

    public T getObject(ResultSet rs, FieldMatcher matcher) {
        // 构造时创建对象
        if (null != bornByRS)
            return bornByRS.born(Lang.array(rs));

        // 通过反射每个字段逐次设置对象
        T re = bornByDefault.born(new Object[]{});
        if (null == matcher)
            for (MappingField fld : fields)
                fld.injectValue(re, rs);
        else
            for (MappingField fld : fields)
                if (matcher.match(fld.getName()))
                    fld.injectValue(re, rs);

        // 返回构造的对象
        return re;
    }

    public T getObject(Record rec) {
        T obj = bornByDefault.born(new Object[]{});
        for (MappingField fld : fields)
            fld.injectValue(obj, rec);
        return obj;

    }

    /**
     * 当所有字段增加完成，这个方法必须被调用，用来搜索复合主键
     * 
     * @param names
     *            复合主键的 Java 字段名数组
     */
    public void checkCompositeFields(String[] names) {
        if (!Lang.isEmptyArray(names) && names.length > 1) {
            for (String name : names) {
                if (byJava.containsKey(name) && byJava.get(name).isCompositePk())
                    theComposites.add(byJava.get(name));
                else
                    throw Lang.makeThrow(    "Fail to find comosite field '%s' in class '%s'!",
                                            name,
                                            type.getName());
            }
            this.pkType = PkType.COMPOSITE;
        } else if (null != this.theId) {
            this.pkType = PkType.ID;
        } else if (null != this.theName) {
            this.pkType = PkType.NAME;
        }
    }

    /**
     * 增加映射字段
     * 
     * @param field
     *            数据库实体字段
     */
    public void addMappingField(MappingField field) {
        if (field.isId())
            theId = field;
        else if (field.isName())
            theName = field;
        byJava.put(field.getName(), field);
        byDB.put(field.getColumnName(), field);
        fields.add(field);
        columnComments.put(field.getName(), field.getColumnComment());
    }

    /**
     * 
     * @param lnk
     */
    public void addLinkField(LinkField lnk) {
        switch (lnk.getLinkType()) {
        case ONE:
            ones.add(lnk);
            break;
        case MANY:
            manys.add(lnk);
            break;
        case MANYMANY:
            manymanys.add(lnk);
            break;
        default:
            throw Lang.makeThrow(    "It is a miracle in Link field: '%s'(%s)",
                                    lnk.getName(),
                                    lnk.getEntity().getType().getName());
        }
    }

    /**
     * 增加实体索引
     * 
     * @param index
     *            实体索引
     */
    public void addIndex(EntityIndex index) {
        indexes.add(index);
        indexMap.put(index.getName(), index);
    }

    public Context wrapAsContext(Object obj) {
        return new EntityObjectContext(this, obj);
    }

    public List<LinkField> visitOne(Object obj, String regex, LinkVisitor visitor) {
        return ones.visit(obj, regex, visitor);
    }

    public List<LinkField> visitMany(Object obj, String regex, LinkVisitor visitor) {
        return manys.visit(obj, regex, visitor);
    }

    public List<LinkField> visitManyMany(Object obj, String regex, LinkVisitor visitor) {
        return manymanys.visit(obj, regex, visitor);
    }

    public void setTableName(String namep) {
        this.tableName = EntityName.create(namep);
    }

    public void setTableComment(String tComment) {
        this.tableComment = tComment;
    }

    public void setHasTableComment(boolean hasTableComment) {
        this.hasTableComment = hasTableComment;
    }

    public void setHasColumnComment(boolean hasColumnComment) {
        this.hasColumnComment = hasColumnComment;
    }

    public void setBeforeInsertMacroes(List<Pojo> beforeInsertMacroes) {
        this.beforeInsertMacroes = beforeInsertMacroes;
    }

    public void setAfterInsertMacroes(List<Pojo> afterInsertMacroes) {
        this.afterInsertMacroes = afterInsertMacroes;
    }

    public void setViewName(String namep) {
        this.viewName = EntityName.create(namep);
    }

    public MappingField getField(String name) {
        return byJava.get(name);
    }

    public MappingField getColumn(String name) {
        return byDB.get(name);
    }

    public List<MappingField> getMappingFields() {
        return fields;
    }

    public List<LinkField> getLinkFields(String regex) {
        List<LinkField> reOnes = ones.getList(regex);
        List<LinkField> reManys = manys.getList(regex);
        List<LinkField> reManymanys = manymanys.getList(regex);
        List<LinkField> re = new ArrayList<LinkField>(reOnes.size()
                                                        + reManys.size()
                                                        + reManymanys.size());
        re.addAll(reOnes);
        re.addAll(reManys);
        re.addAll(reManymanys);
        return re;
    }

    public List<MappingField> getCompositePKFields() {
        return this.theComposites;
    }

    public MappingField getNameField() {
        return this.theName;
    }

    public MappingField getIdField() {
        return this.theId;
    }

    public List<MappingField> getPks() {
        if (null != theId)
            return Lang.list(theId);
        if (null != theName)
            return Lang.list(theName);
        return theComposites;
    }

    public Class<T> getType() {
        return this.type;
    }

    public Mirror<T> getMirror() {
        return this.mirror;
    }

    public List<EntityIndex> getIndexes() {
        return this.indexes;
    }

    public EntityIndex getIndex(String name) {
        return this.indexMap.get(name);
    }

    public String getTableName() {
        return this.tableName.value();
    }

    public String getViewName() {
        return this.viewName.value();
    }

    public boolean addBeforeInsertMacro(Pojo pojo) {
        if (null != pojo) {
            beforeInsertMacroes.add(pojo);
            return true;
        }
        return false;
    }

    public boolean addAfterInsertMacro(Pojo pojo) {
        if (null != pojo) {
            afterInsertMacroes.add(pojo);
            return true;
        }
        return false;
    }

    public List<Pojo> cloneBeforeInsertMacroes() {
        List<Pojo> re = new ArrayList<Pojo>(beforeInsertMacroes.size());
        for (Pojo pojo : beforeInsertMacroes)
            re.add(pojo.duplicate());
        return re;
    }

    public List<Pojo> cloneAfterInsertMacroes() {
        List<Pojo> re = new ArrayList<Pojo>(afterInsertMacroes.size());
        for (Pojo pojo : afterInsertMacroes)
            re.add(pojo.duplicate());
        return re;
    }

    public PkType getPkType() {
        return pkType;
    }

    public Object getMeta(String key) {
        return metas.get(key);
    }

    public boolean hasMeta(String key) {
        return metas.containsKey(key);
    }

    public Map<String, Object> getMetas() {
        return metas;
    }

    public String toString() {
        return String.format("Entity<%s:%s>", getType().getName(), getTableName());
    }

    public boolean hasTableComment() {
        return hasTableComment;
    }

    public String getTableComment() {
        return tableComment;
    }

    public boolean hasColumnComment() {
        return hasColumnComment;
    }

    public String getColumnComent(String columnName) {
        return columnComments.get(columnName);
    }
}
