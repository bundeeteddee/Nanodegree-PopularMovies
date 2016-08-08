package com.tinytinybites.popularmovies.app.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by bundee on 8/4/16.
 */
public class DateUtil {

    /**
     * Given a string (i.e. 2016-05-15), return a Date object
     * @param dateString
     * @return
     */
    public static final Date GetSimpleDate(String dateString){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = df.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();

        }
        return date;
    }

    /**
     * Given a date, return the year in string
     * @param date
     * @return
     */
    public static final String GetYear(Date date){
        DateFormat df = new SimpleDateFormat("yyyy");
        return df.format(date);
    }
}
