package org.nutz.dao;

import java.sql.Connection;

public interface ConnCallback {

    void invoke(Connection conn) throws Exception;

}
