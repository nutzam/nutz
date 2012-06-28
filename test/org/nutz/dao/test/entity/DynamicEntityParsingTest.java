package org.nutz.dao.test.entity;

import static org.junit.Assert.*;

import org.junit.Test;

import org.nutz.dao.TableName;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.impl.entity.field.ManyManyLinkField;
import org.nutz.dao.test.DaoCase;
import org.nutz.dao.test.meta.Tank;

public class DynamicEntityParsingTest extends DaoCase {

    @Test
    public void tank_many_many_link_test() {
        pojos.initPlatoon(1);
        TableName.set(1);
        Entity<?> en = dao.getEntity(Tank.class);
        ManyManyLinkField link = (ManyManyLinkField) en.getLinkFields("members").get(0);
        assertEquals("dao_d_m_soldier_tank_1", link.getRelationName());
        assertEquals("id", link.getLinkedPkNames()[0]);
        assertEquals("name", link.getLinkedPkNames()[1]);
        TableName.clear();
        pojos.dropPlatoon(1);
    }

}
