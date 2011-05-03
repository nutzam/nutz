package org.nutz.dao.impl.jdbc.oracle;

import org.nutz.dao.DB;
import org.nutz.dao.Dao;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.impl.jdbc.AbstractJdbcExpert;
import org.nutz.dao.jdbc.JdbcExpertConfigFile;
import org.nutz.dao.sql.Pojo;

public class OracleJdbcExpert extends AbstractJdbcExpert {

	private static String CSEQ = "CREATE SEQUENCE ${T}_${F}_SEQ  MINVALUE 1"
									+ " MAXVALUE 999999999999 INCREMENT BY 1 START"
									+ " WITH 1 CACHE 20 NOORDER  NOCYCLE";
	private static String DSEQ = "DROP SEQUENCE ${T}_${F}_SEQ";

	private static String CTRI = "create or replace trigger ${T}_${F}_ST"
									+ " BEFORE INSERT ON ${T}"
									+ " FOR EACH ROW"
									+ " BEGIN "
									+ " SELECT ${T}_${F}_seq.nextval into :new.${F} FROM dual;"
									+ " END ${T}_${F}_ST;";

	private static String PTN =
	// <min>
	"SELECT * FROM ("
	// <..max>
			+ "SELECT T.*, ROWNUM RN FROM ("
			// <...query>
			+ "SELECT %s FROM %s %s"
			// </...query>
			+ ") T WHERE ROWNUM <= %d)"
			// </..max>
			+ " WHERE RN > %d"; // </min>

	public OracleJdbcExpert(JdbcExpertConfigFile conf) {
		super(conf);
	}

	public boolean createEntity(Dao dao, Entity<?> en) {
		return false;
	}

	public void formatQuery(Pojo pojo) {}

	public String getDatabaseType() {
		return DB.ORACLE.name();
	}

}
