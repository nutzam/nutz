package org.nutz.dao.test.mapping;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.Test;

import org.nutz.dao.impl.entity.field.*;

import org.nutz.dao.test.DaoCase;
import org.nutz.dao.test.meta.Base;
import org.nutz.dao.test.meta.Fighter;

public class ManyManyTest extends DaoCase {

    @Override
    protected void before() {
        pojos.initData();
    }

    @Override
    protected void after() {}

    @Test
    public void fetch_links() {
        Base b = dao.fetchLinks(dao.fetch(Base.class, "red"), "fighters");
        assertEquals(6, b.getFighters().size());
        assertEquals(1, b.countFighter(Fighter.TYPE.SU_35));
    }

    @Test
    public void delete_links() {
        Base b = dao.fetchLinks(dao.fetch(Base.class, "red"), "fighters");
        dao.deleteLinks(b, "fighters");
        assertEquals(7, dao.count(Fighter.class));
        assertEquals(    7,
                        dao.count(((ManyManyLinkField) dao.getEntity(Base.class)
                                                            .getLinkFields("fighters")
                                                            .get(0)).getRelationName()));
    }

    @Test
    public void delete_links_partly() {
        Base b = dao.fetchLinks(dao.fetch(Base.class, "red"), "fighters");
        b.getFighters().remove(0);
        b.getFighters().remove(0);
        dao.deleteLinks(b, "fighters");
        assertEquals(9, dao.count(Fighter.class));
    }

    @Test
    public void delete_with() {
        Base b = dao.fetchLinks(dao.fetch(Base.class, "red"), "fighters");
        dao.deleteWith(b, "fighters");
        assertEquals(7, dao.count(Fighter.class));
        assertEquals(1, dao.count(Base.class));
    }

    @Test
    public void delete_with_partly() {
        Base b = dao.fetchLinks(dao.fetch(Base.class, "red"), "fighters");
        b.getFighters().remove(0);
        b.getFighters().remove(0);
        dao.deleteWith(b, "fighters");
        assertEquals(9, dao.count(Fighter.class));
        assertEquals(1, dao.count(Base.class));
    }

    @Test
    public void clear_links() {
        Base b = dao.fetch(Base.class, "red");
        dao.clearLinks(b, "fighters");
        assertEquals(13, dao.count(Fighter.class));
        assertEquals(    7,
                        dao.count(((ManyManyLinkField) dao.getEntity(Base.class)
                                                            .getLinkFields("fighters")
                                                            .get(0)).getRelationName()));
    }

    @Test
    public void update_links() {
        Base b = dao.fetchLinks(dao.fetch(Base.class, "blue"), "fighters");
        int lv = b.getLevel();
        b.setLevel(45);
        for (Iterator<Fighter> it = b.getFighters().iterator(); it.hasNext();) {
            it.next().setType(Fighter.TYPE.F22);
        }
        dao.updateLinks(b, "fighters");
        b = dao.fetch(Base.class, "blue");
        assertEquals(lv, b.getLevel());
        b = dao.fetchLinks(dao.fetch(Base.class, "blue"), "fighters");
        assertEquals(7, b.countFighter(Fighter.TYPE.F22));
    }

    @Test
    public void update_with() {
        Base b = dao.fetchLinks(dao.fetch(Base.class, "blue"), "fighters");
        b.setLevel(45);
        for (Iterator<Fighter> it = b.getFighters().iterator(); it.hasNext();) {
            it.next().setType(Fighter.TYPE.F22);
        }
        dao.updateWith(b, "fighters");
        b = dao.fetch(Base.class, "blue");
        assertEquals(45, b.getLevel());
        b = dao.fetchLinks(dao.fetch(Base.class, "blue"), "fighters");
        assertEquals(7, b.countFighter(Fighter.TYPE.F22));
    }

}
