package org.nutz.dao.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Condition;
import org.nutz.dao.ConnCallback;
import org.nutz.dao.Dao;
import org.nutz.dao.DaoException;
import org.nutz.dao.FieldFilter;
import org.nutz.dao.FieldMatcher;
import org.nutz.dao.SqlManager;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.EntityMaker;
import org.nutz.dao.entity.LinkField;
import org.nutz.dao.entity.LinkVisitor;
import org.nutz.dao.entity.MappingField;
import org.nutz.dao.entity.PkType;
import org.nutz.dao.entity.Record;
import org.nutz.dao.impl.link.DoClearLinkVisitor;
import org.nutz.dao.impl.link.DoClearRelationByHostFieldLinkVisitor;
import org.nutz.dao.impl.link.DoClearRelationByLinkedFieldLinkVisitor;
import org.nutz.dao.impl.link.DoDeleteLinkVisitor;
import org.nutz.dao.impl.link.DoInsertLinkVisitor;
import org.nutz.dao.impl.link.DoInsertRelationLinkVisitor;
import org.nutz.dao.impl.link.DoUpdateLinkVisitor;
import org.nutz.dao.impl.link.DoUpdateRelationLinkVisitor;
import org.nutz.dao.impl.sql.pojo.ConditionPItem;
import org.nutz.dao.impl.sql.pojo.PojoEachEntityCallback;
import org.nutz.dao.impl.sql.pojo.PojoEachRecordCallback;
import org.nutz.dao.impl.sql.pojo.PojoFetchEntityByJoinCallback;
import org.nutz.dao.impl.sql.pojo.PojoFetchEntityCallback;
import org.nutz.dao.impl.sql.pojo.PojoFetchIntCallback;
import org.nutz.dao.impl.sql.pojo.PojoFetchObjectCallback;
import org.nutz.dao.impl.sql.pojo.PojoFetchRecordCallback;
import org.nutz.dao.impl.sql.pojo.PojoQueryEntityByJoinCallback;
import org.nutz.dao.impl.sql.pojo.PojoQueryEntityCallback;
import org.nutz.dao.impl.sql.pojo.PojoQueryRecordCallback;
import org.nutz.dao.jdbc.JdbcExpert;
import org.nutz.dao.jdbc.Jdbcs;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Criteria;
import org.nutz.dao.sql.DaoStatement;
import org.nutz.dao.sql.GroupBy;
import org.nutz.dao.sql.PItem;
import org.nutz.dao.sql.Pojo;
import org.nutz.dao.sql.PojoCallback;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.util.Daos;
import org.nutz.dao.util.Pojos;
import org.nutz.dao.util.cri.SqlExpressionGroup;
import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;
import org.nutz.lang.Strings;
import org.nutz.trans.Atom;
import org.nutz.trans.Molecule;

public class NutDao extends DaoSupport implements Dao {

    private PojoCallback _pojo_queryEntity;

    private PojoCallback _pojo_fetchEntity;

    private PojoCallback _pojo_eachEntity;

    private PojoCallback _pojo_queryRecord;

    private PojoCallback _pojo_fetchRecord;

    private PojoCallback _pojo_eachRecord;

    private PojoCallback _pojo_fetchInt;

    private PojoCallback _pojo_fetchObject;

    // ==========================================================
    // 下面是 3 个构造函数
    public NutDao() {
        super();
        // 设置默认的回调
        _pojo_queryEntity = new PojoQueryEntityCallback();
        _pojo_fetchEntity = new PojoFetchEntityCallback();
        _pojo_eachEntity = new PojoEachEntityCallback();
        _pojo_fetchInt = new PojoFetchIntCallback();
        _pojo_fetchObject = new PojoFetchObjectCallback();
        _pojo_queryRecord = new PojoQueryRecordCallback();
        _pojo_fetchRecord = new PojoFetchRecordCallback();
        _pojo_eachRecord = new PojoEachRecordCallback();
    }

    public NutDao(DataSource dataSource) {
        this();
        this.setDataSource(dataSource);
    }

    public NutDao(DataSource dataSource, SqlManager sqlManager) {
        this(dataSource);
        this.setSqlManager(sqlManager);
    }

    public NutDao(DataSource dataSource, EntityMaker maker) {
        this(dataSource);
        this.holder.maker = maker;
        maker.init(dataSource, expert, holder);
    }

    // 上面是 4 个构造函数
    // ==========================================================

    public <T> T getObject(Class<T> classOfT, ResultSet rs, FieldMatcher fm) {
        return getObject(classOfT, rs, fm, null);
    }

    public <T> T getObject(Class<T> classOfT, ResultSet rs, FieldMatcher fm, String prefix) {
        return holder.getEntity(classOfT).getObject(rs, fm, prefix);
    }

    public <T> T insert(final T obj) {
        Object first = Lang.first(obj);
        final EntityOperator opt = _optBy(first);
        if (null == opt)
            return null;
        int size = Lang.eleSize(obj);
    	opt.addInsert(opt.entity, first);
        if (size > 1) {
        	if (opt.getPojoListSize() == 1) {
        		// 单一操作,可以转为批量插入
        		return fastInsert(obj);
        	}
        	Lang.each(obj, false, new Each<Object>() {
        		public void invoke(int i, Object ele, int length) throws ExitLoop, LoopException {
        			if (i != 0)
        				opt.addInsert(opt.entity, ele);
        		}
        	});
        }
        opt.exec();
        return obj;
    }

