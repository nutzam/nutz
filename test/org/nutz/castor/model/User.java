package org.nutz.castor.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * create by zhouwenqing 2017/7/26 .
 */
public class User extends BaseTreeEntity<Long, User> implements Serializable {


    private static final long serialVersionUID = -1943438256837838021L;

    private String userName;
    private Integer age;



    private Map params;

    private List list;


    public List getList() {
        return list;
    }

    public void setList(List list) {
        this.list = list;
    }

    public Map getParams() {
        return params;
    }

    public void setParams(Map params) {
        this.params = params;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
