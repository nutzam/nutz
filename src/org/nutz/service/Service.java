package org.nutz.service;

import org.nutz.dao.Dao;

public abstract class Service {

    public Service() {}

    public Service(Dao dao) {
        this.dao = dao;
    }

    private Dao dao;

    public void setDao(Dao dao) {
        this.dao = dao;
    }

    public Dao dao() {
        return dao;
    }

}
