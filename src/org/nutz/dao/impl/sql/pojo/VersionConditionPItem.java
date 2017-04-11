package org.nutz.dao.impl.sql.pojo;

import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.MappingField;
import org.nutz.dao.jdbc.ValueAdaptor;

public class VersionConditionPItem  extends AbstractPItem {
	  /**
     * 缓存要操作的字段
     */
    protected MappingField mf;

    protected void _mf(Entity<?> en) {
    	if(mf == null)
    		mf =en.getVersionField();
    }

    public void joinSql(Entity<?> en, StringBuilder sb) {
    	_mf(en);
    	if(mf == null){
    		return;
    	}
    	sb.append(" and ").append(mf.getColumnNameInSql()).append("=? ");
    }
    
    public int joinAdaptor(Entity<?> en, ValueAdaptor[] adaptors, int off) {
    	_mf(en);
    	if(mf != null){
    		  adaptors[off++] = mf.getAdaptor();
    	}
    	return off;
    }

    public int joinParams(Entity<?> en, Object obj, Object[] params, int off) {
     	_mf(en);
        if(mf != null){
        	Object v = mf.getValue(obj);
        	params[off++] = null == v ? mf.getDefaultValue(obj) : v;
        }
     	return off;
    }

    public int paramCount(Entity<?> en) {
    	_mf(en);
    	if(mf == null)
    		return 0;
    	else 
    		return 1;
    }
}
