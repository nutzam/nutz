package org.nutz.dao;

import java.util.List;

import org.junit.Test;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.test.DaoCase;
import org.nutz.dao.test.meta.TestMenu;
import org.nutz.dao.test.meta.TestRole;
import org.nutz.dao.test.meta.TestUser;
import org.nutz.dao.util.TestUtil;
import org.nutz.json.Json;
import org.nutz.lang.random.R;

import static org.junit.Assert.*;

/**
 * @Author: Haimming
 * @Date: 2020-01-16 16:47
 * @Version 1.0
 */
public class QueryResultTest extends DaoCase {

    @Test
    public void queryResultTest() {
        // 若必要的数据不存在，则初始化数据库
        dao.create(TestMenu.class, true);
        String menuJson = TestUtil.getFileData("data/menu.json");
        List<TestMenu> menuList =  Json.fromJsonAsList(TestMenu.class,menuJson);
        for(TestMenu menu:menuList){
            dao.fastInsert(menu);
        }
        Pager pager = dao.createPager(0, 10);
        Cnd cnd = Cnd.NEW();
        cnd.orderBy("menu_name", "asc");
        pager.setRecordCount(dao.count(TestMenu.class, cnd));
        List<TestMenu> list = dao.query(TestMenu.class, cnd, pager);
        pager.setRecordCount(dao.count(TestMenu.class, cnd));
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


    @Test
    public void crudTest() {
        // 若必要的数据不存在，则初始化数据库
        dao.create(TestMenu.class, true);
        dao.create(TestRole.class, true);
        dao.create(TestUser.class, true);
        String menuJson = TestUtil.getFileData("data/menu.json");
        List<TestMenu> menuList =  Json.fromJsonAsList(TestMenu.class,menuJson);
        for(TestMenu menu:menuList){
            dao.fastInsert(menu);
        }
        List<TestMenu> testMenus = dao.query(TestMenu.class, Cnd.NEW());
        assertNotNull(testMenus);

        String roleJson = TestUtil.getFileData("data/role.json");
        List<TestRole> roleList = Json.fromJsonAsList(TestRole.class,roleJson);
        for(TestRole role:roleList){
            role.setId(R.UU32().toLowerCase());
            dao.fastInsert(role);
            if("admin".equals(role.getRoleKey())){
                role.setMenus(menuList);
                dao.insertRelation(role, "menus");
            }
        }
//        List<TestRole> testRoles = dao.query(TestRole.class, Cnd.NEW());
        TestRole testRole =dao.fetch(TestRole.class,Cnd.NEW().and("role_key","=","admin"));
        testRole =dao.fetchLinks(testRole,"menus");
        assertNotNull(testRole);
        assertNotNull(testRole.getMenus());

        String userJson = TestUtil.getFileData("data/user.json");
        List<TestUser> userList = Json.fromJsonAsList(TestUser.class,userJson);
        for(TestUser user:userList){
            user.setId(R.UU32().toLowerCase());
//            dao.fastInsert(user);
            if("admin".equals(user.getLoginName())){
                user.setRoles(roleList);
                dao.insertWith(user, "roles");
            }else {
                dao.fastInsert(user);
            }
        }
        TestUser testUser = dao.fetch(TestUser.class,Cnd.NEW().and("login_name","=","admin"));
        testUser =dao.fetchLinks(testUser,"roles");
        assertNotNull(testUser);
        assertNotNull(testUser.getRoles());
        testUser =dao.clearLinks(testUser,"roles");
        testUser =dao.fetchLinks(testUser,"roles");
        assertTrue(testUser.getRoles().size() ==0);
    }
}
