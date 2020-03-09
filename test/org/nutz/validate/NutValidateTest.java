package org.nutz.validate;

import org.junit.Test;
import org.nutz.lang.util.NutMap;

import static org.junit.Assert.*;

/**
 * @Author: Haimming
 * @Date: 2020-01-16 11:40
 * @Version 1.0
 */
public class NutValidateTest {

    @Test
    public void addAll() {
        NutValidate validate = new NutValidate(NutMap.NEW().addv("notNull", true));
        NutMap val = NutMap.NEW();
        val.addv("trim", true);
        validate.addAll(val);
        try {
            String notNull = null;
            validate.check(notNull);
        } catch (NutValidateException e) {
            assertNotNull(e);
        }

        validate = new NutValidate(NutMap.NEW().addv("intRange", "(10,20]"));
        try {
            int intRange = 30;
            validate.check(intRange);
        } catch (NutValidateException e) {
            assertNotNull(e);
        }
        //dateRange
        validate = new NutValidate(NutMap.NEW().addv("dateRange", "(2018-12-02,2018-12-31]"));
        try {
            String dateRange = "2020-01-16";
            validate.check(dateRange);
        } catch (NutValidateException e) {
            assertNotNull(e);
        }
        //maxLength
        validate = new NutValidate(NutMap.NEW().addv("maxLength", 5));
        try {
            String maxLength = "2020-01-16";
            validate.check(maxLength);
        } catch (NutValidateException e) {
            assertNotNull(e);
        }
        validate = new NutValidate(NutMap.NEW().addv("minLength", 20));
        try {
            String minLength = "2020-01-16";
            validate.check(minLength);
        } catch (NutValidateException e) {
            assertNotNull(e);
        }
        validate = new NutValidate(NutMap.NEW().addv("trim", true));
        try {
            String trim = " abc ddc ";
            validate.check(trim);
            System.out.println(trim);
        } catch (NutValidateException e) {
            e.printStackTrace();
            assertNotNull(e);
        }
    }
}