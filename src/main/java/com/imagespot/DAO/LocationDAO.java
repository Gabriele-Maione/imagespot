package com.imagespot.DAO;

import com.imagespot.Model.Location;

import java.util.List;

public interface LocationDAO {

    void addLocation(Location location);
    List<String> getTop(String location); //get top locations (cities, countries, places)
    Location getLocation(int idLocation);
}