    public <T> T insert(final T obj, FieldFilter filter) {
        if (filter == null)
            return insert(obj);
        filter.run(new Atom() {
            public void run() {
                insert(obj);
            }
        });
        return obj;
    }

    public void insert(String tableName, Chain chain) {
        if (chain.isSpecial()) {
            Daos.insertBySpecialChain(this, null, tableName, chain);
            return;
        }
        EntityOperator opt = _optBy(chain.toEntityMap(tableName));
        if (null == opt)
            return;
        opt.addInsert();
        opt.exec();
    }

    public void insert(Class<?> classOfT, Chain chain) {
        if (chain.isSpecial()) {
            Daos.insertBySpecialChain(this, getEntity(classOfT), null, chain);
            return;
        }
        EntityOperator opt = _opt(classOfT);
        opt.myObj = chain;
        opt.addInsertSelfOnly();
        // insert(chain.toObject(classOfT));// TODO 这样的效率,未免太低了,需要改进
        opt.exec();
    }

    public <T> T fastInsert(T obj) {
        EntityOperator opt = _optBy(obj);
        if (null == opt)
            return null;
        opt.addInsertSelfOnly();
        opt.exec();
        return obj;
    }

    public <T> T insertWith(T obj, String regex) {
        EntityOperator opt = _optBy(obj);
        if (null == opt)
            return null;
        final LinkVisitor one = doInsert(opt);
        final boolean[] flag = new boolean[1];
        // issue 889. hostField是@Id(auto=true)的时候
        // 需要把相应的@One对象,押后到host对象插入之后
        opt.entity.visitOne(obj, regex, new LinkVisitor() {
            public void visit(Object obj, LinkField lnk) {
                if (lnk.getHostField().isId()) {
                    flag[0] = true;
                    return;
                }
                one.visit(obj, lnk);
            }
        });
        opt.addInsert();
        opt.entity.visitMany(obj, regex, doInsert(opt));
        opt.entity.visitManyMany(obj, regex, doInsert(opt));
        opt.entity.visitManyMany(obj, regex, doInsertRelation(opt));
        opt.exec();
        
        if (flag[0]) {
            opt = _optBy(obj);
            final LinkVisitor _one = doInsert(opt);
            opt.entity.visitOne(obj, regex, new LinkVisitor() {
                public void visit(Object obj, LinkField lnk) {
                    if (!lnk.getHostField().isId())
                        return;
                    _one.visit(obj, lnk);
                }
            });
            opt.exec();
        }

        return obj;
    }

    public <T> T insertLinks(T obj, String regex) {
        // TODO 天啊,每个调用都有4个正则表达式,能快起来不?
        // TODO zzh: NutEntity 会缓存正则表达式计算的结果的，会很快的
        EntityOperator opt = _optBy(obj);
        if (null == opt)
            return null;

        opt.entity.visitOne(obj, regex, doInsert(opt));
        opt.entity.visitMany(obj, regex, doInsert(opt));
        opt.entity.visitManyMany(obj, regex, doInsert(opt));
        opt.entity.visitManyMany(obj, regex, doInsertRelation(opt));
        opt.exec();

        return obj;
    }

    public <T> T insertRelation(T obj, String regex) {
        EntityOperator opt = _optBy(obj);
        if (null == opt)
            return null;

        opt.entity.visitManyMany(obj, regex, doInsertRelation(opt));
        opt.exec();

        return obj;
    }

    public int update(Object obj) {
        EntityOperator opt = _optBy(obj);
        if (null == opt)
            return 0;
        opt.addUpdate();
        opt.exec();
        return opt.getUpdateCount();
    }

    public int update(final Object obj, String actived) {
        Object first = Lang.first(obj);
        if (null == first)
            return 0;

        if (Strings.isBlank(actived))
            return update(obj);
        
        return update(obj, FieldFilter.create(first.getClass(), actived));
    }
    
    public int update(final Object obj, String actived, String locked, boolean ignoreNull) {
        Object first = Lang.first(obj);
        if (null == first)
            return 0;
        return update(obj, FieldFilter.create(first.getClass(), actived, locked, ignoreNull));
    }
    
    public int update(final Object obj, FieldFilter fieldFilter) {
        if (fieldFilter == null)
            return update(obj);
        
        return fieldFilter.run(new Molecule<Integer>() {
            public void run() {
                setObj(update(obj));
            }
        });
    }
    
    public int update(final Object obj, FieldFilter fieldFilter, final Condition cnd) {
        if (fieldFilter == null)
            return update(obj, cnd);
        return fieldFilter.run(new Molecule<Integer>() {
            public void run() {
                setObj(update(obj, cnd));
            }
        });
    }
    
    public int update(Object obj, Condition cnd) {
        if (cnd == null)
            return update(obj);
        EntityOperator opt = _optBy(obj);
        if (null == opt)
            return 0;
        opt.addUpdateByPkAndCnd(cnd);
        opt.exec();
        return opt.getUpdateCount();
    }

    public int updateIgnoreNull(final Object obj) {
        EntityOperator opt = _optBy(obj);
        if (null == opt)
            return 0;
        opt.addUpdateForIgnoreNull(opt.entity, obj, FieldFilter.get(opt.entity.getType()));
        opt.exec();
        return opt.getUpdateCount();
    }

