package org.nutz.dao.sql;

import org.nutz.dao.Condition;
import org.nutz.dao.util.lambda.PFun;

public interface OrderBy extends Condition,PItem {

    OrderBy asc(String name);

    <T> OrderBy asc(PFun<T, ?> name);

    OrderBy desc(String name);

    <T>  OrderBy desc(PFun<T, ?> name);

    OrderBy orderBy(String name, String dir);

    <T>  OrderBy orderBy(PFun<T, ?> name, String dir);
}
