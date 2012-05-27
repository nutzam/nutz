package utils.form;

import java.util.List;

import org.nutz.dao.Condition;
import org.nutz.dao.Dao;
import org.nutz.dao.pager.Pager;

import controllers.MainModule;

public class PageForm<T> {

	private List<T>  results;
	private Pager pager ;
	public List<T> getResults() {
		return results;
	}
	public void setResults(List<T> results) {
		this.results = results;
	}
	public Pager getPager() {
		return pager;
	}
	public void setPager(Pager pager) {
		this.pager = pager;
	}
	/**
	 *	当query和count的查询不相同时，分别传cnd即可
	 * 
	 */
	public static <T> PageForm<T> getPaper(Dao dao,Class<T> clazz,Condition queryCnd,Condition countCnd,int offset,int max){
		  PageForm<T> pf = new PageForm<T>();
		  if (offset<1) offset = 1;
		  if(max <1 ) max = MainModule.max;
		  Pager pager = dao.createPager(offset,max);
		  List<T> results = dao.query(clazz,queryCnd, pager);
		  int count = dao.count(clazz,countCnd);
		  pager.setRecordCount(count);
		  pf.setPager(pager);
		  pf.setResults(results);
		  return pf;
	}
	/**
	 * 当query和count的查询相同时，可以只传一个cnd即可，若不相同，请用上面的方法
	 * @param dao
	 * @param clazz
	 * @param cnd
	 * @param offset
	 * @param max
	 * @return
	 */
	public static <T> PageForm<T> getPaper(Dao dao,Class<T> clazz,Condition cnd ,int offset,int max){
		PageForm<T> pf = new PageForm<T>();
		if (offset<1) offset = 1;
		if(max <1 ) max = MainModule.max;
		Pager pager = dao.createPager(offset,max);
		List<T> results = dao.query(clazz, cnd, pager);
		int count = dao.count(clazz,cnd);
		pager.setRecordCount(count);
		pf.setPager(pager);
		pf.setResults(results);
		return pf;
	}
}