package com.mmall.util;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author Jason
 * Create in 2018-06-05 20:20
 */
public class DateTimeUtil {

    public static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";

    // str-Date
    public static Date strParseDate(String str) {
        DateTimeFormatter sdf = DateTimeFormat.forPattern(DEFAULT_FORMAT);
        DateTime dateTime = sdf.parseDateTime(str);
        return dateTime.toDate();
    }

    // Date-str
    public static String dateParseStr(Date date) {
        if (date == null)
            return StringUtils.EMPTY;

        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_FORMAT);
        return sdf.format(date);
    }

    public static void main(String[] args) {
        System.out.println(DateTimeUtil.dateParseStr(new Date()));
        System.out.println(DateTimeUtil.strParseDate("2018-06-05 20:33:21"));
    }
}
