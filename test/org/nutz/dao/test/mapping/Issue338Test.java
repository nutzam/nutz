package org.nutz.dao.test.mapping;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.nutz.dao.test.DaoCase;
import org.nutz.dao.test.meta.issue338.Ask;
import org.nutz.dao.test.meta.issue338.AskReply;

public class Issue338Test extends DaoCase {

    
    @Test
    public void test_deleteWith_many() {
        dao.create(Ask.class, true);
        dao.create(AskReply.class, true);
        Ask ask = new Ask();
        ask.setTemptitle("ABC");
        ask.setTitle("ABC");
        
        List<AskReply> askReplies = new ArrayList<AskReply>();
        askReplies.add(new AskReply(ask.getAskId()));
        askReplies.add(new AskReply(ask.getAskId()));
        ask.setReplys(askReplies);
        
        dao.insertWith(ask, "replys");
        
        ask.setReplys(null);
        dao.deleteWith(ask, "replys");
        assertEquals(2, dao.count(AskReply.class));
        
        ask = dao.fetchLinks(ask, null);
        dao.deleteWith(ask, "replys");
        assertEquals(0, dao.count(AskReply.class));
    }
}
