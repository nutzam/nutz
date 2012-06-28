package org.nutz.ioc.loader.xml.meta;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class Bee {

    public Bee() {}

    public Bee(String name) {
        this.name = name;
    }

    public Bee(Bee mother, String name) {
        this.mother = mother;
        this.name = name;
    }

    private String name;

    private int age;

    private List<Bee> friends;

    private Calendar birthday;

    private Bee mother;

    private int workCount;

    private boolean dead;
    
    private Map<String,String> map;

    public void work() {
        workCount++;
    }

    public void onBorn() {
        birthday = Calendar.getInstance();
    }

    public void onDead() {
        dead = true;
    }

    public int getWorkTime() {
        return workCount;
    }

    public void setWorkTime(int workTime) {
        this.workCount = workTime;
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public List<Bee> getFriends() {
        return friends;
    }

    public void setFriends(List<Bee> friends) {
        this.friends = friends;
    }

    public Calendar getBirthday() {
        return birthday;
    }

    public void setBirthday(Calendar birthday) {
        this.birthday = birthday;
    }

    public Bee getMother() {
        return mother;
    }

    public void setMother(Bee mother) {
        this.mother = mother;
    }

    public Map<String, String> getMap() {
        return map;
    }
    
    public void setMap(Map<String, String> map) {
        this.map = map;
    }
}
