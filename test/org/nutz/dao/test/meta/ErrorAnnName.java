package org.nutz.dao.test.meta;

import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;

@Table("error_ann_name")
public class ErrorAnnName {

    @Name
    private long name;

    public long getName() {
        return name;
    }

    public void setName(long name) {
        this.name = name;
    }
    
    
}
