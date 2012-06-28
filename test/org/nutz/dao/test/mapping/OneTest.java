package org.nutz.dao.test.mapping;

import java.util.List;

import org.junit.Test;

import org.nutz.dao.test.DaoCase;
import org.nutz.dao.test.meta.Base;
import org.nutz.dao.test.meta.Country;
import org.nutz.lang.Lang;

import static org.junit.Assert.*;

public class OneTest extends DaoCase {

    @Override
    protected void before() {
        pojos.initData();
    }

    @Override
    protected void after() {}

    @Test
    public void fetch_links() {
        Base b = dao.fetchLinks(dao.fetch(Base.class, "red"), "country");
        assertEquals("China", b.getCountry().getName());
    }

    @Test
    public void fetch_links_as_list() {
        List<Base> b = dao.fetchLinks(Lang.list(dao.fetch(Base.class, "red")), "country");
        assertEquals("China", b.get(0).getCountry().getName());
    }

    @Test
    public void delete_links() {
        Base b = dao.fetchLinks(dao.fetch(Base.class, "red"), "country");
        dao.deleteLinks(b, "country");
        assertEquals(1, dao.count(Country.class));
    }

    @Test
    public void delete_links_as_list() {
        List<Base> b = dao.fetchLinks(Lang.list(dao.fetch(Base.class, "red")), "country");
        dao.deleteLinks(b, "country");
        assertEquals(1, dao.count(Country.class));
    }

    @Test
    public void delete_with() {
        Base b = dao.fetchLinks(dao.fetch(Base.class, "red"), "country");
        dao.deleteWith(b, "country");
        assertEquals(1, dao.count(Country.class));
        assertEquals(1, dao.count(Base.class));
    }

    @Test
    public void delete_with_as_list() {
        List<Base> b = dao.fetchLinks(Lang.list(dao.fetch(Base.class, "red")), "country");
        dao.deleteWith(b, "country");
        assertEquals(1, dao.count(Country.class));
        assertEquals(1, dao.count(Base.class));
    }

    @Test
    public void clear_links() {
        Base b = dao.fetch(Base.class, "red");
        dao.clearLinks(b, "country");
        assertEquals(1, dao.count(Country.class));
    }

    @Test
    public void clear_links_as_list() {
        List<Base> b = dao.fetchLinks(Lang.list(dao.fetch(Base.class, "red")), "country");
        dao.clearLinks(b, "country");
        assertEquals(1, dao.count(Country.class));
    }

    @Test
    public void update_links() {
        Base b = dao.fetchLinks(dao.fetch(Base.class, "red"), "country");
        int lv = b.getLevel();
        b.setLevel(45);
        b.getCountry().setName("ABC");
        dao.updateLinks(b, "country");
        b = dao.fetch(Base.class, "red");
        assertEquals(lv, b.getLevel());
        Country c = dao.fetch(Country.class, b.getCountryId());
        assertEquals("ABC", c.getName());
    }

    @Test
    public void update_links_as_list() {
        List<Base> b = dao.fetchLinks(Lang.list(dao.fetch(Base.class, "red")), "country");
        int lv = b.get(0).getLevel();
        b.get(0).setLevel(45);
        b.get(0).getCountry().setName("ABC");
        dao.updateLinks(b, "country");
        b = Lang.list(dao.fetch(Base.class, "red"));
        assertEquals(lv, b.get(0).getLevel());
        Country c = dao.fetch(Country.class, b.get(0).getCountryId());
        assertEquals("ABC", c.getName());
    }

    @Test
    public void update_with() {
        Base b = dao.fetchLinks(dao.fetch(Base.class, "red"), "country");
        b.setLevel(6);
        b.getCountry().setName("ABC");
        dao.updateWith(b, "country");
        b = dao.fetch(Base.class, b.getName());
        assertEquals(6, b.getLevel());
        Country c = dao.fetch(Country.class, b.getCountryId());
        assertEquals("ABC", c.getName());
    }

    @Test
    public void update_with_as_list() {
        List<Base> b = dao.fetchLinks(Lang.list(dao.fetch(Base.class, "red")), "country");
        b.get(0).setLevel(45);
        b.get(0).getCountry().setName("ABC");
        dao.updateWith(b, "country");
        b = Lang.list(dao.fetch(Base.class, b.get(0).getName()));
        assertEquals(45, b.get(0).getLevel());
        Country c = dao.fetch(Country.class, b.get(0).getCountryId());
        assertEquals("ABC", c.getName());
    }

}
