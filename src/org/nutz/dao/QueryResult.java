package org.nutz.dao;

import java.util.ArrayList;
import java.util.List;

import org.nutz.castor.Castors;
import org.nutz.dao.pager.Pager;

public class QueryResult {

	private List<?> list;
	private Pager pager;

	public QueryResult(List<?> list, Pager pager) {
		this.list = list;
		this.pager = pager;
	}

	public List<?> getList() {
		return list;
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> getList(Class<T> eleType) {
		return (List<T>) list;
	}
	
	@SuppressWarnings("unchecked")
	public <T> List<T> convertList(Class<T> eleType){
		if(null==list || list.isEmpty())
			return (List<T>)list;
		
		List<T> re = new ArrayList<T>(list.size());
		Castors castors = Castors.me();
		for(Object obj : list) 
			re.add(castors.castTo(obj, eleType));
	
		return re;
	}

	public void setList(List<?> list) {
		this.list = list;
	}

	public Pager getPager() {
		return pager;
	}

	public void setPager(Pager pager) {
		this.pager = pager;
	}

}
