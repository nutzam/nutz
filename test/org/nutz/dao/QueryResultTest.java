package org.nutz.dao;

import org.junit.Test;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.test.DaoCase;
import org.nutz.dao.test.meta.Pet;
import org.nutz.json.Json;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @Author: Haimming
 * @Date: 2020-01-16 16:47
 * @Version 1.0
 */
public class QueryResultTest extends DaoCase {

    @Test
    public void queryResultTest() {
        if (!dao.meta().isH2())
            return; // Only test for h2 now
        dao.create(Pet.class, true);
        dao.insert(Pet.create("wendal"));
        Pager pager = dao.createPager(0, 10);
        Cnd cnd =Cnd.NEW();
        cnd.orderBy("name","asc");
        pager.setRecordCount(dao.count(Pet.class, cnd));
        List<Pet> list = dao.query(Pet.class, cnd, pager);
        pager.setRecordCount(dao.count(Pet.class, cnd));
        System.out.println(pager.toString());
        QueryResult result = new QueryResult(list, pager);
        assertNotNull(result);
        assertNotNull(result.getList());
        assertNotNull(result.convertList(Json.class));
        assertNotNull(result.getPager());
        result.setList(null);
        assertNull(result.getList());
        result.setPager(null);
        assertNull(result.getPager());

    }
}