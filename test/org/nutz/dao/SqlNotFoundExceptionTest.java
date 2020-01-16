package org.nutz.dao;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @Author: Haimming
 * @Date: 2020-01-15 10:58
 * @Version 1.0
 */
public class SqlNotFoundExceptionTest {

    @Test
    public void SqlNotFoundExceptionTest(){
        try {
            throw new SqlNotFoundException("key");
        }catch (Exception e){
            System.out.println(e.getMessage());
            assertEquals("fail to find SQL 'key'!", e.getMessage());
        }
    }

}