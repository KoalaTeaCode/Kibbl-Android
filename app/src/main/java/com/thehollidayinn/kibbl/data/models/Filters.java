package com.thehollidayinn.kibbl.data.models;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by krh12 on 2/2/2017.
 */

public class Filters {

    private static Filters sharedInstance;

    public static Filters getSharedInstance() {
        if (sharedInstance == null) {
            sharedInstance = new Filters();
        }
        return sharedInstance;
    }

    public String location;
    public String breed;
    public String age;
    public Integer ageIndex = 0;
    public String gender;
    public Integer genderIndex = 0;
    public String type;
    public String lastUpdatedBefore;
    public String createdAtBefore;
    public Integer activeMainPage = 0;
    public Calendar startDate;
    public Calendar endDate;
    public Locale locale;

    public Map<String, String> toMap() {
        Map<String, String> filterMap = new HashMap<>();

        if (location != null && !location.equals("All")) {
            filterMap.put("location", location);
        }

        if (type != null && !type.equals("All")) {
            filterMap.put("type", type);
        }

        if (breed != null && !breed.equals("All")) {
            filterMap.put("breed", breed);
        }

        if (age != null && !age.equals("All")) {
            filterMap.put("age", age);
        }

        if (gender != null && !gender.equals("All")) {
            filterMap.put("gender", gender);
        }

        if (lastUpdatedBefore != null && !lastUpdatedBefore.equals("All")) {
            filterMap.put("lastUpdatedBefore", lastUpdatedBefore);
        }

        if (createdAtBefore != null && !createdAtBefore.equals("All")) {
            filterMap.put("createdAtBefore", createdAtBefore);
        }

        if (locale == null) {
            return filterMap;
        }

        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'", locale); // Quoted "Z" to indicate UTC, no timezone offset
//        df.setTimeZone(tz);

        if (startDate != null) {
            startDate.set(Calendar.HOUR, 0);
            startDate.set(Calendar.MINUTE, 0);
            startDate.set(Calendar.SECOND, 0);

            Date start = startDate.getTime();
            String nowAsISO = df.format(start);
            filterMap.put("startDate", nowAsISO);
        }

        if (endDate != null) {
            endDate.set(Calendar.HOUR, 0);
            endDate.set(Calendar.MINUTE, 0);
            endDate.set(Calendar.SECOND, 0);
            Date end = endDate.getTime();
            String nowAsISO = df.format(end);
            filterMap.put("endDate", nowAsISO);
        }

        return filterMap;
    }
}
