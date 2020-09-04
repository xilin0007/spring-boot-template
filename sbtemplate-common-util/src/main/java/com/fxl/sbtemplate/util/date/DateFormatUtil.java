package com.fxl.sbtemplate.util.date;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * <p>项目名称: payconter</p>
 * <p>文件名称: DateFormatUtil</p>
 * <p>文件描述: </p>
 * <p>创建日期: 2019/08/21 10:29</p>
 * <p>创建用户：huaxu</p>
 */
public class DateFormatUtil {

    private static final ZoneId zone = ZoneId.systemDefault();
    private static DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static Instant getInstant(LocalDateTime localDateTime) {
        return localDateTime.atZone(zone).toInstant();
    }

    private static Instant getInstant(Date date) {
        return date.toInstant();
    }

    private static LocalDateTime getLocaDate(Date date) {
        return LocalDateTime.ofInstant(getInstant(date), zone);
    }

    public static Date parse(String date) {
        return Date.from(getInstant(LocalDateTime.parse(date, format)));
    }

    public static String format(LocalDateTime date) {
        return format.format(date);
    }

    public static String format(Date date) {
        return getLocaDate(date).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public static String format(Date date, String pattern) {
        return getLocaDate(date).format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String formatMM_dd(Date date) {
        return getLocaDate(date).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    /**
     *  @Date: 10:53 AM 2019/8/21
     *  @Author: huaxu
     *  @Description:
     *           某天零点
     */
    public static Date getTodayStart(long daysToAdd) {
        return Date.from(getInstant(LocalDateTime.of(LocalDate.now().plusDays(daysToAdd), LocalTime.MIN)));
    }

    /**
     *  @Date: 10:58 AM 2019/8/21
     *  @Author: huaxu
     *  @Description:
     *           某天最后一秒
     */
    public static Date getTodayEnd(long daysToAdd) {
        return Date.from(getInstant(LocalDateTime.of(LocalDate.now().plusDays(daysToAdd), LocalTime.MAX)));
    }
}