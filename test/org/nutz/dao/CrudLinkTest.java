package org.nutz.dao;

import org.junit.Test;
import org.nutz.dao.test.DaoCase;
import org.nutz.dao.test.meta.TestMenu;
import org.nutz.dao.test.meta.TestRole;
import org.nutz.dao.util.TestUtil;
import org.nutz.json.Json;
import org.nutz.lang.random.R;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Haiming
 * @date 2020/8/14 18:50 AM
 */
public class CrudLinkTest extends DaoCase {

    @Test
    public void insertWithTest() {

        dao.create(TestMenu.class, true);
        dao.create(TestRole.class, true);
        String menuJson = TestUtil.getFileData("data/menu.json");
        List<TestMenu> menuList = Json.fromJsonAsList(TestMenu.class, menuJson);
        String roleJson = TestUtil.getFileData("data/role.json");
        List<TestRole> roleList = Json.fromJsonAsList(TestRole.class, roleJson);
        for (TestRole role : roleList) {
            role.setId(R.UU32().toLowerCase());
            if ("admin".equals(role.getRoleKey())) {
                role.setMenus(menuList);
                dao.insertWith(role, "menus");
            } else {
                dao.fastInsert(role);
            }
        }
        TestRole testRole = dao.fetch(TestRole.class, Cnd.NEW().and("role_key", "=", "admin"));
        testRole = dao.fetchLinks(testRole, "menus");
        assertNotNull(testRole);
        assertNotNull(testRole.getMenus());
    }


    @Test
    public void insertLinksTest() {

        dao.create(TestMenu.class, true);
        dao.create(TestRole.class, true);
        String menuJson = TestUtil.getFileData("data/menu.json");
        List<TestMenu> menuList = Json.fromJsonAsList(TestMenu.class, menuJson);
        String roleJson = TestUtil.getFileData("data/role.json");
        List<TestRole> roleList = Json.fromJsonAsList(TestRole.class, roleJson);
        for (TestRole role : roleList) {
            role.setId(R.UU32().toLowerCase());
            dao.fastInsert(role);
            if ("admin".equals(role.getRoleKey())) {
                role.setMenus(menuList);
                dao.insertLinks(role, "menus");
            }
        }
        TestRole testRole = dao.fetch(TestRole.class, Cnd.NEW().and("role_key", "=", "admin"));
        testRole = dao.fetchLinks(testRole, "menus");
        assertNotNull(testRole.getMenus());
    }


    @Test
    public void insertRelationTest() {

        dao.create(TestMenu.class, true);
        dao.create(TestRole.class, true);
        String menuJson = TestUtil.getFileData("data/menu.json");
        List<TestMenu> menuList = Json.fromJsonAsList(TestMenu.class, menuJson);
        for (TestMenu menu : menuList) {
            dao.fastInsert(menu);
        }
        String roleJson = TestUtil.getFileData("data/role.json");
        List<TestRole> roleList = Json.fromJsonAsList(TestRole.class, roleJson);
        for (TestRole role : roleList) {
            role.setId(R.UU32().toLowerCase());
            dao.fastInsert(role);
            if ("admin".equals(role.getRoleKey())) {
                role.setMenus(menuList);
                dao.insertRelation(role, "menus");
            }
        }
        TestRole testRole = dao.fetch(TestRole.class, Cnd.NEW().and("role_key", "=", "admin"));
        testRole = dao.fetchLinks(testRole, "menus");
        assertNotNull(testRole.getMenus());
    }

    @Test
    public void updateWithTest() {

        dao.create(TestMenu.class, true);
        dao.create(TestRole.class, true);
        String menuJson = TestUtil.getFileData("data/menu.json");
        List<TestMenu> menuList = Json.fromJsonAsList(TestMenu.class, menuJson);
        String roleJson = TestUtil.getFileData("data/role.json");
        List<TestRole> roleList = Json.fromJsonAsList(TestRole.class, roleJson);
        for (TestRole role : roleList) {
            role.setId(R.UU32().toLowerCase());
            if ("admin".equals(role.getRoleKey())) {
                role.setMenus(menuList);
                dao.insertWith(role, "menus");
            } else {
                dao.fastInsert(role);
            }
        }
        TestRole testRole = dao.fetch(TestRole.class, Cnd.NEW().and("role_key", "=", "admin"));
        testRole = dao.fetchLinks(testRole, "menus");
        testRole.setRoleName("hhhhh");
        TestMenu tmpMenu = testRole.getMenus().get(0);
        for(TestMenu m:testRole.getMenus()){
            if(tmpMenu.getId().equals(m.getId())){
                m.setMenuName("test");
            }
        }
        dao.updateWith(testRole, "menus");
        testRole = dao.fetch(TestRole.class, Cnd.NEW().and("role_key", "=", "admin"));
        assertEquals("hhhhh",testRole.getRoleName());
        TestMenu testMenu = dao.fetch(TestMenu.class, tmpMenu.getId());
        assertEquals("test",testMenu.getMenuName());
    }


