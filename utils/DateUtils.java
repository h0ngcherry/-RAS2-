package com.example.documentReview.utils;


import org.thymeleaf.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    public static String  getDateStr(Date date){
        if(date == null){
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = sdf.format(date);
        return dateStr;
    }

    public static Date strToDate(String date) {
        if(StringUtils.isEmpty(date)){
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date result = null;
        try {
            result = sdf.parse(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
