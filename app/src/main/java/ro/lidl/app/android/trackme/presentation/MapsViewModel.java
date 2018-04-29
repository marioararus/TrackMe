package ro.lidl.app.android.trackme.presentation;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.location.Location;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

/**
 * Created by Marioara Rus on 4/26/2018.
 */
public class MapsViewModel extends AndroidViewModel {

    private FusedLocationProviderClient fusedLocationProviderClient;
    private MutableLiveData<Location> locationLiveData;
    private MutableLiveData<List<Location>> locationsLiveData;

    public MapsViewModel(@NonNull Application application) {
        super(application);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient
                (application.getApplicationContext());
        locationLiveData = new MutableLiveData<>();

    }
    @SuppressLint("MissingPermission")
    public LiveData<Location> retrieveLocation(){
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    locationLiveData.setValue(task.getResult());

                } else {
                    Log.e("Eroare", "Locatia este nula");
                }
            }
        });



        return locationLiveData;
    }

    @SuppressLint("MissingPermission")
    public MutableLiveData<List<Location>> getUpdatedLocations() {
        locationsLiveData = new MutableLiveData<>();
        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setFastestInterval(1000);
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if(locationResult != null){
                    locationsLiveData.setValue(locationResult.getLocations());
                }
            }

            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
            }
        },null);
        return locationsLiveData;
    }
}
