package org.nutz.dao.test.mapping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.nutz.dao.test.DaoCase;
import org.nutz.dao.test.meta.issue1155.SuperPet;
import org.nutz.dao.test.meta.issue1155.SuperPetMaster;
import org.nutz.lang.random.R;

public class Issue1155Test extends DaoCase {

    @Test
    public void test_issue_1155() {
        dao.create(SuperPetMaster.class, true);
        SuperPet spet = new SuperPet();
        spet.setName(R.UU32());
        spet.setAge(31);;
        SuperPetMaster master = new SuperPetMaster();
        master.setName(R.UU32());
        master.setPet(spet);
        dao.insert(master);
        
        SuperPetMaster out = dao.fetch(SuperPetMaster.class, master.getName());
        assertNotNull(out);
        assertNotNull(out.getPet());
        assertEquals(out.getPet().getAge(), spet.getAge());
    }

}
