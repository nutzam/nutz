package org.nutz.dao.test.normal;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.junit.Test;
import org.nutz.dao.ConnCallback;
import org.nutz.dao.FieldFilter;
import org.nutz.dao.test.DaoCase;
import org.nutz.dao.test.meta.Pet;
import org.nutz.lang.Strings;
import org.nutz.trans.Atom;
import org.nutz.trans.Molecule;
import org.nutz.trans.Trans;

public class InsertTest extends DaoCase {

    /**
     * Github Issue #131
     */
    @Test
    public void test_fastinsert_as_rollback() {
        dao.create(Pet.class, true);
        // 在插入数据中有错误 ...
        final Pet[] pets = Pet.create(10);
        pets[4].setName(Strings.dup('t', 1024));
        try {
            Trans.exec(new Atom() {
                public void run() {
                    dao.fastInsert(pets);
                }
            });
        }
        catch (RuntimeException e) {}
        assertEquals(0, dao.count(Pet.class));
        // 插入后，主动抛出一个错误，看回滚不回滚
        try {
            final Pet[] pets2 = Pet.create(10);
            Trans.exec(new Atom() {
                public void run() {
                    dao.fastInsert(pets2);
                    throw new RuntimeException("I am ok!");
                }
            });
        }
        catch (RuntimeException e) {
            assertEquals("I am ok!", e.getMessage());
        }
        assertEquals(0, dao.count(Pet.class));
    }

    /**
     * Github Issue #131， 这个证明了 JDBC 驱动支持事务
     */
    @Test
    public void test_fastInsert_rollback_jdbc() {
        dao.create(Pet.class, true);
        try {
            Trans.exec(new Molecule<Object>() {
                public void run() {
                    dao.run(new ConnCallback() {
                        public void invoke(Connection conn) throws Exception {
                            PreparedStatement ps = conn.prepareStatement("INSERT INTO t_pet(name) VALUES(?)");
                            for (int i = 0; i < 100; i++) {
                                ps.setString(1, "XXXXX" + i);
                                ps.addBatch();
                            }
                            ps.execute();
                        }
                    });
                    throw new RuntimeException();
                }
            });
        }
        catch (Throwable e) {}
        assertEquals(0, dao.count(Pet.class));
    }

    @Test
    public void test_insert_by_fieldfilter() {
        dao.create(Pet.class, true);
        final Pet pet = Pet.create("xb");
        pet.setNickName("xiaobai");
        FieldFilter.create(Pet.class, "^id|name$").run(new Atom() {
            public void run() {
                dao.insert(pet);
            }
        });
        Pet xb = dao.fetch(Pet.class, "xb");
        assertNull(xb.getNickName());
    }

    @Test
    public void test_insert_by_el() {
        dao.create(Pet3.class, false);
        Pet3 p = new Pet3();
        dao.insert(p);
        assertTrue(p.getId() > 0);
        assertTrue(p.getName().startsWith("N_"));
    }

}
