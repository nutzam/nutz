package org.nutz.dao.test.meta.issue_ix3il;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.nutz.dao.test.DaoCase;
import org.nutz.json.Json;
import org.nutz.lang.Streams;

public class IssueIX3IL extends DaoCase {

    @Test
    public void test_ix3il() {
        dao.create(MidFunctionRole.class, true);
        
        List<MidFunctionRole> list = Json.fromJsonAsList(MidFunctionRole.class, Streams.utf8r(getClass().getResourceAsStream("test_data.json")));
        assertEquals(12, list.size());
        dao.insert(list);
        
        assertEquals(12, dao.count(MidFunctionRole.class));
    }
}
