package com.thehollidayinn.kibbl.data.models;

import java.util.Date;
import java.util.List;

/**
 * Created by krh12 on 6/10/2017.
 */

public class Updates {
    public String _id;
    public Date checkDate;
    public List<Event> newEvents;
    public List<Event> updatedEvents;
    public List<Pet> newPets;
    public List<Pet> updatedPets;
    public Shelter shelterId;

}