    public int update(String tableName, Chain chain, Condition cnd) {
        EntityOperator opt = _optBy(chain.toEntityMap(tableName));
        if (null == opt)
            return 0;
        opt.addUpdate(chain, cnd);
        opt.exec();
        return opt.getUpdateCount();
    }

    public int update(Class<?> classOfT, Chain chain, Condition cnd) {
        EntityOperator opt = _opt(classOfT);
        opt.addUpdate(chain, cnd);
        opt.exec();
        return opt.getUpdateCount();
    }

    public <T> T updateWith(T obj, final String regex) {
        if (null == obj)
            return null;
        Lang.each(obj, false, new Each<Object>() {
            public void invoke(int index, Object ele, int length) throws ExitLoop, ContinueLoop,
                    LoopException {
                EntityOperator opt = _optBy(ele);
                if (null == opt)
                    return;

                opt.entity.visitOne(ele, regex, doUpdate(opt));
                opt.addUpdate();
                opt.entity.visitMany(ele, regex, doUpdate(opt));
                opt.entity.visitManyMany(ele, regex, doUpdate(opt));

                opt.exec();
            }
        });
        return obj;
    }

    public <T> T updateLinks(T obj, final String regex) {
        if (null == obj)
            return null;
        Lang.each(obj, false, new Each<Object>() {
            public void invoke(int index, Object ele, int length) throws ExitLoop, ContinueLoop,
                    LoopException {
                EntityOperator opt = _optBy(ele);
                if (null == opt)
                    return;

                opt.entity.visitOne(ele, regex, doUpdate(opt));
                opt.entity.visitMany(ele, regex, doUpdate(opt));
                opt.entity.visitManyMany(ele, regex, doUpdate(opt));

                opt.exec();
            }
        });
        return obj;
    }

    public int updateRelation(Class<?> classOfT, String regex, Chain chain, Condition cnd) {
        if (chain.isSpecial())
            throw Lang.noImplement();

        EntityOperator opt = this._opt(classOfT);

        opt.entity.visitManyMany(null, regex, doUpdateRelation(opt, chain, cnd));
        opt.exec();

        return opt.getUpdateCount();
    }

    public int delete(Class<?> classOfT, long id) {
        Entity<?> en = holder.getEntity(classOfT);
        Pojo pojo = pojoMaker.makeDelete(en).append(Pojos.Items.cndId(en, id));
        pojo.addParamsBy(id);
        _exec(pojo);
        return pojo.getUpdateCount();
    }

    public int delete(Class<?> classOfT, String name) {
        Entity<?> en = holder.getEntity(classOfT);
        Pojo pojo = pojoMaker.makeDelete(en)
                             .append(Pojos.Items.cndName(en, name))
                             .addParamsBy(name);
        _exec(pojo);
        return pojo.getUpdateCount();
    }

    public <T> int deletex(Class<T> classOfT, Object... pks) {
        Entity<T> en = holder.getEntity(classOfT);
        Pojo pojo = pojoMaker.makeDelete(en).append(Pojos.Items.cndPk(en, pks));
        _exec(pojo);
        return pojo.getUpdateCount();
    }

    public int delete(Object obj) {
        EntityOperator opt = _optBy(obj);
        if (null == opt)
            return 0;
        opt.addDeleteSelfOnly();
        opt.exec();
        return opt.getUpdateCount();
    }

    public int deleteWith(Object obj, final String regex) {
        if (null == obj)
            return 0;
        final int[] re = new int[1];
        Lang.each(obj, false, new Each<Object>() {
            public void invoke(int index, Object ele, int length) throws ExitLoop, ContinueLoop,
                    LoopException {
                EntityOperator opt = _optBy(ele);
                if (null == opt)
                    return;
                opt.entity.visitMany(ele, regex, doDelete(opt));
                opt.entity.visitManyMany(ele, regex, doClearRelationByLinkedField(opt));
                opt.entity.visitManyMany(ele, regex, doDelete(opt));
                opt.addDeleteSelfOnly();
                opt.entity.visitOne(ele, regex, doDelete(opt));

                re[0] += opt.exec().getUpdateCount();
            }
        });
        return re[0];
    }

    public int deleteLinks(Object obj, final String regex) {
        if (null == obj)
            return 0;
        final int[] re = new int[1];
        Lang.each(obj, false, new Each<Object>() {
            public void invoke(int index, Object ele, int length) throws ExitLoop, ContinueLoop,
                    LoopException {
                EntityOperator opt = _optBy(ele);
                if (null == opt)
                    return;
                opt.entity.visitMany(ele, regex, doDelete(opt));
                opt.entity.visitManyMany(ele, regex, doClearRelationByLinkedField(opt));
                opt.entity.visitManyMany(ele, regex, doDelete(opt));
                opt.entity.visitOne(ele, regex, doDelete(opt));

                re[0] += opt.exec().getUpdateCount();
            }
        });
        return re[0];
    }

    public <T> List<T> query(Class<T> classOfT, Condition cnd, Pager pager) {
        Pojo pojo = pojoMaker.makeQuery(holder.getEntity(classOfT))
                             .append(Pojos.Items.cnd(cnd))
                             .addParamsBy("*")
                             .setPager(pager)
                             .setAfter(_pojo_queryEntity);
        expert.formatQuery(pojo);
        _exec(pojo);
        return pojo.getList(classOfT);
    }

    public <T> List<T> query(Class<T> classOfT, Condition cnd) {
        return query(classOfT, cnd, Pojos.Items.pager(cnd));
    }

