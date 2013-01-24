package org.nutz.dao.test.meta.issue338;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.EL;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.One;
import org.nutz.dao.entity.annotation.Prev;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.lang.random.R;

@Table("tb_ic_ask_reply")
public class AskReply {

    @Name
    @Prev(els = {@EL("$me.uuid()")})
    @Column("reply_id")
    private String replyId;// 回复ID

    @Column("ask_id")
    private String askId;// 问吧ID
    
    public AskReply() {
    }
    
    public AskReply(String askId) {
        setAskId(askId);
    }

    @One(target = Ask.class, field = "askId")
    private Ask ask;

    public String getReplyId() {
        return replyId;
    }
    
    public String uuid() {
        return R.UU16();
    }

    public void setReplyId(String replyId) {
        this.replyId = replyId;
    }

    public String getAskId() {
        return askId;
    }

    public void setAskId(String askId) {
        this.askId = askId;
    }

    public Ask getAsk() {
        return ask;
    }

    public void setAsk(Ask ask) {
        this.ask = ask;
    }

}