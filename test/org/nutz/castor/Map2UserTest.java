package org.nutz.castor;

import org.junit.Assert;
import org.junit.Test;
import org.nutz.castor.model.User;
import org.nutz.json.Json;
import org.nutz.lang.Lang;

import java.util.Map;

/**
 * create by zhouwenqing 2017/7/26 .
 */
public class Map2UserTest {

    @Test
    public void testMap2User() throws Exception{


        String jsonStr = "{\n"
                + "    \"id\": 1,\n"
                + "    \"userName\": \"zhou\",\n"
                + "    \"params\":{\"key1\":\"value1\", \"key2\":\"value2\"},\n"
                + "    \"list\":[\"l1\",\"l2\"],\n"
                + "    \"parent\": {\n"
                + "        \"id\": 2,\n"
                + "        \"userName\": \"zhou2\"\n"
                + "    },\n"
                + "    \"childMap\": {\n"
                + "        \"3\": {\n"
                + "            \"id\": 3,\n"
                + "            \"userName\": \"zhou3\"\n"
                + "        },\n"
                + "        \"4\": {\n"
                + "            \"id\": 4,\n"
                + "            \"userName\": \"zhou4\"\n"
                + "        }\n"
                + "    },\n"
                + "    \"children\": [\n"
                + "        {\n"
                + "            \"id\": 3,\n"
                + "            \"userName\": \"zhou3\"\n"
                + "        },\n"
                + "        {\n"
                + "            \"id\": 4,\n"
                + "            \"userName\": \"zhou4\"\n"
                + "        }\n"
                + "    ]\n"
                + "}";






        Map m = (Map) Json.fromJson(jsonStr);

        //直接Map转对象测试
        User user = Lang.map2Object(m, User.class);
        Assert.assertTrue(user.getId() instanceof Long);
        Assert.assertNotNull(user.getUserName());
        Assert.assertNotNull(user.getParent());
        Assert.assertEquals(user.getParent().getId(), new Long(2L));


        //json字符串转对象测试
        user = Json.fromJson(User.class, jsonStr);

        Assert.assertTrue(user.getId() instanceof Long);
        Assert.assertNotNull(user.getUserName());
        Assert.assertNotNull(user.getParent());
        Assert.assertEquals(user.getParent().getId(), new Long(2L));

    }
}