    @Test
    public void updateLinksTest() {

        dao.create(TestMenu.class, true);
        dao.create(TestRole.class, true);
        String menuJson = TestUtil.getFileData("data/menu.json");
        List<TestMenu> menuList = Json.fromJsonAsList(TestMenu.class, menuJson);
        String roleJson = TestUtil.getFileData("data/role.json");
        List<TestRole> roleList = Json.fromJsonAsList(TestRole.class, roleJson);
        for (TestRole role : roleList) {
            role.setId(R.UU32().toLowerCase());
            if ("admin".equals(role.getRoleKey())) {
                role.setMenus(menuList);
                dao.insertWith(role, "menus");
            } else {
                dao.fastInsert(role);
            }
        }
        TestRole testRole = dao.fetch(TestRole.class, Cnd.NEW().and("role_key", "=", "admin"));
        testRole = dao.fetchLinks(testRole, "menus");
        TestMenu tmpMenu = testRole.getMenus().get(0);
        for(TestMenu m:testRole.getMenus()){
            if(tmpMenu.getId().equals(m.getId())){
                m.setMenuName("test");
            }
        }
        dao.updateLinks(testRole, "menus");
        TestMenu testMenu = dao.fetch(TestMenu.class, tmpMenu.getId());
        assertEquals("test",testMenu.getMenuName());
    }

    @Test
    public void deleteWithTest() {

        dao.create(TestMenu.class, true);
        dao.create(TestRole.class, true);
        String menuJson = TestUtil.getFileData("data/menu.json");
        List<TestMenu> menuList = Json.fromJsonAsList(TestMenu.class, menuJson);
        for (TestMenu menu : menuList) {
            dao.fastInsert(menu);
        }
        String roleJson = TestUtil.getFileData("data/role.json");
        List<TestRole> roleList = Json.fromJsonAsList(TestRole.class, roleJson);
        for (TestRole role : roleList) {
            role.setId(R.UU32().toLowerCase());
            dao.fastInsert(role);
            if ("admin".equals(role.getRoleKey())) {
                role.setMenus(menuList);
                dao.insertRelation(role, "menus");
            }
        }
        TestRole testRole = dao.fetch(TestRole.class, Cnd.NEW().and("role_key", "=", "admin"));
        testRole = dao.fetchLinks(testRole, "menus");

        dao.deleteWith(testRole, "menus");
        testRole = dao.fetch(TestRole.class, Cnd.NEW().and("role_key", "=", "admin"));
        assertNull(testRole);
        for(TestMenu m:menuList){
            TestMenu tmp =dao.fetch(TestMenu.class,m.getId());
            assertNull(tmp);
        }
    }


    @Test
    public void deleteLinksTest() {

        dao.create(TestMenu.class, true);
        dao.create(TestRole.class, true);
        String menuJson = TestUtil.getFileData("data/menu.json");
        List<TestMenu> menuList = Json.fromJsonAsList(TestMenu.class, menuJson);
        for (TestMenu menu : menuList) {
            dao.fastInsert(menu);
        }
        String roleJson = TestUtil.getFileData("data/role.json");
        List<TestRole> roleList = Json.fromJsonAsList(TestRole.class, roleJson);
        for (TestRole role : roleList) {
            role.setId(R.UU32().toLowerCase());
            dao.fastInsert(role);
            if ("admin".equals(role.getRoleKey())) {
                role.setMenus(menuList);
                dao.insertRelation(role, "menus");
            }
        }
        TestRole testRole = dao.fetch(TestRole.class, Cnd.NEW().and("role_key", "=", "admin"));
        testRole = dao.fetchLinks(testRole, "menus");

        dao.deleteLinks(testRole, "menus");
        testRole = dao.fetch(TestRole.class, Cnd.NEW().and("role_key", "=", "admin"));
        assertNotNull(testRole);
        for(TestMenu m:menuList){
            TestMenu tmp =dao.fetch(TestMenu.class,m.getId());
            assertNull(tmp);
        }
    }

