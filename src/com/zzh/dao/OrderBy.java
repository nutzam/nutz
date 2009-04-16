package com.zzh.dao;

import com.zzh.dao.Condition;

public interface OrderBy extends Condition {

	OrderBy asc(String name);

	OrderBy desc(String name);
}
