package org.nutz.dao.util.meta;

import java.util.Date;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.EL;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Prev;
import org.nutz.dao.entity.annotation.Table;

/**
 * Nutz的Pojo不需要继承任何类,也不需要实现任何接口.<p/>
 * 当然也可以跟nutzbook一样做个公共超类及实现Serializable<p/>
 * 超类的字段也会继承哦,但@Table等类注解不能
 * @author wendal(wendal1985@gmail.com)
 *
 */
@Table("t_test_simple_pojo")
public class SimplePojo {

    /**
     * Nutz区分数值型主键和字符型主键. nutz并不检查数据库中是不是真正的主键哦.
     */
    
    /**
     * 数值型主键, 如果不是自增或触发器生成,就需要写成 @Id(auto=false), 否则插入数据时将忽略这个属性.
     */
    @Id
    private int id; /*字段属性名称是任意,并非强制叫id*/
    
    /**
     * 字符型主键.
     */
    @Name
    private String name;

    // @Prev用于在插入前为字段赋值,非必选.
    @Prev(els=@EL("uuid()")) // 通过调用nutz内部的el引擎,使用内置的uuid方法生成UUID字符串
    // 还可以调用数据库语句来赋值 @Prev(@Sql("select xxx.next_var from dual"))
    @Column("uk") // java属性与数据库字段不同名时,就需要用@Column设置
    private String uckey;
    
    // uckey设置了@Column,nutz就要求其他所有数据库字段都需要加@Column,即使它们的与表字段同名
    @Column
    @ColDefine(width=1024) // @ColDefine 大部分情况下用于建表时指定长度,
    // 转为表定义就是 varchar(1024)
    // 注意一下,如果不是字符串,应加上type=ColType.XXX 选一个最接近的类型
    private String website;
    
    @Column
    private int age;
    
    @Column("ct")
    private Date createTime;
    
    /*
     * 如果添加了任何带参数的构造方法(除非是单个ResultSet参数),否则都需要添加一个无参数的构造方法
     */
    
    public SimplePojo() {}
    

    public SimplePojo(String name, String website, int age) {
        this.name = name;
        this.website = website;
        this.age = age;
        this.createTime = new Date();
    }
    
    // ====== 一堆getter/setter
    
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

    public String getUckey() {
        return uckey;
    }

    public void setUckey(String uckey) {
        this.uckey = uckey;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    
    
}
