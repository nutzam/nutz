package org.nutz.dao.test.meta.issue928;

import java.util.Set;

import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

@Table("t_bean_with_set")
public class BeanWithSet {

    @Id
    private long id;
    
    private Set<Long> uids;
    
    private Set<String> names;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Set<Long> getUids() {
        return uids;
    }

    public void setUids(Set<Long> uids) {
        this.uids = uids;
    }

    public Set<String> getNames() {
        return names;
    }

    public void setNames(Set<String> names) {
        this.names = names;
    }
    
    
}
