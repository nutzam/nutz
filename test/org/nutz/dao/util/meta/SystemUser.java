package org.nutz.dao.util.meta;

import java.util.List;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Many;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.One;
import org.nutz.dao.entity.annotation.Table;

@Table("t_daoup_user")
public class SystemUser {

    @Id
    private long id;
    
    @Name
    @Column("nm")
    private String name;
    
    /**
     * 关联字段必须写出来
     */
    @Column("t_Id")
    private int teamId;
    
    /**
     * @One的field对应当前类(SystemUser)的关联属性teamId
     */
    @One(target=SystemTeam.class, field = "teamId")/**在field填写的本*/
    private SystemTeam team;
    
    /**
     * 在@Many中, field对应的是目标类(SystemJob)中的关联属性userId, 注意与@One的区别
     */
    @Many(target=SystemJob.class, field="userId")
    private List<SystemJob> jobs;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public SystemTeam getTeam() {
        return team;
    }

    public void setTeam(SystemTeam team) {
        this.team = team;
    }

    public List<SystemJob> getJobs() {
        return jobs;
    }

    public void setJobs(List<SystemJob> jobs) {
        this.jobs = jobs;
    }
    
    
}
