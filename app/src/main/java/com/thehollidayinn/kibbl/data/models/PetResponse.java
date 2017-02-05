
package com.thehollidayinn.kibbl.data.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PetResponse {

    @SerializedName("total")
    @Expose
    private Integer total;

    @SerializedName("pets")
    @Expose
    private List<Pet> pets = null;

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<Pet> getPets() {
        return pets;
    }

    public void setPets(List<Pet> pets) {
        this.pets = pets;
    }

}