    public <T> int each(Class<T> classOfT, Condition cnd, Pager pager, Each<T> callback) {
        Pojo pojo = pojoMaker.makeQuery(holder.getEntity(classOfT))
                             .append(Pojos.Items.cnd(cnd))
                             .addParamsBy("*")
                             .setPager(pager)
                             .setAfter(_pojo_queryEntity);
        expert.formatQuery(pojo);
        pojo.setAfter(_pojo_eachEntity);
        pojo.getContext().attr(Each.class.getName(), callback);
        pojo.getContext().attr("dao-cache-skip", "true");
        _exec(pojo);
        return pojo.getInt();
    }
    
    public <T> int each(Class<T> classOfT, Condition cnd, Each<T> callback) {
        return each(classOfT, cnd, Pojos.Items.pager(cnd), callback);
    }
    
    public List<Record> query(String tableName, Condition cnd, Pager pager) {
        return query(tableName, cnd, pager, "*");
    }
    
    public List<Record> query(String tableName, Condition cnd, Pager pager, String fields) {
        Pojo pojo = pojoMaker.makeQuery(tableName, fields)
                             .addParamsBy(fields)
                             .setPager(pager)
                             .append(Pojos.Items.cnd(cnd));
        expert.formatQuery(pojo);
        pojo.setAfter(_pojo_queryRecord);
        _exec(pojo);
        return pojo.getList(Record.class);
    }

    public List<Record> query(String tableName, Condition cnd) {
        return query(tableName, cnd, Pojos.Items.pager(cnd));
    }

    public int each(String tableName, Condition cnd, Pager pager, Each<Record> callback, String fields) {
        Pojo pojo = pojoMaker.makeQuery(tableName, fields)
                             .addParamsBy(fields)
                             .setPager(pager)
                             .append(Pojos.Items.cnd(cnd));
        expert.formatQuery(pojo);
        pojo.setAfter(_pojo_eachRecord);
        pojo.getContext().attr(Each.class.getName(), callback);
        pojo.getContext().attr("dao-cache-skip", "true");
        _exec(pojo);
        return pojo.getInt();
    }
    
    public int each(String tableName, Condition cnd, Pager pager, Each<Record> callback) {
        return each(tableName, cnd, pager, callback, "*");
    }

    public int each(String tableName, Condition cnd, Each<Record> callback) {
        return each(tableName, cnd, Pojos.Items.pager(cnd), callback);
    }

    public <T> T fetch(Class<T> classOfT, long id) {
        Entity<T> en = holder.getEntity(classOfT);
        if (en.getIdField() == null)
            throw new DaoException("Need @Id for " + classOfT);
        Pojo pojo = pojoMaker.makeQuery(en)
                             .append(Pojos.Items.cndId(en, id))
                             .addParamsBy(id)
                             .setAfter(_pojo_fetchEntity);
        _exec(pojo);
        return pojo.getObject(classOfT);
    }

    public <T> T fetch(Class<T> classOfT, String name) {
        if (name == null)
            throw new IllegalArgumentException("name MUST NOT NULL!");
        Entity<T> en = holder.getEntity(classOfT);
        if (en.getNameField() == null)
            throw new DaoException("Need @Name for " + classOfT);
        Pojo pojo = pojoMaker.makeQuery(en)
                             .append(Pojos.Items.cndName(en, name))
                             .addParamsBy(name)
                             .setAfter(_pojo_fetchEntity);
        _exec(pojo);
        return pojo.getObject(classOfT);
    }

    public <T> T fetchx(Class<T> classOfT, Object... pks) {
        Entity<T> en = holder.getEntity(classOfT);
        Pojo pojo = pojoMaker.makeQuery(en)
                             .append(Pojos.Items.cndPk(en, pks))
                             .setAfter(_pojo_fetchEntity);
        _exec(pojo);
        return pojo.getObject(classOfT);
    }

    public <T> T fetch(Class<T> classOfT, Condition cnd) {
        Pojo pojo = pojoMaker.makeQuery(holder.getEntity(classOfT))
                             .append(Pojos.Items.cnd(cnd))
                             .addParamsBy("*")
                             .setPager(createPager(1, 1))
                             .setAfter(_pojo_fetchEntity);
        expert.formatQuery(pojo);
        _exec(pojo);
        return pojo.getObject(classOfT);
    }

    public Record fetch(String tableName, Condition cnd) {
        return fetch(tableName, cnd, "*");
    }
    
    public Record fetch(String tableName, Condition cnd, String fields) {
        Pojo pojo = pojoMaker.makeQuery(tableName, fields)
                             .append(Pojos.Items.cnd(cnd))
                             .addParamsBy(fields)
                             .setPager(createPager(1, 1))
                             .setAfter(_pojo_fetchRecord);
        expert.formatQuery(pojo);
        _exec(pojo);
        return pojo.getObject(Record.class);
    }

    @SuppressWarnings("unchecked")
    public <T> T fetch(T obj) {
        Entity<?> en = holder.getEntityBy(obj);
        Pojo pojo = pojoMaker.makeQuery(en)
                             .append(Pojos.Items.cndAuto(en, obj))
                             .setAfter(_pojo_fetchEntity)
                             .setPager(createPager(1, 1));
        _exec(pojo);
        return (T) pojo.getResult();
    }

