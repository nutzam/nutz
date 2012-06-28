package org.nutz.dao.test.normal;

import static org.junit.Assert.*;

import org.junit.Test;
import org.nutz.dao.test.DaoCase;

public class AutoGenerateValueTest extends DaoCase {

    @Override
    protected void before() {
        dao.create(Killer.class, true);
        dao.create(Resident.class, true);
    }

    @Override
    protected void after() {}

    @Test
    public void test_var_in_prev() {
        dao.create(Pet2.class, true);

        // TODO Oracle 那个 @Prev 有问题，暂时先忽略
        // TODO HSQL 也不认这样的语句 SELECT 'dog.xb'
        // TODO DB2 同样不认
        if (dao.meta().isOracle() || dao.meta().isHsql()
                || dao.meta().isDB2())
            return;
        pojos.initPet();
        Pet2 pet = new Pet2();
        pet.setName("xb").setAge(10);

        dao.insert(pet);

        assertEquals("dog.xb", pet.getNickName());
    }

    @Test
    public void test_simple() {
        Resident xh = new Resident("XH");
        Resident xw = new Resident("XW");
        dao.insert(xh);
        dao.insert(xw);

        Killer zzh = new Killer("zzh");
        zzh.kill(xh);
        zzh.kill(xw);
        dao.insertRelation(zzh, "killeds");
        dao.insert(zzh);

        zzh = dao.fetch(Killer.class, zzh.getId());
        assertEquals(2, zzh.getKilledCount());
        assertEquals("XW", zzh.getLastKillName());

        Resident gfw = new Resident("GFW");
        Resident bs = new Resident("BS");
        dao.insert(gfw);
        dao.insert(bs);

        Killer cnm = new Killer("CNM");
        cnm.kill(gfw);
        cnm.kill(bs);
        dao.insertRelation(cnm, "killeds");
        dao.insert(cnm);

        cnm = dao.fetch(Killer.class, cnm.getId());
        assertEquals(2, cnm.getKilledCount());
        assertEquals("GFW", cnm.getLastKillName());
    }

    // 重构后的 Dao 将不再支持这个用例
    // TODO 在正式发布 1.b.38 时删除这个用例
    // @Test
    public void test_insert_prev_by_fastInsert() {
        Resident xh = new Resident("XH");
        Resident xw = new Resident("XW");
        dao.insert(xh);
        dao.insert(xw);

        Killer zzh = new Killer("zzh");
        zzh.kill(xh);
        zzh.kill(xw);
        dao.insertRelation(zzh, "killeds");
        dao.fastInsert(zzh);

        zzh = dao.fetch(Killer.class, "zzh");
        assertEquals(2, zzh.getKilledCount());
        assertEquals("XW", zzh.getLastKillName());
    }

}
