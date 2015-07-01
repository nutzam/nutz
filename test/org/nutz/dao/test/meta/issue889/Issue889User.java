package org.nutz.dao.test.meta.issue889;

import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.One;
import org.nutz.dao.entity.annotation.Table;

@Table("t_issue_user")
public class Issue889User {

    @Id
    private long id;
    
    @Name
    private String name;
    
    @One(target=Issue889UserProfile.class, field="id" , key="userId")
    private Issue889UserProfile profile;

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

    public Issue889UserProfile getProfile() {
        return profile;
    }

    public void setProfile(Issue889UserProfile profile) {
        this.profile = profile;
    }
    
    
}
