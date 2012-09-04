package org.nutz.dao.test.mapping;

import static org.junit.Assert.*;

import org.junit.Test;
import org.nutz.dao.Cnd;
import org.nutz.dao.test.DaoCase;
import org.nutz.dao.test.meta.Base;
import org.nutz.dao.test.meta.Country;
import org.nutz.lang.Strings;

public class LinksGeneralTest extends DaoCase {

    @Test
    public void fetch_all_links() {
        Base b = dao.fetchLinks(dao.fetch(Base.class, "red"), null);
        assertEquals("red", b.getName());
        assertEquals("China", b.getCountry().getName());
        assertEquals(6, b.getFighters().size());
        assertEquals(3, b.getPlatoons().size());
    }

    @Test(expected=Exception.class)
    public void insert_links_with_exception() {
        Base b = Base.make("Red");
        dao.insert(b);
        b.setCountry(Country.make(Strings.dup('C', 52)));
        dao.insertLinks(b, "country");
    }

    @Test
    public void test_fetchLinks_cnd() {
        Base b = dao.fetchLinks(dao.fetch(Base.class, "red"), "wavebands", Cnd.where("value", ">", -1).asc("name"));
        assertNotNull(b);
        
        Base b2 = dao.fetchLinks(dao.fetch(Base.class, "red"), "platoons", Cnd.where("id", ">", 0).limit(1, 20).asc("name"));
        assertNotNull(b2);
        
    }
    
    protected void before() {
        pojos.initData();
    }
}
