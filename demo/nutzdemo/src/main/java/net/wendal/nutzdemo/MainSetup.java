package net.wendal.nutzdemo;

import org.nutz.dao.Dao;
import org.nutz.dao.util.Daos;
import org.nutz.ioc.Ioc;
import org.nutz.lang.Lang;
import org.nutz.lang.random.R;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;

import net.wendal.nutzdemo.bean.User;

public class MainSetup implements Setup {

    public void init(NutConfig nc) {
        Ioc ioc = nc.getIoc();
        Dao dao = ioc.get(Dao.class);
        Daos.createTablesInPackage(dao, getClass(), false);

        if (0 == dao.count(User.class)) {
            User user = new User();
            user.setName("admin");
            user.setSalt(R.UU32());
            user.setPassword(Lang.digest("SHA-256", user.getSalt() + "123456"));
            dao.insert(user);
        }
    }

    public void destroy(NutConfig nc) {}

}