    public <T> T fetch(Class<T> classOfT) {
        List<T> list = query(classOfT, null, createPager(1, 1));
        if (null != list && !list.isEmpty())
            return list.get(0);
        return null;
    }

    public <T> T fetchLinks(T obj, final String regex) {
        return fetchLinks(obj, regex, null);
    }

    public <T> T fetchLinks(final T obj, final String regex, final Condition cnd) {
        if (null == obj)
            return null;
        Lang.each(obj, false, new Each<Object>() {
            public void invoke(int index, Object ele, int length) {
                _fetchLinks(ele, regex, true, true, true, cnd);
            }
        });
        return obj;
    }

    public int clear(Class<?> classOfT, Condition cnd) {
        Pojo pojo = pojoMaker.makeDelete(holder.getEntity(classOfT)).append(Pojos.Items.cnd(cnd));
        _exec(pojo);
        return pojo.getUpdateCount();
    }

    public int clear(String tableName, Condition cnd) {
        Pojo pojo = pojoMaker.makeDelete(tableName).append(Pojos.Items.cnd(cnd));
        _exec(pojo);
        return pojo.getUpdateCount();
    }

    public int clear(Class<?> classOfT) {
        return clear(classOfT, null);
    }

    public int clear(String tableName) {
        return clear(tableName, null);
    }

    public <T> T clearLinks(T obj, final String regex) {
        if (null == obj)
            return null;
        Lang.each(obj, false, new Each<Object>() {
            public void invoke(int index, Object ele, int length) {
                EntityOperator opt = _optBy(ele);
                if (null == opt)
                    return;
                opt.entity.visitMany(ele, regex, doClear(opt));
                opt.entity.visitManyMany(ele, regex, doClearRelationByHostField(opt));
                opt.entity.visitOne(ele, regex, doClear(opt));

                opt.exec();
            }
        });
        return obj;
    }

    public <T> Entity<T> getEntity(Class<T> classOfT) {
        return holder.getEntity(classOfT);
    }

    public int count(Class<?> classOfT, Condition cnd) {
        Entity<?> en = holder.getEntity(classOfT);
        return _count(en, en.getViewName(), cnd);
    }

    public int count(Class<?> classOfT) {
        Entity<?> en = holder.getEntity(classOfT);
        return _count(en, en.getViewName(), null);
    }

    public int count(String tableName) {
        return count(tableName, null);
    }

    public int count(String tableName, Condition cnd) {
        return _count(null, tableName, cnd);
    }

    private int _count(Entity<?> en, String tableName, Condition cnd) {
        // 如果有条件的话
        if (null != cnd) {
            Pojo pojo = pojoMaker.makeFunc(tableName, "COUNT", "*");
            pojo.setEntity(en);
            // 高级条件接口，直接得到 WHERE 子句
            if (cnd instanceof Criteria) {
                pojo.append(((Criteria) cnd).where());
                // MySQL/PgSQL/SqlServer 与 Oracle/H2的结果会不一样,奇葩啊
                GroupBy gb = ((Criteria) cnd).getGroupBy();
                pojo.append(gb);
            }
            // 否则暴力获取 WHERE 子句
            else {
                String str = Pojos.formatCondition(en, cnd);
                if (!Strings.isBlank(str)) {
                    String[] ss = str.toUpperCase().split("ORDER BY");
                    pojo.append(Pojos.Items.wrap(str.substring(0, ss[0].length())));
                }
            }
            // 设置回调，并执行 SQL
            pojo.setAfter(_pojo_fetchInt);
            _exec(pojo);
            return pojo.getInt();
        }
        // 没有条件，直接生成表达式
        return func(tableName, "COUNT", "*");
    }

    public int getMaxId(Class<?> classOfT) {
        Entity<?> en = holder.getEntity(classOfT);
        return func(en.getViewName(), "MAX", en.getIdField().getColumnNameInSql());
    }

    public int func(Class<?> classOfT, String funcName, String fieldName) {
        return func(classOfT, funcName, fieldName, null);
    }

    public int func(String tableName, String funcName, String colName) {
        return func(tableName, funcName, colName, null);
    }

    public int func(Class<?> classOfT, String funcName, String colName, Condition cnd) {
        Entity<?> en = holder.getEntity(classOfT);
        if (null != en.getField(colName))
            colName = en.getField(colName).getColumnNameInSql();
        DaoStatement pojo = pojoMaker.makeFunc(en.getViewName(), funcName, colName)
                                     .append(Pojos.Items.cnd(cnd))
                                     .setAfter(_pojo_fetchInt)
                                     .setEntity(en);
        _exec(pojo);
        return pojo.getInt();
    }

    public int func(String tableName, String funcName, String colName, Condition cnd) {
        DaoStatement pojo = pojoMaker.makeFunc(tableName, funcName, colName)
                                     .append(Pojos.Items.cnd(cnd))
                                     .setAfter(_pojo_fetchInt);
        _exec(pojo);
        return pojo.getInt();
    }

    public Object func2(Class<?> classOfT, String func2Name, String fieldName) {
        return func2(classOfT, func2Name, fieldName, null);
    }

    public Object func2(String tableName, String func2Name, String colName) {
        return func2(tableName, func2Name, colName, null);
    }

