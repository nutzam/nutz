package org.nutz.dao.test.normal;

import static org.junit.Assert.*;

import org.junit.Test;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.test.DaoCase;

public class CustomizedFieldNameTest extends DaoCase {

    @Test
    public void test_insert_update_chain_with_another_column_name() {
        dao.create(Killer.class, true);

        dao.insert(Killer.class, Chain    .make("name", "Peter Zhang")
                                        .add("lastKillName", "xh")
                                        .add("killedCount", 1));
        Killer zzh = dao.fetch(Killer.class);
        assertEquals("Peter Zhang", zzh.getName());
        assertEquals("xh", zzh.getLastKillName());
        assertEquals(1, zzh.getKilledCount());

        dao.update(    Killer.class,
                    Chain.make("lastKillName", "lw").add("killedCount", 2),
                    Cnd.where("id", "=", zzh.getId()));
        zzh = dao.fetch(Killer.class);
        assertEquals("Peter Zhang", zzh.getName());
        assertEquals("lw", zzh.getLastKillName());
        assertEquals(2, zzh.getKilledCount());
    }
}
