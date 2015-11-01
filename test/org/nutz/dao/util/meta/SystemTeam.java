package org.nutz.dao.util.meta;

import java.util.List;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.ManyMany;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;

@Table("t_daoup_team")
public class SystemTeam {

    @Id
    private int id;
    @Name
    @Column("nm")
    private String name;
    
    /**
     * @ManyMany, from是关联表中的本类(SystemTeam)的字段名, to就关联类(SystemJob)的字段名咯
     */
    @ManyMany(target=SystemJob.class, from="t_id", to="job_id", relation="t_daoup_team_job")
    private List<SystemJob> jobs;

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

    public List<SystemJob> getJobs() {
        return jobs;
    }

    public void setJobs(List<SystemJob> jobs) {
        this.jobs = jobs;
    }
    
}