    public Object func2(Class<?> classOfT, String func2Name, String colName, Condition cnd) {
        Entity<?> en = holder.getEntity(classOfT);
        if (null != en.getField(colName))
            colName = en.getField(colName).getColumnNameInSql();
        DaoStatement pojo = pojoMaker.makeFunc(en.getViewName(), func2Name, colName)
                                     .append(Pojos.Items.cnd(cnd))
                                     .setAfter(_pojo_fetchObject)
                                     .setEntity(en);
        _exec(pojo);
        return pojo.getResult();
    }

    public Object func2(String tableName, String func2Name, String colName, Condition cnd) {
        DaoStatement pojo = pojoMaker.makeFunc(tableName, func2Name, colName)
                                     .append(Pojos.Items.cnd(cnd))
                                     .setAfter(_pojo_fetchObject);
        _exec(pojo);
        return pojo.getResult();
    }

    public Pager createPager(int pageNumber, int pageSize) {
        Pager pager = new Pager();
        pager.setPageNumber(pageNumber);
        pager.setPageSize(pageSize);
        return pager;
    }

    public synchronized <T> Entity<T> create(Class<T> classOfT, boolean dropIfExists) {
        Entity<T> en = holder.getEntity(classOfT);
        if (exists(en.getTableName())) {
            if (dropIfExists) {
                expert.dropEntity(this, en);
            } else {
                expert.createRelation(this, en);
                return en;
            }
        }
        holder.remove(classOfT.getName());
        final Entity<T> _en = holder.getEntity(classOfT);
        expert.createEntity(this, _en);
        // 最后在数据库中验证一下实体各个字段
        run(new ConnCallback() {
            public void invoke(Connection conn) throws Exception {
                expert.setupEntityField(conn, _en);
            }
        });
        return en;
    }

    public boolean drop(Class<?> classOfT) {
        Entity<?> en = holder.getEntity(classOfT);
        if (!exists(en.getTableName()))
            return false;
        return expert.dropEntity(this, en);
    }

    public boolean drop(String tableName) {
        if (!exists(tableName))
            return false;
        Sql sql = Sqls.createf("DROP TABLE %s", tableName);
        _exec(sql);
        return true;
    }

    public boolean exists(Class<?> classOfT) {
        return exists(getEntity(classOfT).getViewName());
    }

    public boolean exists(final String tableName) {
        final boolean[] ee = {false};
        this.run(new ConnCallback() {
            public void invoke(Connection conn) {
                Statement stat = null;
                ResultSet rs = null;
                try {
                    stat = conn.createStatement();
                    // 增加不等式,减少sql执行时间
                    String sql = "SELECT COUNT(1) FROM " + tableName + " where 1!=1";
                    rs = stat.executeQuery(sql);
                    if (rs.next())
                        ee[0] = true;
                }
                catch (SQLException e) {}
                finally {
                    Daos.safeClose(stat, rs);
                }
            }
        });
        return ee[0];
    }

    // ==========================================================
    // 下面几个是快速创建映射操作回调的帮助函数

    private LinkVisitor doInsert(EntityOperator opt) {
        return new DoInsertLinkVisitor().opt(opt);
    }

    private LinkVisitor doInsertRelation(EntityOperator opt) {
        return new DoInsertRelationLinkVisitor(holder).opt(opt);
    }

    private LinkVisitor doUpdate(EntityOperator opt) {
        return new DoUpdateLinkVisitor().opt(opt);
    }

    private LinkVisitor doUpdateRelation(EntityOperator opt, Chain chain, Condition cnd) {
        return new DoUpdateRelationLinkVisitor(chain.toMap(), cnd).opt(opt);
    }

    private LinkVisitor doClearRelationByLinkedField(EntityOperator opt) {
        return new DoClearRelationByLinkedFieldLinkVisitor().opt(opt);
    }

    private LinkVisitor doClearRelationByHostField(EntityOperator opt) {
        return new DoClearRelationByHostFieldLinkVisitor().opt(opt);
    }

    private LinkVisitor doDelete(EntityOperator opt) {
        return new DoDeleteLinkVisitor().opt(opt);
    }

    private LinkVisitor doClear(EntityOperator opt) {
        return new DoClearLinkVisitor().opt(opt);
    }

    private LinkVisitor doFetch(final EntityOperator opt) {
        return new LinkVisitor() {
            public void visit(final Object obj, final LinkField lnk) {
                Pojo pojo = opt.maker().makeQuery(lnk.getLinkedEntity());
                pojo.setOperatingObject(obj);
                pojo.append(Pojos.Items.cnd(lnk.createCondition(obj)));
                pojo.setAfter(lnk.getCallback());
                _exec(pojo);
                lnk.setValue(obj, pojo.getObject(Object.class));
            }
        };
    }

    private LinkVisitor doLinkQuery(final EntityOperator opt, final Condition cnd) {
        return new LinkVisitor() {
            public void visit(final Object obj, final LinkField lnk) {
                Pojo pojo = opt.maker().makeQuery(lnk.getLinkedEntity());
                pojo.setOperatingObject(obj);
                PItem[] _cndItems = Pojos.Items.cnd(lnk.createCondition(obj));
                pojo.append(_cndItems);
                if (cnd != null) {
                    if (cnd instanceof Criteria) {
                        Criteria cri = (Criteria) cnd;
                        SqlExpressionGroup seg = cri.where();
                        if (_cndItems.length > 0 && seg != null && !seg.isEmpty()) {
                            seg.setTop(false);
                            pojo.append(Pojos.Items.wrap(" AND "));
                        }
                        pojo.append(cri);
                        if (cri.getPager() != null) {
                            pojo.setPager(cri.getPager());
                            expert.formatQuery(pojo);
                        }
                    }
                    // 普通条件
                    else {
                        pojo.append(new ConditionPItem(cnd));
                    }
                }
                pojo.setAfter(lnk.getCallback());
                pojo.setEntity(lnk.getLinkedEntity());
                _exec(pojo);
                lnk.setValue(obj, pojo.getResult());
            }
        };
    }

