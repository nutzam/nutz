package com.zzh.trans;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import com.zzh.dao.TableName;
import com.zzh.dao.test.DaoCase;
import com.zzh.dao.test.meta.Base;
import com.zzh.dao.test.meta.Country;
import com.zzh.dao.test.meta.Fighter;
import com.zzh.dao.test.meta.Mission;

public class BatchTransTest extends DaoCase {

	@Override
	protected void after() {}

	@Override
	protected void before() {
		pojos.execFile("com/zzh/trans/trans.sqls");
	}

	@Test
	public void try_insert_static_objects_by_many_many() {
		pojos.init();
		final Base b = Base.make("B");
		b.setFighters(new ArrayList<Fighter>());
		TableName.run(1, new Atom() {
			public void run() {
				Trans.exec(new Atom() {
					public void run() {
						dao.insert(Country.make("A"));
						try {
							dao.insert(Country.make("A"));
						} catch (Exception e) {}
						dao.insert(Country.make("C"));
						dao.insert(Country.make("D"));
					}
				});
				assertEquals(3, dao.count(Country.class));
			}
		});

	}

	@Test
	public void try_insert_multiple_dynamic_objects() {
		pojos.initPlatoon(1);
		TableName.run(1, new Atom() {
			public void run() {
				Trans.exec(new Atom() {
					public void run() {
						dao.insert(Mission.make("D1", "2008-12-21 20:15:26"));
						try {
							dao.insert(Mission.make("D1", "2008-9-21 17:12:23"));
						} catch (Exception e) {}
						dao.insert(Mission.make("D2", "2008-10-21 18:13:24"));
						dao.insert(Mission.make("D3", "2008-11-21 19:14:25"));
					}
				});
				assertEquals(3, dao.count(Mission.class));
			}
		});
	}

	@Test
	public void try_insert_multiple_companys() {
		Trans.exec(new Atom() {
			public void run() {
				dao.insert(Company.make("Google"));
				dao.insert(Company.make("Yahoo"));
				try {
					dao.insert(Company.make("Yahoo"));
				} catch (Exception e) {}
				dao.insert(Company.make("Microsoft"));
				dao.insert(Company.make("Sun"));
				dao.insert(Company.make("IBM"));
			}
		});
		assertEquals(5, dao.count(Company.class));
	}

}
