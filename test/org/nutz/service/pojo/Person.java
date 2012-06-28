package org.nutz.service.pojo;

import java.util.List;

import org.nutz.dao.Dao;
import org.nutz.dao.entity.annotation.*;
import org.nutz.lang.meta.Email;

@Table("srv_person")
public class Person {

    public Person() {
        super();
    }

    public Person(String name) {
        this.name = name;
    }

    public Person(String name, String email, int fatherId, int managerId, String masterName) {
        this.name = name;
        this.email = new Email(email);
        this.fatherId = fatherId;
        this.managerId = managerId;
        this.masterName = masterName;
    }

    @Column
    @Id
    private int id;

    @Column
    @Name
    private String name;

    @Column
    @Default("${name}@gmail.com")
    private Email email;

    @Column("fid")
    private int fatherId;

    @Column("mid")
    private int managerId;

    @Column("master")
    private String masterName;

    @One(target = Person.class, field = "masterName")
    private Person master;

    @Many(target = Person.class, field = "masterName")
    private List<Person> students;

    @One(target = Person.class, field = "fatherId")
    private Person father;

    @Many(target = Person.class, field = "fatherId")
    private List<Person> children;

    @Many(target = Person.class, field = "managerId")
    private Person[] employees;

    @Many(target = Profile.class, field = "id")
    private Profile profile;

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public int getManagerId() {
        return managerId;
    }

    public String getMasterName() {
        return masterName;
    }

    public void setMasterName(String masterName) {
        this.masterName = masterName;
    }

    public Person getMaster() {
        return master;
    }

    public void setMaster(Person master) {
        this.master = master;
    }

    public List<Person> getStudents() {
        return students;
    }

    public void setStudents(List<Person> students) {
        this.students = students;
    }

    public void setManagerId(int managerId) {
        this.managerId = managerId;
    }

    public List<Person> getChildren() {
        return children;
    }

    public void setChildren(List<Person> children) {
        this.children = children;
    }

    public Person[] getEmployees() {
        return employees;
    }

    public void setEmployees(Person[] employees) {
        this.employees = employees;
    }

    public int getFatherId() {
        return fatherId;
    }

    public void setFatherId(int fatherId) {
        this.fatherId = fatherId;
    }

    public Person getFather() {
        return father;
    }

    public void setFather(Person father) {
        this.father = father;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Email getEmail() {
        return email;
    }

    public void setEmail(Email email) {
        this.email = email;
    }

    public static void prepareTable(Dao dao) {
        Person p = dao.insert(new Person("zyy", "zyy@unknown.com", 0, 0, null));
        dao.insert(new Profile(p.getId(), "JiLin"));
        p = dao.insert(new Person("yy", "yy@unknown.com", 1, 0, null));
        dao.insert(new Profile(p.getId(), "Shulan"));
        p = dao.insert(new Person("ycs", "sm@163.com", 2, 0, null));
        dao.insert(new Profile(p.getId(), "XiAn"));
        p = dao.insert(new Person("zzh", "zozohtnn@gmail.com", 3, 0, null));
        dao.insert(new Profile(p.getId(), "BeiJing"));
        p = dao.insert(new Person("ydl", "youoo@129.com", 3, 0, null));
        dao.insert(new Profile(p.getId(), "BeiJing"));
        p = dao.insert(new Person("Merry", "merry@zozoh.com", 0, 4, "zzh"));
        dao.insert(new Profile(p.getId(), "New York"));
        p = dao.insert(new Person("John", "john@zozoh.com", 0, 4, "zzh"));
        dao.insert(new Profile(p.getId(), "Hongkong"));
    }

}
