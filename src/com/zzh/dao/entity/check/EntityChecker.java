package com.zzh.dao.entity.check;

public interface EntityChecker {
	void doCheck(Object obj) throws EnityInvalidException;
}
