package org.nutz.dao.test.entity;

import static org.junit.Assert.*;

import java.sql.Timestamp;

import org.junit.Test;
import org.nutz.dao.Cnd;
import org.nutz.dao.TableName;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.Record;
import org.nutz.dao.impl.entity.field.ManyManyLinkField;
import org.nutz.dao.test.DaoCase;
import org.nutz.dao.test.meta.Tank;
import org.nutz.dao.test.meta.issue1286.Issue1286;
import org.nutz.http.Request.METHOD;
import org.nutz.lang.random.R;

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

    @Test
    public void test_issue_1286() {
        dao.create(Issue1286.class, true);
        Record record = new Record();
        record.set(".table", "t_issue_1286");
        record.set("*+id", 0).set("name", R.UU32()).set("method", METHOD.POST).set("t", null);
        record.set(".t.type", Timestamp.class);
        dao.insert(record);
        record = dao.fetch("t_issue_1286", Cnd.NEW());
        assertEquals("POST", record.get("method"));
    }
}
