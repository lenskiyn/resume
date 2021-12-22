package com.sbrf.telegrambot.service;

import com.sbrf.telegrambot.config.Config;

import java.util.Calendar;
import java.util.TimeZone;

public class ServiceDateSupplier {

    public static int getLastDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    public static int getCurrentDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static int getTomorrowDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return day + 1;
    }

    public static int getYesterday() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return day - 1;
    }

    public static int getCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
        return calendar.get(Calendar.MONTH);
    }

    public static String getDayOfWeekByDate(int date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
        calendar.set(Calendar.DATE, date);
        return Config.DAYS_OF_WEEK[calendar.get(Calendar.DAY_OF_WEEK) - 1];
    }
}
