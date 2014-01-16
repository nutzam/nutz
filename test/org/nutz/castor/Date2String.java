/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.nutz.castor;

import org.nutz.castor.castor.DateTimeCastor;
import org.nutz.lang.Times;

/**
 *
 * @author guitar
 */
public class Date2String extends DateTimeCastor<java.util.Date, String> {

    @Override
    public String cast(java.util.Date src, Class<?> toType, String... args) {
        return Times.sD(src);
    }

}