package es.upm.dam2016g6.shout.fragments;


import android.app.Fragment;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

import es.upm.dam2016g6.shout.R;
import es.upm.dam2016g6.shout.activities.MainActivity;
import es.upm.dam2016g6.shout.model.ChatRoom;
import es.upm.dam2016g6.shout.model.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class DiscoveryFragment extends android.support.v4.app.Fragment {


    MapView mMapView;
    private GoogleMap googleMap;
    private HashMap<String, Marker> mUserMarkers; // Markers for users, indexed by key
    private HashMap<String, Marker> mChatroomMarkers; // Markers for chatrooms, indexed by keya
    private MainActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_discovery, container, false);

         // Add toolbar to fragment (contains logout button)
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar_discovery);
        toolbar.setTitle("Discovery");
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        mainActivity = (MainActivity)DiscoveryFragment.this.getActivity();

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                // For showing a move to my location button
                if (ActivityCompat.checkSelfPermission(DiscoveryFragment.this.getContext(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(DiscoveryFragment.this.getContext(),
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    DiscoveryFragment.this.getActivity()
                            .requestPermissions(new String[]{"android.permission.ACCESS_FINE_LOCATION"}, getTargetRequestCode());
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }

                googleMap.setMyLocationEnabled(true);

                // Draw already loaded users and chatrooms
                for (User user : DiscoveryFragment.this.mainActivity.usersInRange.values()) {
                    drawUser(user);
                }

                for (ChatRoom chatroom : DiscoveryFragment.this.mainActivity.chatroomsInRange.values()) {
                    drawChatroom(chatroom);
                }

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(mainActivity.mCurrentLocation.getLatitude(), mainActivity.mCurrentLocation.getLongitude()))
                        .zoom((float) (mainActivity.discoveryRadius * 1.5))
                        .build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    public DiscoveryFragment() {
        // Required empty public constructor
    }

    public void drawUser(User user) {
        Marker userMarker = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(user.location.latitude, user.location.longitude))
                .title(user.name)
                .snippet(user.uid));
        mUserMarkers.put(user.uid, userMarker);
    }

        public void drawChatroom(ChatRoom chatroom) {
        Marker chatroomMarker= googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(chatroom.location.latitude, chatroom.location.longitude))
                .title(chatroom.title)
                .snippet(chatroom.uid));
        chatroomMarker.setAlpha((float) 0.5);
        mUserMarkers.put(chatroom.uid, chatroomMarker);
    }

    public void undrawUser(String userKey) {
        Marker marker = mUserMarkers.get(userKey);
        if (marker != null) {
            mUserMarkers.remove(userKey);
            marker.remove();
        }
    }

    public void undrawChatroom(String chatroomKey) {
        Marker marker = mChatroomMarkers.get(chatroomKey);
        if (marker != null) {
            mChatroomMarkers.remove(chatroomKey);
            marker.remove();
        }
    }

    public void updateUserLocation(String userKey, GeoLocation location) {
        Marker marker = mUserMarkers.get(userKey);
        if (marker != null)
            marker.setPosition(new LatLng(location.latitude, location.longitude));
    }
}
