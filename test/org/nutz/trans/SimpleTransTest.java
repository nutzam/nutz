package org.nutz.trans;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Connection;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.nutz.dao.ConnCallback;
import org.nutz.dao.test.DaoCase;
import org.nutz.dao.test.meta.Pet;
import org.nutz.lang.Lang;

public class SimpleTransTest extends DaoCase {

    @Before
    public void before() {
        dao.create(Company.class, true);
        dao.create(Master.class, true);
        dao.create(Cat.class, true);
        dao.insert(Cat.create("xb"));

    }

    @Test
    public void test_update_rollback() {
        dao.create(Pet.class, true);
        dao.insert(Pet.create("XiaoBai"));
        dao.insert(Pet.create("XiaoHei"));
        final Pet pet1 = dao.fetch(Pet.class,"XiaoBai");
        final Pet pet2 = dao.fetch(Pet.class,"XiaoHei");

        pet1.setName("A");
        pet2.setName("A");
        // Begin transaction        
        Trans.DEBUG = true;
        try {
            Trans.exec(new Atom(){
                public void run() {
                    dao.update(pet1);
                    System.out.println(dao.fetch(Pet.class, pet1.getId()).getName());
                    dao.update(pet2);
                    //throw Lang.noImplement();
                }
            });
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        // End transaction
        System.out.println(dao.fetch(Pet.class, pet1.getId()).getName());
        assertFalse(dao.fetch(Pet.class, pet1.getId()).getName().equals(pet1.getName()));
    }

    @Test
    public void test_batch_update_rollback() {
        final Cat cat1 = dao.fetch(Cat.class, "xb");
        final Cat cat2 = new Cat();
        cat2.setId(cat1.getId() + 1);
        cat2.setMaster(cat1.getMaster());
        cat2.setMasterId(cat1.getMasterId());
        cat2.setName("xb2");
        dao.insert(cat2);
        assertTrue(dao.fetch(Cat.class, "xb2").getName().equals("xb2"));
        try {
            Trans.exec(new Atom() {
                public void run() {
                    cat1.setName("p1");
                    dao.update(cat1);
                    cat2.setName("p2");
                    dao.update(cat2);
                    throw Lang.makeThrow("Quite!!!");
                }
            });
            fail();
        }
        catch (RuntimeException e) {
            e.printStackTrace();
        }
        assertTrue(dao.fetch(Cat.class, "xb").getName().equals("xb"));
        assertTrue(dao.fetch(Cat.class, "xb2").getName().equals("xb2"));
        dao.delete(cat2);
    }


    @Test
    public void test_issue312() {
        Trans.exec(Connection.TRANSACTION_SERIALIZABLE, new Atom(){
            public void run() {
                final Connection[] conns = new Connection[2];
                dao.run(new ConnCallback() {
                    public void invoke(Connection conn) throws Exception {
                        conns[0] = conn;
                    } 
                });
                dao.run(new ConnCallback() {
                    public void invoke(Connection conn) throws Exception {
                        conns[1] = conn;
                    } 
                });
                //必然是同一个对象
                //assertEquals(conns[0], conns[1]);
                assertTrue(conns[0] == conns[1]);
                try {
                    //必然是同一个对象
                    assertTrue(Trans.get().getConnection(ioc.get(DataSource.class)) == conns[1]);
                    assertTrue(Trans.get().getConnection(ioc.get(DataSource.class)) == conns[1]);
                    assertTrue(Trans.get().getConnection(ioc.get(DataSource.class)) == conns[1]);
                    assertTrue(Trans.get().getConnection(ioc.get(DataSource.class)) == conns[1]);

                    //assertEquals(Trans.get().getConnection(ioc.get(DataSource.class)), Trans.get().getConnection(ioc.get(DataSource.class)));
                    
                    assertTrue(Trans.get().getConnection(ioc.get(DataSource.class)) == Trans.get().getConnection(ioc.get(DataSource.class)));
                }
                catch (Throwable e) {
                    throw Lang.wrapThrow(e);
                }
            } 
        });
    }
}