    // ==========================================================
    // 下面几个是快速创建实体操作对象的帮助函数

    private <T> EntityOperator _opt() {
        EntityOperator opt = new EntityOperator();
        opt.dao = this;
        return opt;
    }

    <T> EntityOperator _opt(Entity<T> en) {
        EntityOperator opt = _opt();
        opt.entity = en;
        return opt;
    }

    <T> EntityOperator _opt(Class<T> classOfT) {
        return _opt(holder.getEntity(classOfT));
    }

    EntityOperator _optBy(Object obj) {
        // 阻止空对象
        if (null == obj)
            return null;
        // 对象是否有内容，这里会考虑集合与数组
        Entity<?> en = holder.getEntityBy(obj);
        if (null == en)
            return null;
        // 创建操作对象
        EntityOperator re = _opt(en);
        re.myObj = obj.getClass().isArray() ? Lang.array2list((Object[]) obj) : obj;
        return re;
    }

    // ---------------------------------------------------------------
    // 专属于NutDao的一些帮助方法

    public void setExpert(Object obj) throws Exception {
        if (obj == null)
            throw new NullPointerException("expert MUST NOT NULL!!");
        if (obj instanceof JdbcExpert) {
            this.expert = (JdbcExpert) obj;
        } else {
            String name = obj.toString();
            this.expert = Jdbcs.getExpert(name, "");
            if (this.expert == null) {
                if (name.contains(".")) {
                    this.expert = (JdbcExpert) Lang.loadClass(name).newInstance();
                } else {
                    throw new DaoException("not such expert=" + obj);
                }
            }
        }
        DataSource ds = this.dataSource;
        // 如果数据源比expert先设置,那么需要重新设置一次
        if (ds != null) {
            this.dataSource = null;
            setDataSource(ds);
        }
    }
    
    public Sql execute(Sql sql) {
        if (sql != null)
            execute(new Sql[]{sql});
        return sql;
    }
    
    public <T> T insert(final T t, boolean ignoreNull, boolean ignoreZero, boolean ignoreBlankStr) {
    	Object obj = Lang.first(t);
    	Entity<?> en = getEntity(obj.getClass());
    	List<String> names = new ArrayList<String>();
    	for (MappingField mf : en.getMappingFields()) {
    	    if (mf.isName() || mf.isPk() || mf.isId()) {
                names.add(mf.getName());
    	        continue;
    	    }
    		Object tmp = mf.getValue(obj);
			if (ignoreNull && tmp == null) {
				continue;
			}
			if (ignoreZero && (tmp == null || (tmp instanceof Number && ((Number)tmp).intValue() == 0))) {
				continue;
			}
			if (ignoreBlankStr && (tmp instanceof CharSequence && Strings.isBlank((CharSequence)tmp)))
				continue;
			names.add(mf.getName());
		}
    	FieldFilter ff = FieldFilter.create(obj.getClass(), "^("+Strings.join("|", names.toArray())+")$");
    	Molecule<T> m = new Molecule<T>() {
    		public void run() {
    			insert(t);
    			setObj(t);
    		}
		};
		return ff.run(m);
    }
    
    public <T> List<T> query(final Class<T> classOfT, final Condition cnd, final Pager pager, FieldMatcher matcher) {
        if (matcher == null)
            return query(classOfT, cnd, pager);
        FieldFilter ff = FieldFilter.create(classOfT, matcher);
        Molecule<List<T>> m = new Molecule<List<T>>() {
            public void run() {
                setObj(query(classOfT, cnd, pager));
            }
        };
        return ff.run(m);
    }
    
    public <T> List<T> query(final Class<T> classOfT, final Condition cnd, final Pager pager, String regex) {
        if (regex == null)
            return query(classOfT, cnd, pager);
        FieldFilter ff = FieldFilter.create(classOfT, FieldMatcher.make(regex, null, false));
        Molecule<List<T>> m = new Molecule<List<T>>() {
            public void run() {
                setObj(query(classOfT, cnd, pager));
            }
        };
        return ff.run(m);
    }
    
    public <T> T insertOrUpdate(T t) {
        return insertOrUpdate(t, null, null);
    }
    
    public <T> T insertOrUpdate(T t, FieldFilter insertFieldFilter, FieldFilter updateFieldFilter) {
        if (t == null)
            return null;
        Object obj = Lang.first(t);
        Entity<?> en = getEntity(obj.getClass());
        if (en.getPkType() == PkType.NAME) {
            MappingField mf = en.getNameField();
            Object val = mf.getValue(obj);
            if (val == null || fetch(obj.getClass(), Cnd.where(mf.getName(), "=", val)) == null) {
                insert(t, insertFieldFilter);
            } else {
                update(t, updateFieldFilter);
            }
            return t;
        }
        if (fetch(t) != null)
            update(t, updateFieldFilter);
        else
            insert(t, insertFieldFilter);
        return t;
    }
    
