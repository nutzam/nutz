package com.zzh.dao.test.normal;

import static org.junit.Assert.*;

import org.junit.Test;

import com.zzh.dao.Chain;
import com.zzh.dao.Cnd;
import com.zzh.dao.test.DaoCase;
import com.zzh.dao.test.meta.Fighter;
import com.zzh.dao.test.meta.Platoon;

public class UpdateTest extends DaoCase {

	@Override
	protected void before() {
		pojos.initData();
	}

	@Override
	protected void after() {}

	@Test
	public void batch_update_all() {
		dao.update(Fighter.class, Chain.make("type", Fighter.TYPE.SU_35.name()), null);
		assertEquals(13, dao
				.count(Fighter.class, Cnd.where("type", "=", Fighter.TYPE.SU_35.name())));
	}

	@Test
	public void batch_update_partly() {
		dao.update(Fighter.class, Chain.make("type", "F15"), Cnd.where("type", "=", "SU_35"));
		dao.update(Fighter.class, Chain.make("type", "SU_35"), Cnd.where("id", "<", 5));
		assertEquals(4, dao.count(Fighter.class, Cnd.where("type", "=", "SU_35")));
	}

	@Test
	public void batch_update_relation() {
		dao.updateRelation(Fighter.class, "base", Chain.make("bname", "blue"), Cnd.where("bname",
				"=", "red"));
		assertEquals(13, dao.count("dao_m_base_fighter", Cnd.where("bname", "=", "blue")));
	}

	@Test
	public void fetch_by_name_ignorecase() {
		Platoon p = dao.fetch(Platoon.class, "sF");
		assertEquals("SF", p.getName());
	}

	@Test
	public void update_with_null_links() {
		Platoon p = dao.fetch(Platoon.class, "sF");
		p.setLeaderName("xyz");
		dao.updateWith(p, null);
		p = dao.fetch(Platoon.class, "sF");
		assertEquals("xyz", p.getLeaderName());
	}
}
