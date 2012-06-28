package org.nutz.ioc.json.pojo;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.nutz.lang.Strings;

public class Animal {

    public Animal() {}

    public Animal(String name) {
        this.name = name;
    }

    private int age;

    private float attact;

    private String name;

    private Animal another;

    private AnimalRace race;

    private Calendar birthday;

    private Animal[] enemies;

    private Map<String, Integer> map;

    private List<Object> misc;

    private Map<String, Animal> relations;

    private int fetchTime;

    private int deposeTime;

    private int createTime;

    public Animal getAnother() {
        return another;
    }

    public int getFetchTime() {
        return fetchTime;
    }

    public int getDeposeTime() {
        return deposeTime;
    }

    public int getCreateTime() {
        return createTime;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public float getAttact() {
        return attact;
    }

    public void setAttact(float attact) {
        this.attact = attact;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AnimalRace getRace() {
        return race;
    }

    public void setRace(AnimalRace race) {
        this.race = race;
    }

    public Calendar getBirthday() {
        return birthday;
    }

    public void setBirthday(Calendar birthday) {
        this.birthday = birthday;
    }

    public Animal[] getEnemies() {
        return enemies;
    }

    public void setEnemies(Animal[] enemies) {
        this.enemies = enemies;
    }

    public Map<String, Integer> getMap() {
        return map;
    }

    public void setMap(Map<String, Integer> map) {
        this.map = map;
    }

    public List<Object> getMisc() {
        return misc;
    }

    public void setMisc(List<Object> misc) {
        this.misc = misc;
    }

    public Map<String, Animal> getRelations() {
        return relations;
    }

    public void setRelations(Map<String, Animal> relations) {
        this.relations = relations;
    }

    public String showName(String prefix, int num, String name) {
        return Strings.dup(prefix, num) + name;
    }

    public void onFetch() {
        fetchTime++;
    }

    public void onCreate() {
        createTime++;
    }

    public void onDepose() {
        deposeTime++;
    }

}
