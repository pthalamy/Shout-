package es.upm.dam2016g6.shout.support;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;

import com.firebase.geofire.GeoLocation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by pthalamy on 5/12/16.
 */

public class Utils {
    public static final int MY_PERMISSIONS_ACCESS_LOCATION = 0x123;
    private static FirebaseDatabase mDatabase;

    public static FirebaseDatabase getDatabase() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
        }

        return mDatabase;
    }

    private static GeoLocation currentLocation;
    public static GeoLocation getCurrentLocation(Activity callerActivity) {
        if (currentLocation == null) {
            LocationManager locationManager = (LocationManager)
                    callerActivity.getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            if (ActivityCompat.checkSelfPermission(callerActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(callerActivity, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                callerActivity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_ACCESS_LOCATION);
                // Do something?
            } else {
                Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
                if (currentLocation != null)
                    currentLocation = new GeoLocation(location.getLatitude(), location.getLongitude());
            }

            if (currentLocation == null)
                currentLocation = new GeoLocation(40.405057, -3.839404); // etsiinf by default
        }

        return currentLocation;
    }

    public static void setCurrentLocation(GeoLocation newLocation) {
        currentLocation = newLocation;
    }

    private static String currentUserUid = null;
    public static String getCurrentUserUid() {
        if (currentUserUid == null)
            currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        return currentUserUid;
    }

    public static void resetCurrentUserUid() {
        currentUserUid = null;
    }

    public static String getDateStringFromTimestamp(long timestamp) {
        SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return sfd.format(new Date(timestamp));
    }

    public static String getUserDistanceToLatLng(double lat, double lng) {
        if (currentLocation == null)
            return "Unavailable";

        Location loc1 = new Location("");
        loc1.setLatitude(currentLocation.latitude);
        loc1.setLongitude(currentLocation.longitude);

        Location loc2 = new Location("");
        loc2.setLatitude(lat);
        loc2.setLongitude(lng);

        float distanceInMeters = loc1.distanceTo(loc2);

        if (distanceInMeters < 1000)
            return (long)distanceInMeters + "m";
        else
            return (long)(distanceInMeters / 1000) + "km";
    }
}
