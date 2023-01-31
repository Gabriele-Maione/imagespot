package com.imagespot.DAO;

import com.imagespot.model.Location;

import java.util.List;

public interface LocationDAO {

    int addLocation(Location location);
    List<String> getTop(String location); //get top locations (cities, countries, places)
    Location getLocation(int idLocation);
}
