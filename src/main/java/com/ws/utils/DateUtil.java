package com.ws.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    public static final String YMD = "yyyy-MM-dd";
    public static final String YMDHMS = "yyyy-MM-dd HH:mm:ss";

    public static String getNowYMD() {
        return formatDate(new Date(), YMD);
    }

    public static String getNowYMDHMS() {
        return formatDate(new Date(), YMDHMS);
    }

    public static String getYMD(Date date) {
        return formatDate(date, YMD);
    }

    public static String getYMDHMS(Date date) {
        return formatDate(date, YMDHMS);
    }

    public static String formatDate(Date date, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

}
