package com.xinyirun.scm.excel.bean.test.demo;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author zxh
 * @date 2019-08-07
 */
public class ToBeanObject {
    public static void main(String[] args) {
        String s = longToDate(Long.parseLong("1603350927000"));
        System.out.println(s);
    }

    public static String longToDate(long lo){

        Date date = new Date(lo);

        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return sd.format(date);

    }
}
