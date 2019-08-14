package org.nutz.dao.test.entity;

import org.junit.Assert;
import org.junit.Test;
import org.nutz.dao.entity.Record;

public class RecordTest {
    @Test
    public void testRecord2Pojo(){
        Record record = new Record();
        record.set("loginName","loginName");
        record.set("nickName","nickName");

        TPojo pojo = record.toPojo(TPojo.class);
        Assert.assertEquals(pojo.loginName,"loginName");
    }

    public static class TPojo{
        private String loginName;
        private String nickName;

        public String getLoginName() {
            return loginName;
        }

        public void setLoginName(String loginName) {
            this.loginName = loginName;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }
    }
}
