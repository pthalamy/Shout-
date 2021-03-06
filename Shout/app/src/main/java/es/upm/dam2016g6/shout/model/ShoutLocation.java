package es.upm.dam2016g6.shout.model;

import com.google.firebase.database.IgnoreExtraProperties;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.Exclude;

/**
 * Created by pthalamy on 4/1/17.
 */

@IgnoreExtraProperties
public class ShoutLocation {

    public double latitude;
    public double longitude;

    public ShoutLocation() {

    }

    public ShoutLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Exclude
    public LatLng getShoutLocation(){
        return new LatLng(latitude,longitude);
    }
}
