package org.nutz.dao.impl.entity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.nutz.dao.DB;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.EntityField;
import org.nutz.dao.entity.EntityMaker;
import org.nutz.dao.entity.MappingField;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.EL;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Index;
import org.nutz.dao.entity.annotation.Many;
import org.nutz.dao.entity.annotation.ManyMany;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.One;
import org.nutz.dao.entity.annotation.PK;
import org.nutz.dao.entity.annotation.SQL;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.entity.annotation.TableIndexes;
import org.nutz.dao.entity.annotation.TableMeta;
import org.nutz.dao.entity.annotation.View;
import org.nutz.dao.impl.EntityHolder;
import org.nutz.dao.impl.entity.field.ManyLinkField;
import org.nutz.dao.impl.entity.field.ManyManyLinkField;
import org.nutz.dao.impl.entity.field.NutMappingField;
import org.nutz.dao.impl.entity.field.OneLinkField;
import org.nutz.dao.impl.entity.info.LinkInfo;
import org.nutz.dao.impl.entity.info.MappingInfo;
import org.nutz.dao.impl.entity.info.TableInfo;
import org.nutz.dao.impl.entity.info._Infos;
import org.nutz.dao.impl.entity.macro.ElFieldMacro;
import org.nutz.dao.impl.entity.macro.SqlFieldMacro;
import org.nutz.dao.jdbc.JdbcExpert;
import org.nutz.dao.jdbc.Jdbcs;
import org.nutz.dao.sql.Pojo;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.lang.segment.CharSegment;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.trans.Trans;

