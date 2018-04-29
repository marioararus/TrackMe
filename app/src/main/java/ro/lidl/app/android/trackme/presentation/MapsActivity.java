package ro.lidl.app.android.trackme.presentation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

import ro.lidl.app.android.trackme.R;

import static android.widget.Toast.LENGTH_LONG;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    /**
     * This is the ViewModel of the current activity
     */
    private MapsViewModel mapsViewModel;

    /**
     * This is switch button for user's location
     */
    private Switch locationSwitch;

    /**
     * This is the Observer for the last known location
     */
    private Observer<Location> locationObserver = new Observer<Location>() {
        @Override
        public void onChanged(@Nullable Location location) {
            /**
             * Zooms in the last known location
             */
            if (location != null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 5));
            }
        }
    };

    private Observer<List<Location>> locationsUpdateObserver = new Observer<List<Location>>() {
        @Override
        public void onChanged(@Nullable List<Location> locations) {
            if (locations != null) {
                for (int i = 0; i < locations.size()-1; i++) {
                    mMap.addPolyline(new PolylineOptions().add(
                            new LatLng(locations.get(i).getLatitude(), locations.get(i).getLongitude()),
                            new LatLng(locations.get(i+1).getLatitude(), locations.get(i+1).getLongitude()))
                    .color(Color.RED).width(5));
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        locationSwitch = findViewById(R.id.id_switch);

        mapsViewModel = ViewModelProviders.of(this).get(MapsViewModel.class);
        mapFragment.getMapAsync(this);


        locationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mMap.setMyLocationEnabled(isChecked);
                Toast.makeText(MapsActivity.this, "Location " + (isChecked ? "ON" : "OFF"), LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                mapsViewModel.retrieveLocation().observe(this, locationObserver);
                mapsViewModel.getUpdatedLocations().observe(this, locationsUpdateObserver);
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    0);
        } else {
            mapsViewModel.retrieveLocation().observe(this, locationObserver);
            mapsViewModel.getUpdatedLocations().observe(this, locationsUpdateObserver);
        }
    }

}
