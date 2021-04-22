package org.nutz.dao.texp;

import org.junit.Test;
import org.nutz.dao.Cnd;
import org.nutz.dao.Condition;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.sql.Criteria;
import org.nutz.dao.sql.OrderBy;
import org.nutz.dao.test.DaoCase;
import org.nutz.dao.util.cri.Exps;
import org.nutz.dao.util.cri.SqlExpression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2021/4/21
 */
public class LamdaCndTest extends DaoCase {

    private Entity<?> en;

    protected void before() {
        en = dao.create(Worker.class, true);
    }

    protected void after() {
    }

    @Test
    public void test_gt_like() {
        Condition c = Cnd.where(Worker::getId, ">", 45)
                .and(Worker::getName, "LIKE", "%ry%");
        String exp = "WHERE wid>45 AND wname LIKE '%ry%'";
        assertEquals(exp, c.toSql(en).trim());
    }

    @Test
    public void test_bracket() {
        String exp = "WHERE (wid>45) AND wname LIKE '%ry%'";
        Condition c0 = Cnd.where(Cnd.exps("id", ">", 45))
                .and("name", "LIKE", "%ry%");
        Condition c1 = Cnd.where(Cnd.exps(Worker::getId, ">", 45))
                .and(Worker::getName, "LIKE", "%ry%");
        assertEquals(exp, c0.toSql(en).trim());
        assertEquals(exp, c1.toSql(en).trim());
    }

    @Test
    public void test_order() {
        Condition c = Cnd.orderBy()
                .asc(Worker::getId)
                .desc(Worker::getName)
                .asc(Worker::getAge)
                .desc(Worker::getWorkingDay);
        String exp = "ORDER BY wid ASC, wname DESC, age ASC, days DESC";
        assertEquals(exp, c.toSql(en).trim());
        assertEquals(exp, c.toSql(en).trim());
    }

    @Test
    public void test_like_in() {
        int[] ages = {4, 7, 9};
        SqlExpression e = Cnd.exps(Worker::getAge, ">", 35).and(Worker::getId, "<", 47);
        SqlExpression e2 = Cnd.exps(Worker::getName, "\tLIKE ", "%t%")
                .and(Worker::getAge, "IN  \n\r", ages)
                .or(e);
        Condition c = Cnd.where(Worker::getId, "=", 37)
                .and(e)
                .or(e2)
                .asc(Worker::getAge)
                .desc(Worker::getId);
        String exp = "WHERE wid=37 AND (age>35 AND wid<47) OR (wname LIKE '%t%' AND age IN (4,7,9) OR (age>35 AND wid<47)) ORDER BY age ASC, wid DESC";
        assertEquals(exp, c.toSql(en).trim());
    }


    @Test
    public void test_in_by_int_array() {
        int[] ids = {3, 5, 7};
        Condition c = Cnd.where(Worker::getId, "iN", ids);
        String exp = "WHERE id IN (3,5,7)";
        assertEquals(exp, c.toSql(null).trim());
    }

    @Test
    public void test_in_by_int_list() {
        List<Integer> list = new ArrayList<Integer>();
        list.add(3);
        list.add(5);
        list.add(7);
        Condition c = Cnd.where(Worker::getId, "iN", list);
        String exp = "WHERE id IN (3,5,7)";
        assertEquals(exp, c.toSql(null).trim());
    }


    @Test
    public void test_add_other_or_method_by_github_issuse_148() {
        SqlExpression e1 = Cnd.exps(Worker::getCity, "=", "beijing")
                .or(Worker::getCity, "=", "shanghai")
                .or(Worker::getCity, "=", "guangzhou")
                .or(Worker::getCity, "=", "shenzhen");
        SqlExpression e2 = Cnd.exps(Worker::getAge, ">", 18).and(Worker::getAge, "<", 30);
        String exp = "WHERE (ct='beijing' OR ct='shanghai' OR ct='guangzhou' OR ct='shenzhen') AND (age>18 AND age<30)";
        assertEquals(exp, Cnd.where(e1).and(e2).toSql(en).trim());
    }


    /**
     * Criteria 接口测试List&lt;String&gt;
     */
    @Test
    public void test_not_in_by_criteria_string_list() {
        List<String> ids = new ArrayList<String>();
        ids.add("bj");
        ids.add("sh");
        ids.add("gz");
        ids.add("sz");
        Criteria cri = Cnd.cri();
        cri.where().andNotInStrList(Worker::getId, ids);
        assertEquals(" WHERE id NOT IN ('bj','sh','gz','sz')", cri.toString());
    }

    /**
     * test_orderby
     */
    @Test
    public void test_orderby() {
        OrderBy orderBy0 = Cnd.orderBy().desc(Worker::getCity).asc(Worker::getCity);
        OrderBy orderBy1 = Cnd.orderBy().desc("ct").asc("ct");
        assertEquals(orderBy0.toSql(en), orderBy1.toSql(en));
    }

    /**
     * test_orderby
     */
    @Test
    public void test_groupby() {
        OrderBy orderBy0 = Cnd.cri().groupBy(Worker::getCity,Worker::getId);
        OrderBy orderBy1 = Cnd.cri().groupBy("ct","id");
        assertEquals(orderBy0.toSql(en), orderBy1.toSql(en));
    }

    /**
     * test_exps_insql2
     */
    @Test
    public void test_exps_insql2() {
        Cnd cnd0 = Cnd.where(Exps.inSql2(Worker::getId, "select user_id from role where id in (%s)", Arrays.asList(1,2,3)));
        Cnd cnd1 = Cnd.where(Exps.inSql2("wid", "select user_id from role where id in (%s)", Arrays.asList(1,2,3)));
        assertEquals(cnd0.toSql(en), cnd1.toSql(en));
    }

}
