package org.nutz.json.meta;

import org.nutz.json.JsonField;

public class JENObj {

    @JsonField("id")
    private long objId;

    private String name;

    private int age;

    public long getObjId() {
        return objId;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public void setObjId(long objId) {
        this.objId = objId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }
    
    public void setName2(String name){
        this.name = name;
    }

}
