package org.nutz.dao.test.meta.issue1297;

import java.sql.Timestamp;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.EL;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Prev;
import org.nutz.dao.entity.annotation.Table;

@Table("t_issue_insert_or_update_pojo")
public class Issue1297 {

    @Name
    @Prev(els = @EL("uuid()"))
    @ColDefine(width = 32)
    private String uuid;

    @Column
    private int userid;

    @Column
    @ColDefine(width = 50)
    private String keySn;

    @Column
    @Comment("创建时间")
    private Timestamp ct;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getKeySn() {
        return keySn;
    }

    public void setKeySn(String keySn) {
        this.keySn = keySn;
    }

    public Timestamp getCt() {
        return ct;
    }

    public void setCt(Timestamp ct) {
        this.ct = ct;
    }


}