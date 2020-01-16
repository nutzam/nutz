package org.nutz.dao.test.entity;

import org.junit.Assert;
import org.junit.Test;
import org.nutz.dao.entity.Record;

import java.util.Set;

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


    @Test
    public void testRecordRemove(){
        Record record = new Record();
        record.set("loginName","loginName");
        record.set("nickName","nickName");
        TPojo pojo = record.toPojo(TPojo.class);
        Assert.assertEquals(pojo.loginName,"loginName");
        record.remove("loginName");
        pojo = record.toPojo(TPojo.class);
        Assert.assertEquals(pojo.loginName,null);
    }

    @Test
    public void getColumnNamesTest() {
        Record record = new Record();
        record.set("loginName","loginName");
        record.set("nickName","nickName");
        Set<String> column = record.getColumnNames();
        System.out.println(column.toString());
        Assert.assertEquals(column.toString(),"[loginname, nickname]");
    }

    @Test
    public void getTest() {
        Record record = new Record();
        record.set("loginName","loginName");
        record.set("age",18);
        record.set("long",500);
        record.set("double",10.1);
        record.set("year","testests");

        int age = record.getInt("age");
        int year = record.getInt("year",2000);
        int dft = record.getInt("dft",30);
        double double1 = record.getDouble("double");
        double double2 = record.getDouble("double1",100.1);
        long long1 = record.getLong("long");
        long long2 = record.getLong("long1",600);
        Assert.assertEquals(age,18);
        Assert.assertEquals(year,2000);
        Assert.assertEquals(dft,30);
//        System.out.println(Double.compare(double1,10.1));
        Assert.assertEquals(0,Double.compare(double1,10.1));
        Assert.assertEquals(0,Double.compare(double2,100.1));
        Assert.assertEquals(long1,500);
        Assert.assertEquals(long2,600);
    }


}