    public int updateAndIncrIfMatch(final Object obj, FieldFilter fieldFilter, String fieldName) {
        final EntityOperator opt = _optBy(obj);
        if (null == opt)
            return 0;
        if (fieldName == null)
            fieldName = "version";
        if (fieldFilter == null)
            fieldFilter = FieldFilter.create(opt.entity.getType(), null, "^"+fieldName+"$", false);
        else {
            FieldMatcher fieldMatcher = fieldFilter.map().get(opt.entity.getType());
            if (fieldMatcher == null) {
                fieldMatcher = FieldMatcher.make(null, "^"+fieldName+"$", false);
                fieldFilter.map().put(opt.entity.getType(), fieldMatcher);
            } else {
                if (fieldMatcher.getLocked() == null) {
                    fieldMatcher.setLocked("^"+fieldName+"$");
                }
            }
        }
        final String _fieldName = fieldName;
        fieldFilter.run(new Atom() {
            public void run() {
                opt.addUpdateAndIncrIfMatch(opt.entity, obj, _fieldName);
                opt.exec();}
        });
        return opt.getUpdateCount();
    }

    public int updateWithVersion(Object obj) {
        return updateWithVersion(obj, null);
    }
    
    public int updateWithVersion(Object obj, FieldFilter fieldFilter) {
        return updateAndIncrIfMatch(obj, fieldFilter, getEntity(Lang.first(obj).getClass()).getVersionField().getName());
    }
    
    public <T> T fetchByJoin(Class<T> klass, String regex, long id) {
        Entity<T> en = getEntity(klass);
        MappingField mf = en.getIdField();
        return fetchByJoin(klass, regex, en, mf, id);
    }
    
    public <T> T fetchByJoin(Class<T> klass, String regex, String name) {
        Entity<T> en = getEntity(klass);
        MappingField mf = en.getNameField();
        return fetchByJoin(klass, regex, en, mf, name);
    }
    
    public <T> T fetchByJoin(Class<T> klass, String regex, Entity<T> en, MappingField mf, Object value) {
        String key = en.getTableName() + "." + mf.getColumnNameInSql();
        T t = fetchByJoin(klass, regex, Cnd.where(key, "=", value));
        if (t != null)
            _fetchLinks(t, regex, false, true, true, null);
        return t;
    }
    
    public <T> T fetchByJoin(Class<T> classOfT, String regex, Condition cnd) {
        Pojo pojo = pojoMaker.makeQueryByJoin(holder.getEntity(classOfT), regex)
                .append(Pojos.Items.cnd(cnd))
                .addParamsBy("*")
                .setPager(createPager(1, 1))
                .setAfter(new PojoFetchEntityByJoinCallback(regex));
        expert.formatQuery(pojo);
        _exec(pojo);
        T t = pojo.getObject(classOfT);
        if (t != null)
            _fetchLinks(t, regex, false, true, true, null);
        return t;
    }
    
    public <T> List<T> queryByJoin(Class<T> classOfT, String regex, Condition cnd) {
        return this.queryByJoin(classOfT, regex, cnd, null);
    }
    
    public <T> List<T> queryByJoin(Class<T> classOfT, String regex, Condition cnd, Pager pager) {
    	Pojo pojo = pojoMaker.makeQueryByJoin(holder.getEntity(classOfT), regex)
    			.append(Pojos.Items.cnd(cnd))
    			.addParamsBy("*")
    			.setPager(pager)
    			.setAfter(new PojoQueryEntityByJoinCallback(regex));
    	expert.formatQuery(pojo);
    	_exec(pojo);
    	List<T> list = pojo.getList(classOfT);
    	if (list != null && list.size() > 0) 
    		for (T t : list) {
    			_fetchLinks(t, regex, false, true, true, null);
    		}
    	return list;
    }

    public <T> int countByJoin(Class<T> classOfT, String regex, Condition cnd) {
        Pojo pojo = pojoMaker.makeCountByJoin(holder.getEntity(classOfT), regex)
                .append(Pojos.Items.cnd(cnd))
                .addParamsBy("*")
                .setAfter(_pojo_fetchInt);
        expert.formatQuery(pojo);
        _exec(pojo);
        return pojo.getInt(0);
    }
    
    protected Object _fetchLinks(Object t, String regex, boolean visitOne, boolean visitMany, boolean visitManyMany, final Condition cnd) {
        EntityOperator opt = _optBy(t);
        if (null == opt)
            return t;
        if (visitMany)
            opt.entity.visitMany(t, regex, doLinkQuery(opt, cnd));
        if (visitManyMany)
            opt.entity.visitManyMany(t, regex, doLinkQuery(opt, cnd));
        if (visitOne)
            opt.entity.visitOne(t, regex, doFetch(opt));
        opt.exec();
        return t;
    }
    
    public EntityHolder getEntityHolder() {
        return holder;
    }
    
    public <T> T insert(T obj, String actived) {
        Object first = Lang.first(obj);
        if (null == first)
            return null;

        if (Strings.isBlank(actived))
            return insert(obj);
        
        return insert(obj, FieldFilter.create(first.getClass(), actived));
    }
    
    @Override
    public void truncate(Class<?> klass) {
        Entity<?> en = getEntity(klass);
        truncate(en.getTableName());
    }
    
    @Override
    public void truncate(String tableName) {
        if (!exists(tableName))
            return;
        Sql sql = Sqls.createf("TRUNCATE TABLE %s", tableName);
        _exec(sql);
        return;
    }
}
