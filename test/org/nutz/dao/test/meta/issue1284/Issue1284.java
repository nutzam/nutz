package org.nutz.dao.test.meta.issue1284;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.EL;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Prev;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.lang.random.R;

@Table("t_issue_1284")
public class Issue1284 {

    @Id(auto=false)
    @Prev(els=@EL("$me.xxx()"))
    private Long id;
    
    @Column
    private int age;
    
    public int xxx() {
        return R.random(1, 199);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
