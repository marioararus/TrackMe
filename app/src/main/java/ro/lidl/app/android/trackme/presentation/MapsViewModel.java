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
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

/**
 * Created by Marioara Rus on 4/26/2018.
 */
public class MapsViewModel extends AndroidViewModel {

    private FusedLocationProviderClient fusedLocationProviderClient;
    private MutableLiveData<Location> locationLiveData;

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
//        fusedLocationProviderClient.requestLocationUpdates()
    }
}
