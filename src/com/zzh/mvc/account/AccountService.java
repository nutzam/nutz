package com.zzh.mvc.account;

public interface AccountService<T extends Account> {

	Class<T> getAccountType();

	T verify(T account);

}
