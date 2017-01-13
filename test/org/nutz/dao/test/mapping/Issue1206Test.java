package org.nutz.dao.test.mapping;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.nutz.dao.test.DaoCase;
import org.nutz.dao.test.meta.issue1206.Issue1206Master;
import org.nutz.dao.test.meta.issue1206.Issue1206Pet;
import org.nutz.lang.random.R;

public class Issue1206Test extends DaoCase {

    @Test
    public void test_issue_1206() {
        dao.drop(Issue1206Master.class);
        dao.drop(Issue1206Pet.class);

        dao.create(Issue1206Master.class, false);
        dao.create(Issue1206Pet.class, false);

        Issue1206Master master = new Issue1206Master();
        master.setName("wendal");
        List<Issue1206Pet> pets = new ArrayList<Issue1206Pet>();
        for (int i = 0; i < 10; i++) {
            Issue1206Pet pet = new Issue1206Pet();
            pet.setName(R.UU32());
            pets.add(pet);
        }
        master.setPets(pets);

        dao.insertWith(master, null);
        assertTrue(master.getId() >= 0);
        
        master = dao.fetch(Issue1206Master.class);
        assertNotNull(master);
        dao.fetchLinks(master, null);
        assertNotNull(master.getPets());
        assertEquals(10, master.getPets().size());
    }
}