    @Test
    public void clearLinksTest() {
        dao.create(TestMenu.class, true);
        dao.create(TestRole.class, true);
        String menuJson = TestUtil.getFileData("data/menu.json");
        List<TestMenu> menuList = Json.fromJsonAsList(TestMenu.class, menuJson);
        for (TestMenu menu : menuList) {
            dao.fastInsert(menu);
        }
        String roleJson = TestUtil.getFileData("data/role.json");
        List<TestRole> roleList = Json.fromJsonAsList(TestRole.class, roleJson);
        for (TestRole role : roleList) {
            role.setId(R.UU32().toLowerCase());
            dao.fastInsert(role);
            if ("admin".equals(role.getRoleKey())) {
                role.setMenus(menuList);
                dao.insertRelation(role, "menus");
            }
        }
        TestRole testRole = dao.fetch(TestRole.class, Cnd.NEW().and("role_key", "=", "admin"));
        testRole = dao.fetchLinks(testRole, "menus");

        dao.clearLinks(testRole, "menus");
        testRole = dao.fetch(TestRole.class, Cnd.NEW().and("role_key", "=", "admin"));
        assertNotNull(testRole);
        assertNull(testRole.getMenus());
        for(TestMenu m:menuList){
            TestMenu tmp =dao.fetch(TestMenu.class,m.getId());
            assertNotNull(tmp);
        }
    }

    @Test
    public void fetchByJoinTest() {
        dao.create(TestMenu.class, true);
        dao.create(TestRole.class, true);
        String menuJson = TestUtil.getFileData("data/menu.json");
        List<TestMenu> menuList = Json.fromJsonAsList(TestMenu.class, menuJson);
        for (TestMenu menu : menuList) {
            dao.fastInsert(menu);
        }
        String roleJson = TestUtil.getFileData("data/role.json");
        List<TestRole> roleList = Json.fromJsonAsList(TestRole.class, roleJson);
        for (TestRole role : roleList) {
            role.setId(R.UU32().toLowerCase());
            dao.fastInsert(role);
            if ("admin".equals(role.getRoleKey())) {
                role.setMenus(menuList);
                dao.insertRelation(role, "menus");
            }
        }
        TestRole testRole = dao.fetchByJoin(TestRole.class,"menus", Cnd.NEW().and("role_key", "=", "admin"));
        assertNotNull(testRole);
        assertNotNull(testRole.getMenus());
    }

    @Test
    public void queryByJoinTest() {
        dao.create(TestMenu.class, true);
        dao.create(TestRole.class, true);
        String menuJson = TestUtil.getFileData("data/menu.json");
        List<TestMenu> menuList = Json.fromJsonAsList(TestMenu.class, menuJson);
        for (TestMenu menu : menuList) {
            dao.fastInsert(menu);
        }
        String roleJson = TestUtil.getFileData("data/role.json");
        List<TestRole> roleList = Json.fromJsonAsList(TestRole.class, roleJson);
        for (TestRole role : roleList) {
            role.setId(R.UU32().toLowerCase());
            dao.fastInsert(role);
            if ("admin".equals(role.getRoleKey())) {
                role.setMenus(menuList);
                dao.insertRelation(role, "menus");
            }
        }
        List<TestRole> testRoles = dao.queryByJoin(TestRole.class,"menus", Cnd.NEW().and("role_key", "=", "admin"));
        for(TestRole r:testRoles){
            assertNotNull(r);
            assertNotNull(r.getMenus());
        }
    }

    @Test
    public void countByJoinTest() {
        dao.create(TestMenu.class, true);
        dao.create(TestRole.class, true);
        String menuJson = TestUtil.getFileData("data/menu.json");
        List<TestMenu> menuList = Json.fromJsonAsList(TestMenu.class, menuJson);
        for (TestMenu menu : menuList) {
            dao.fastInsert(menu);
        }
        String roleJson = TestUtil.getFileData("data/role.json");
        List<TestRole> roleList = Json.fromJsonAsList(TestRole.class, roleJson);
        for (TestRole role : roleList) {
            role.setId(R.UU32().toLowerCase());
            dao.fastInsert(role);
            if ("admin".equals(role.getRoleKey())) {
                role.setMenus(menuList);
                dao.insertRelation(role, "menus");
            }
        }
        int data = dao.countByJoin(TestRole.class,"menus", Cnd.NEW().and("role_key", "=", "admin"));
        assertTrue(data > 0);
    }
}
