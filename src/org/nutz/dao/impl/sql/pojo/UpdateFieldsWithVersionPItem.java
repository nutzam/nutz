package org.nutz.dao.impl.sql.pojo;

import java.util.List;

import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.MappingField;
import org.nutz.dao.jdbc.ValueAdaptor;
import org.nutz.dao.util.Pojos;


public class UpdateFieldsWithVersionPItem extends AbstractPItem{

    /**
     * 缓存要操作的字段
     */
    protected List<MappingField> mfs;

    /**
     * 参考对象，根据这个对象来决定是否忽略空值
     */
    private Object refer;

    public UpdateFieldsWithVersionPItem(Object refer) {
        this.refer = refer;
    }

    protected List<MappingField> _mfs(Entity<?> en) {
        if (null == mfs){
        	mfs= Pojos.getFieldsForUpdateWithVersion(_en(en), getFieldMatcher(), refer == null ? pojo.getOperatingObject() : refer);
        }
        return mfs;
    }

    public void joinSql(Entity<?> en, StringBuilder sb) {
        List<MappingField> mfs = _mfs(en);
        
        sb.append(" SET ");
        for (MappingField mf : mfs){
        	sb.append(mf.getColumnNameInSql()).append("=?,");
        }
        MappingField mf =en.getVersionField();
        //version =version+1
        if(mf == null)
        	sb.setCharAt(sb.length() - 1, ' ');
        else
        	sb.append(mf.getColumnNameInSql()).append("=").append(mf.getColumnNameInSql()).append("+1 ");
    }
    
    public int joinAdaptor(Entity<?> en, ValueAdaptor[] adaptors, int off) {
        List<MappingField> mfs = _mfs(en);
        for (MappingField mf : mfs)
            adaptors[off++] = mf.getAdaptor();
        return off;
    }

    public int joinParams(Entity<?> en, Object obj, Object[] params, int off) {
        List<MappingField> mfs = _mfs(en);
        for (MappingField mf : mfs) {
            Object v = mf.getValue(obj);
            params[off++] = null == v ? mf.getDefaultValue(obj) : v;
        }
        return off;
    }

    public int paramCount(Entity<?> en) {
        return _mfs(en).size();
    }
}
