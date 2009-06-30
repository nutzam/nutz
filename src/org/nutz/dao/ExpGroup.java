package org.nutz.dao;

public interface ExpGroup extends Expression {

	ExpGroup and(Expression e);

	ExpGroup and(String name, String op, Object value);

	ExpGroup or(Expression e);

	ExpGroup or(String name, String op, Object value);

	ExpGroup andNot(Expression e);

	ExpGroup andNot(String name, String op, Object value);

	ExpGroup orNot(Expression e);

	ExpGroup orNot(String name, String op, Object value);

}
