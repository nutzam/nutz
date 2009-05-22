package com.zzh.mvc.access;

public interface AccountService<T extends Account> {

	Class<T> getAccountType();

	T verify(T account);

}
