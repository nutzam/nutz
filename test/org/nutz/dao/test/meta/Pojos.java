package org.nutz.dao.test.meta;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.nutz.dao.Dao;
import org.nutz.dao.TableName;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.tools.DTable;
import org.nutz.dao.tools.TableDefinition;
import org.nutz.dao.tools.Tables;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.segment.CharSegment;
import org.nutz.lang.segment.Segment;
import org.nutz.service.*;
import org.nutz.trans.Atom;

public class Pojos extends Service {

	public static void main(String[] args) {

	}

	private String topTables;
	private String platoonTables;
	private Dao dao;

	public Pojos(Dao dao) {
		super(dao);
		String prefix = Pojos.class.getPackage().getName().replace('.', '/');
		topTables = Lang.readAll(Streams.fileInr(prefix + "/top_tables.dod"));
		platoonTables = Lang.readAll(Streams.fileInr(prefix + "/platoon_tables.dod"));
		this.dao = dao;
	}

	public void initPet() {
		Tables.run(dao, Tables.define("org/nutz/dao/test/meta/pet.dod"));
	}

	public void init() {
		Tables.run(dao, Tables.defineBy(topTables));
	}

	public Platoon create4Platoon(Base base, String name) {
		final Platoon p = dao().insert(Platoon.make(base, name));
		int id = p.getId();
		initPlatoon(id);
		TableName.run(id, new Atom() {
			public void run() {
				Soldier mick = Soldier.make("Mick");
				Soldier zzh = Soldier.make("ZZH");
				Soldier peter = Soldier.make("Peter");
				Soldier sm = Soldier.make("Super Man");
				Soldier bush = Soldier.make("George.W.Bush");
				p.setTanks(new HashMap<String, Tank>());

				Tank m1a1 = p.addTank(Tank.make("M1-A1"));
				m1a1.setMotorName(bush.getName());
				m1a1.setMembers(new HashMap<String, Soldier>());
				m1a1.addMember(bush).addMember(sm).addMember(zzh);

				Tank t92 = p.addTank(Tank.make("T92"));
				t92.setMotorName(zzh.getName());
				t92.setMembers(new HashMap<String, Soldier>());
				t92.addMember(zzh).addMember(mick).addMember(peter);

				p.setLeaderName(zzh.getName());
				p.setLeader(zzh);

				p.setSoliders(new LinkedList<Soldier>());
				p.getSoliders().add(mick);
				p.getSoliders().add(peter);
				p.getSoliders().add(sm);
				p.getSoliders().add(bush);

				dao.update(p);
				dao.insertLinks(p, "leader|soliders|tanks");
				dao.insertLinks(m1a1, "members");
				dao.insertLinks(t92, "members");
				dao.insertLinks(Gun.assign(mick, Gun.TYPE.AK47, Gun.TYPE.P228), "guns");
				dao.insertLinks(Gun.assign(zzh, Gun.TYPE.AWP, Gun.TYPE.MP5, Gun.TYPE.P228), "guns");
				dao.insertLinks(Gun.assign(peter, Gun.TYPE.M16, Gun.TYPE.UMP_45), "guns");
				dao.insertLinks(Gun.assign(sm, Gun.TYPE.M16, Gun.TYPE.UMP_45), "guns");
				dao.insertLinks(Gun.assign(bush, Gun.TYPE.M60, Gun.TYPE.P228), "guns");
			}
		});
		return p;
	}

	public void initPlatoon(int id) {
		Segment seg = new CharSegment(this.platoonTables);
		Tables.run(dao, Tables.defineBy(seg.set("id", id).toString()));
	}

	public void dropPlatoon(int id) {
		Segment seg = new CharSegment(this.platoonTables);
		List<DTable> dts = Tables.defineBy(seg.set("id", id).toString());
		TableDefinition maker = Tables.newInstance(((NutDao) dao).meta());
		for (DTable dt : dts) {
			if (dao.exists(dt.getName()))
				dao.execute(maker.makeDropSql(dt));
		}
	}

	public void initData() {
		init();
		dao.insert(WaveBand.make("FM_A", 107.9));
		dao.insert(WaveBand.make("FM_B", 104.83));
		dao.insert(WaveBand.make("DQ_99", 109.99));
		dao.insert(WaveBand.make("X", 101.21));

		Base b = Base.make("red");
		b.setCountry(Country.make("China"));
		b.setFighters(new LinkedList<Fighter>());
		b.getFighters().add(Fighter.make(Fighter.TYPE.SU_35));
		b.getFighters().add(Fighter.make(Fighter.TYPE.SU_27));
		b.getFighters().add(Fighter.make(Fighter.TYPE.SU_27));
		b.getFighters().add(Fighter.make(Fighter.TYPE.SU_31));
		b.getFighters().add(Fighter.make(Fighter.TYPE.SU_31));
		b.getFighters().add(Fighter.make(Fighter.TYPE.SU_31));
		b.setPlatoons(new HashMap<String, Platoon>());
		b.addPlatoon(Platoon.make(b, "C"));
		b.addPlatoon(Platoon.make(b, "ES"));
		b.addPlatoon(Platoon.make(b, "DT"));
		dao.insertWith(b, "country|fighters|platoons");

		b = Base.make("blue");
		b.setCountry(Country.make("US"));
		b.setFighters(new LinkedList<Fighter>());
		b.getFighters().add(Fighter.make(Fighter.TYPE.F117A));
		b.getFighters().add(Fighter.make(Fighter.TYPE.F22));
		b.getFighters().add(Fighter.make(Fighter.TYPE.F15));
		b.getFighters().add(Fighter.make(Fighter.TYPE.F15));
		b.getFighters().add(Fighter.make(Fighter.TYPE.F16));
		b.getFighters().add(Fighter.make(Fighter.TYPE.F16));
		b.getFighters().add(Fighter.make(Fighter.TYPE.F16));
		b.setPlatoons(new HashMap<String, Platoon>());
		b.addPlatoon(Platoon.make(b, "SF"));
		b.addPlatoon(Platoon.make(b, "DF"));
		b.addPlatoon(Platoon.make(b, "seals"));
		dao.insertLinks(b, "country");
		dao.insert(b);
		dao.insertLinks(b, "fighters|platoons");
	}

}
