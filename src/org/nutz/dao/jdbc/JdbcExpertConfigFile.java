package org.nutz.dao.jdbc;

import java.util.Map;
import java.util.regex.Pattern;

import org.nutz.filepool.NutFilePool;
import org.nutz.lang.Mirror;

public class JdbcExpertConfigFile {

	private Map<String, Class<? extends JdbcExpert>> experts;

	private Map<String, Object> config;

	private NutFilePool pool;

	JdbcExpertConfigFile init() {
		String home = config.get("pool-home").toString();
		Integer max = (Integer) config.get("pool-max");
		pool = new NutFilePool(home, max);
		return this;
	}

	public JdbcExpert getExpert(String str) {
		Class<? extends JdbcExpert> type = experts.get(str);
		return Mirror.me(type).born(config);
	}

	public JdbcExpert matchExpert(String dbName) {
		for (String key : experts.keySet()) {
			if (Pattern.matches(key, dbName))
				return Mirror.me(experts.get(key)).born(this);
		}
		return null;
	}

	public Map<String, Class<? extends JdbcExpert>> getExperts() {
		return experts;
	}

	public Map<String, Object> getConfig() {
		return config;
	}

	public NutFilePool getPool() {
		return pool;
	}

	public void setExperts(Map<String, Class<? extends JdbcExpert>> experts) {
		this.experts = experts;
	}

	public void setConfig(Map<String, Object> config) {
		this.config = config;
	}

}
