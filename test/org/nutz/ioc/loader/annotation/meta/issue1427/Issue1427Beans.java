package org.nutz.ioc.loader.annotation.meta.issue1427;

import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;

@IocBean
public class Issue1427Beans {

    // 注入的应该是issue1427AAA
    @IocBean(name="issue_1427_mapa")
    public NutMap makeMapA(Issue1427Top issue1427AAA) {
        return new NutMap("obj", issue1427AAA);
    }
    
    // 注入的应该是refer:issue1427BBB
    @IocBean(name="issue_1427_mapb")
    public NutMap makeMapB(@Inject("refer:issue1427BBB") Issue1427Top issue1427BBB) {
        return new NutMap("obj", issue1427BBB);
    }
    
    // 注入的应该是refer:issue1427BBB
    @IocBean(name="issue_1427_mapc")
    public NutMap makeMapC(@Inject("refer:issue1427BBB") Issue1427Top issue1427AAA) {
        return new NutMap("obj", issue1427AAA);
    }
}
