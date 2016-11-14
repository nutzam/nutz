package org.nutz.dao.test.meta.issue1168;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.EL;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Prev;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.lang.random.R;

@Table("nftm_right")
public class Issue1168 {

    @Name
    @Prev(els = @EL("$me.getUUId()"))
    private String id;
    @Column("right_view")
    private String view;
    
    public String getUUId() {
        return R.UU32();
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getView() {
        return view;
    }
    
    public void setView(String view) {
        this.view = view;
    }
    
}