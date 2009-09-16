package org.nutz.dao.sql;

import java.sql.Connection;
import java.sql.SQLException;

import org.nutz.dao.Condition;
import org.nutz.dao.entity.Entity;

public interface Sql {

	VarSet vars();

	VarSet holders();

	SqlContext getContext();

	Sql setCallback(SqlCallback callback);

	Sql setCondition(Condition condition);

	void execute(Connection conn) throws SQLException;

	Object getResult();
	
	Sql setResult(Object result);

	Entity<?> getEntity();

	Sql setAdapter(FieldTypeAdapter adapter);

	Sql setEntity(Entity<?> entity);

	int getUpdateCount();

	Sql duplicate();
}
