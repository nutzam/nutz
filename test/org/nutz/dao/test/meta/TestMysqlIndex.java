package org.nutz.dao.test.meta;

import org.nutz.dao.entity.annotation.Index;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.entity.annotation.TableIndexes;

@Table("t_test_mysql_index")
@TableIndexes(@Index(fields = "test1",name = "index_test1"))
public class TestMysqlIndex {

    private String test1;
    @Name
    private String name;
    public String getTest1() {
        return test1;
    }
    public void setTest1(String test1) {
        this.test1 = test1;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    
}