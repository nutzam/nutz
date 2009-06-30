package org.nutz.mvc.access;

import org.nutz.dao.Dao;
import org.nutz.lang.Strings;
import org.nutz.service.EntityService;

public class EntityAccountService<T extends Account> extends EntityService<T> implements
		AccountService<T> {

	public EntityAccountService() {
		super();
	}

	public EntityAccountService(Dao dao) {
		super(dao);
	}

	@Override
	public Class<T> getAccountType() {
		return this.getEntityClass();
	}

	@Override
	public T verify(T account) {
		T dba = dao().fetch(getAccountType(), account.getName());
		if (null != dba)
			if (Strings.equalsIgnoreCase(account.getPassword(), dba.getPassword())) {
				return dba;
			}
		return null;
	}

}
