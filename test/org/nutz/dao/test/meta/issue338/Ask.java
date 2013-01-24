package org.nutz.dao.test.meta.issue338;

import java.util.List;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.EL;
import org.nutz.dao.entity.annotation.Many;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Prev;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.lang.random.R;

@Table("tb_ic_ask")
public class Ask {

    @Name
    @Prev(els = {@EL("$me.uuid()")})
    @Column("ask_id")
    private String askId;// 问吧ID

    @Column("user_id")
    private String userId;// 提问人ID

    @Column("title")
    private String title;// 提问标题
    private String temptitle;// 提问标题

    @Many(target = AskReply.class, field = "askId")
    private List<AskReply> replys;

    public String getAskId() {
        return askId;
    }

    public void setAskId(String askId) {
        this.askId = askId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTemptitle() {
        return temptitle;
    }

    public void setTemptitle(String temptitle) {
        this.temptitle = temptitle;
    }

    public List<AskReply> getReplys() {
        return replys;
    }

    public void setReplys(List<AskReply> replys) {
        this.replys = replys;
    }
    
    public String uuid() {
        return R.UU16();
    }
}