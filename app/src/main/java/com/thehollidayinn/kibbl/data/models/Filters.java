package com.thehollidayinn.kibbl.data.models;

import java.util.HashMap;
import java.util.Map;

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
    public String gender;
    public String type;
    public String lastUpdatedBefore;

    public Map<String, String> toMap() {
        Map<String, String> filterMap = new HashMap<>();

        if (location != null && !location.equals("All")) {
            filterMap.put("zipCode", location);
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

        return filterMap;
    }
}
