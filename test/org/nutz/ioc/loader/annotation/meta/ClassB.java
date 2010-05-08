package org.nutz.ioc.loader.annotation.meta;

import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean
public class ClassB {
	
	@SuppressWarnings("unused")
	@Inject("refer:dao")
	private Dao dao;
	
	public void setDao(Dao dao) {
		this.dao = dao;
	}

}