/**
 * 根据一个 Class 对象生成 Entity 的实例
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class AnnotationEntityMaker implements EntityMaker {

    private static final Log log = Logs.get();

    private DataSource datasource;

    private JdbcExpert expert;

    private EntityHolder holder;

    public AnnotationEntityMaker(DataSource datasource, JdbcExpert expert, EntityHolder holder) {
        this.datasource = datasource;
        this.expert = expert;
        this.holder = holder;
    }

    public <T> Entity<T> make(Class<T> type) {
        NutEntity<T> en = _createNutEntity(type);

        TableInfo ti = _createTableInfo(type);

        /*
         * 获取实体的扩展描述
         */
        // 全局
        if (null != expert.getConf()) {
            for (String key : expert.getConf().keySet())
                en.getMetas().put(key, expert.getConf().get(key));
        }
        // 当前表
        if (null != ti.annMeta) {
            Map<String, Object> map = Lang.map(ti.annMeta.value());
            for (Entry<String, Object> entry : map.entrySet()) {
                en.getMetas().put(entry.getKey(), entry.getValue().toString());
            }
        }

        /*
         * 获得表名以及视图名称及注释
         */
        String tableName = null == ti.annTable ? Strings.lowerWord(type.getSimpleName(), '_')
                                              : ti.annTable.value();
        String viewName = null == ti.annView ? tableName : ti.annView.value();
        en.setTableName(tableName);
        en.setViewName(viewName);

        boolean hasTableComment = null != ti.tableComment;
        String tableComment = hasTableComment ? Strings.isBlank(ti.tableComment.value()) ? type.getName()
                                                                                        : ti.tableComment.value()
                                             : null;
        en.setHasTableComment(hasTableComment);
        en.setTableComment(tableComment);

        /*
         * 获取所有的数据库字段
         */
        // 字段里面是不是有声明过 '@Column' @Comment
        boolean shouldUseColumn = false;
        boolean hasColumnComment = false;
        for (Field field : en.getMirror().getFields()) {
            if (shouldUseColumn && hasColumnComment) {
                break;
            }
            if (!shouldUseColumn && null != field.getAnnotation(Column.class)) {
                shouldUseColumn = true;
            }
            if (!hasColumnComment && null != field.getAnnotation(Comment.class)) {
                hasColumnComment = true;
            }
        }

        en.setHasColumnComment(hasColumnComment);

        /*
         * 循环获取实体字段
         */
        List<MappingInfo> infos = new ArrayList<MappingInfo>();
        List<LinkInfo> ones = new ArrayList<LinkInfo>();
        List<LinkInfo> manys = new ArrayList<LinkInfo>();
        List<LinkInfo> manymanys = new ArrayList<LinkInfo>();

        // 循环所有的字段，查找有没有数据库映射字段
        for (Field field : en.getMirror().getFields()) {
            // '@One'
            if (null != field.getAnnotation(One.class)) {
                ones.add(_Infos.createLinkInfo(field));
            }
            // '@Many'
            else if (null != field.getAnnotation(Many.class)) {
                manys.add(_Infos.createLinkInfo(field));
            }
            // '@ManyMany'
            else if (null != field.getAnnotation(ManyMany.class)) {
                manymanys.add(_Infos.createLinkInfo(field));
            }
            // 应该忽略
            else if ((Modifier.isTransient(field.getModifiers()) && null == field.getAnnotation(Column.class))
                     || (shouldUseColumn && (null == field.getAnnotation(Column.class)
                                             && null == field.getAnnotation(Id.class) && null == field.getAnnotation(Name.class)))) {
                continue;
            }
            // '@Column'
            else {
                infos.add(_Infos.createMappingInfo(ti.annPK, field));
            }

        }
        // 循环所有方法，查找有没有虚拟数据库映射字段
        for (Method method : en.getType().getMethods()) {
            // '@One'
            if (null != method.getAnnotation(One.class)) {
                ones.add(_Infos.createLinkInfo(method));
            }
            // '@Many'
            else if (null != method.getAnnotation(Many.class)) {
                manys.add(_Infos.createLinkInfo(method));
            }
            // '@ManyMany'
            else if (null != method.getAnnotation(ManyMany.class)) {
                manymanys.add(_Infos.createLinkInfo(method));
            }
            // 应该忽略
            else if (null == method.getAnnotation(Column.class)
                     && null == method.getAnnotation(Id.class)
                     && null == method.getAnnotation(Name.class)) {
                continue;
            }
            // '@Column'
            else {
                infos.add(_Infos.createMapingInfo(ti.annPK, method));
            }
        }

        // 给字段排序一下, fix issue #29
        List<MappingInfo> tmp = new ArrayList<MappingInfo>(infos.size());
        MappingInfo miId = null;
        MappingInfo miName = null;
        for (MappingInfo mi : infos) {
            if (mi.annId != null)
                miId = mi;
            else if (mi.annName != null)
                miName = mi;
            else
                tmp.add(mi);
        }
        if (miName != null)
            tmp.add(0, miName);
        if (miId != null)
            tmp.add(0, miId);
        infos = tmp;

        // 映射字段搞完了? 我看看你到底有没有字段!!
        if (infos.isEmpty())
            throw Lang.makeThrow(IllegalArgumentException.class,
                                 "Pojo(%s) without any Mapping Field!!",
                                 type);

        /*
         * 解析所有映射字段
         */
        for (MappingInfo info : infos) {
            NutMappingField ef = new NutMappingField(en);
            _evalMappingField(ef, info);
            en.addMappingField(ef);
        }
        holder.set(en); // 保存一下，这样别的实体映射到这里时会用的到
        /*
         * 解析所有关联字段
         */
        // 一对一 '@One'
        for (LinkInfo li : ones) {
            en.addLinkField(new OneLinkField(en, holder, li));
        }
        // 一对多 '@Many'
        for (LinkInfo li : manys) {
            en.addLinkField(new ManyLinkField(en, holder, li));
        }
        // 多对多 '@ManyMany'
        for (LinkInfo li : manymanys) {
            en.addLinkField(new ManyManyLinkField(en, holder, li));
        }
        // 检查复合主键
        en.checkCompositeFields(null == ti.annPK ? null : ti.annPK.value());

        /*
         * 交付给 expert 来检查一下数据库一致性
         */
        if (null != datasource && null != expert) {
            _checkupEntityFieldsWithDatabase(en);
        }

        /*
         * 检查字段宏
         */
        _evalFieldMacro(en, infos);

        /*
         * 解析实体索引
         */
        if (null != ti.annIndexes)
            _evalEntityIndexes(en, ti.annIndexes);

        // 搞定收工，哦耶 ^_^
        return en;
    }

    /**
     * 向父类递归查找实体的配置
     * 
     * @param type
     *            实体类型
     * @return 实体表描述
     */
    private TableInfo _createTableInfo(Class<?> type) {
        TableInfo info = new TableInfo();
        Mirror<?> mirror = Mirror.me(type);
        info.annTable = mirror.getAnnotation(Table.class);
        info.annView = mirror.getAnnotation(View.class);
        info.annMeta = mirror.getAnnotation(TableMeta.class);
        info.annPK = mirror.getAnnotation(PK.class);
        info.annIndexes = mirror.getAnnotation(TableIndexes.class);
        info.tableComment = mirror.getAnnotation(Comment.class);
        return info;
    }

    /**
     * 根据 '@Next' 和 '@Prev' 的信息，生成一个 FieldMacroInfo 对象
     * 
     * @param els
     *            表达式
     * @param sqls
     *            SQL
     * @return 一个字段宏信息的列表
     */
    private List<FieldMacroInfo> _annToFieldMacroInfo(EL[] els, SQL[] sqls) {
        List<FieldMacroInfo> mis = new LinkedList<FieldMacroInfo>();
        if (els.length > 0) { // els 没有机会为 null 的
            for (EL el : els)
                mis.add(new FieldMacroInfo(el));
        }
        if (sqls.length > 0) { // @SQL 没有 @EL 优先级高
            for (SQL sql : sqls)
                mis.add(new FieldMacroInfo(sql));
        }
        return mis;
    }

    /**
     * @param ef
     * @param info
     */
    private void _evalMappingField(NutMappingField ef, MappingInfo info) {
        // 字段的 Java 名称
        ef.setName(info.name);
        ef.setType(info.fieldType);

        // 字段的数据库名
        if (null == info.annColumn || Strings.isBlank(info.annColumn.value()))
            ef.setColumnName(info.name);
        else
            ef.setColumnName(info.annColumn.value());

        // 字段的注释
        boolean hasColumnComment = null != info.columnComment;
        ef.setHasColumnComment(hasColumnComment);
        if (hasColumnComment) {
            String comment = info.columnComment.value();
            if (Strings.isBlank(comment)) {
                ef.setColumnComment(info.name);
            } else {
                ef.setColumnComment(comment);
            }
        }

        // Id 字段
        if (null != info.annId) {
            ef.setAsId();
            if (info.annId.auto() && info.annPrev == null)
                ef.setAsAutoIncreasement();
        }

        // Name 字段
        if (null != info.annName) {
            ef.setAsName();
            ef.setCasesensitive(info.annName.casesensitive());
        }

        // 检查 @Id 和 @Name 的冲突
        if (ef.isId() && ef.isName())
            throw Lang.makeThrow("Field '%s'(%s) can not be @Id and @Name at same time!",
                                 ef.getName(),
                                 ef.getEntity().getType().getName());

        // 检查 PK
        if (null != info.annPK) {
            // 用 @PK 的方式声明的主键
            if (info.annPK.value().length == 1) {
                if (Lang.contains(info.annPK.value(), info.name)) {
                    if (ef.getTypeMirror().isIntLike())
                        ef.setAsId();
                    else
                        ef.setAsName();
                }
            }
            // 看看是不是复合主键
            else if (Lang.contains(info.annPK.value(), info.name))
                ef.setAsCompositePk();
        }

        // 默认值
        if (null != info.annDefault)
            ef.setDefaultValue(new CharSegment(info.annDefault.value()));

        // 只读
        if (null != info.annReadonly)
            ef.setAsReadonly();

        // 字段更多定义
        if (null != info.annDefine) {
            // 类型
            ef.setColumnType(info.annDefine.type());
            // 宽度
            ef.setWidth(info.annDefine.width());
            // 精度
            ef.setPrecision(info.annDefine.precision());
            // 无符号
            if (info.annDefine.unsigned())
                ef.setAsUnsigned();
            // 非空约束
            if (info.annDefine.notNull())
                ef.setAsNotNull();
            // 自增，如果 @Id(auto=false)，则忽略
            if (info.annDefine.auto() && !ef.isId())
                ef.setAsAutoIncreasement();

            // 是否为自定义类型呢?
            if (info.annDefine.customType().length() > 0) {
                ef.setCustomDbType(info.annDefine.customType());
            }

            // 插入更新操作
            ef.setInsert(info.annDefine.insert());
            ef.setUpdate(info.annDefine.update());
        }
        // 猜测字段类型
        else {
            Jdbcs.guessEntityFieldColumnType(ef);
        }

        // 字段值的适配器
        ef.setAdaptor(expert.getAdaptor(ef));

        // 输入输出
        ef.setInjecting(info.injecting);
        ef.setEjecting(info.ejecting);

    }

    private void _evalFieldMacro(Entity<?> en, List<MappingInfo> infos) {
        for (MappingInfo info : infos) {
            // '@Prev' : 预设值
            if (null != info.annPrev) {
                en.addBeforeInsertMacro(__macro(en.getField(info.name),
                                                _annToFieldMacroInfo(info.annPrev.els(),
                                                                     info.annPrev.value())));
            }

            // '@Next' : 后续获取
            if (null != info.annNext
                && en.addAfterInsertMacro(__macro(en.getField(info.name),
                                                  _annToFieldMacroInfo(info.annNext.els(),
                                                                       info.annNext.value())))) {
                continue;
            }
            // '@Id' : 的自动后续获取
            else if (null != info.annId && info.annId.auto()) {
                if (expert != null && !expert.isSupportAutoIncrement()) {
                	//仅提醒,因为如果有触发器的话,还是可以插入的
                    log.debug("Database don't support auto-increment. If insert fail, pls add trigger in database or using @Prev in Pojo");
                }
                en.addAfterInsertMacro(expert.fetchPojoId(en, en.getField(info.name)));
            }
        }
    }

    private Pojo __macro(MappingField ef, List<FieldMacroInfo> infoList) {
        FieldMacroInfo theInfo = null;
        // 根据当前数据库，找到合适的宏
        for (FieldMacroInfo info : infoList) {
            if (DB.OTHER == info.getDb()) {
                theInfo = info;
            } else if (info.getDb().name().equalsIgnoreCase(expert.getDatabaseType())) {
                theInfo = info;
                break;
            }
        }
        // 如果找到，增加
        if (null != theInfo) {
            if (theInfo.isEl())
                return new ElFieldMacro(ef, theInfo.getValue());
            else
                return new SqlFieldMacro(ef, theInfo.getValue());
        }
        return null;
    }

    private void _evalEntityIndexes(NutEntity<?> en, TableIndexes indexes) {
        for (Index idx : indexes.value()) {
            NutEntityIndex index = new NutEntityIndex();
            index.setUnique(idx.unique());
            index.setName(idx.name());
            for (String indexName : idx.fields()) {
                EntityField ef = en.getField(indexName);
                if (null == ef) {
                    throw Lang.makeThrow("Fail to find field '%s' in '%s' by @Index(%s:%s)",
                                         indexName,
                                         en.getType().getName(),
                                         index.getName(),
                                         Lang.concat(idx.fields()));
                }
                index.addField(ef);
            }
            en.addIndex(index);
        }
    }

    private void _checkupEntityFieldsWithDatabase(NutEntity<?> en) {
        Connection conn = null;
        try {
            conn = Trans.getConnectionAuto(datasource);
            expert.setupEntityField(conn, en);
        }
        catch (Exception e) {
            if (log.isDebugEnabled())
                log.debugf("Fail to setup '%s'(%s) by DB, because: (%s)'%s'",
                           en.getType().getName(),
                           en.getTableName(),
                           e.getClass().getName(),
                           e.getMessage());
        }
        finally {
            Trans.closeConnectionAuto(conn);
        }
    }

    protected <T> NutEntity<T> _createNutEntity(Class<T> type) {
        return new NutEntity<T>(type);
    }
}
