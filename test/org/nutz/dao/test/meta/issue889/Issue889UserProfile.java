package org.nutz.dao.test.meta.issue889;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;

@Table("t_issue_user_profile")
public class Issue889UserProfile {

    @Id(auto=false)
    @Column("user_id")
    protected long userId;
    
    @Name
    @Column("email")
    private String email;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    
}
