package lk.javainstitute.swiftex.user.home;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import lk.javainstitute.swiftex.R;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private String currentAddress;
    private Button setStartLocationButton;
    private BroadcastReceiver locationProviderChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_maps);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mapsActivityConstraintLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setStartLocationButton = findViewById(R.id.setStartLocationButton);

        SupportMapFragment supportMapFragment = new SupportMapFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.mapsConstraintLayout, supportMapFragment);
        fragmentTransaction.commit();

        supportMapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        setStartLocationButton.setOnClickListener(this::onSetStartLocationButtonClick);

        // register broadcast receiver
        locationProviderChangeReceiver = new LocationProviderChangeReceiver();
        IntentFilter filter = new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION);
        registerReceiver(locationProviderChangeReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // unregister broadccast receiver
        unregisterReceiver(locationProviderChangeReceiver);
    }

    //location broadcast listener
    private class LocationProviderChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (LocationManager.PROVIDERS_CHANGED_ACTION.equals(intent.getAction())) {
                checkLocationEnabledAndRequestUpdates();
            }
        }
    }

    private void checkLocationEnabledAndRequestUpdates() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (isGpsEnabled || isNetworkEnabled) {
            // Location is enabled
            requestLocationUpdates(mMap);
        } else {
            // Location is disabled
            Toast.makeText(this, "Please enable location services", Toast.LENGTH_SHORT).show();

            //open settings
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);

            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    moveCameraToLocation(mMap, location);
                    getCurrentAddress(location);
                } else {
                    Toast.makeText(this, "Could not get last location. Requesting updates...", Toast.LENGTH_SHORT).show();
                    requestLocationUpdates(mMap);
                }
            });
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 200);
        }

        mMap.setOnMapLongClickListener(latLng -> {
            Location selectedLocation = new Location("");
            selectedLocation.setLatitude(latLng.latitude);
            selectedLocation.setLongitude(latLng.longitude);

            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location"));
            getCurrentAddress(selectedLocation);
        });
    }

    private void moveCameraToLocation(GoogleMap googleMap, Location location) {
        googleMap.moveCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(
                new LatLng(location.getLatitude(), location.getLongitude()), 18));
    }

    private com.google.android.gms.location.LocationRequest locationRequest;
    private com.google.android.gms.location.LocationCallback locationCallback;

    private void requestLocationUpdates(GoogleMap googleMap) {
        locationRequest = com.google.android.gms.location.LocationRequest.create();
        locationRequest.setPriority(com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        locationCallback = new com.google.android.gms.location.LocationCallback() {
            @Override
            public void onLocationResult(@NonNull com.google.android.gms.location.LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    moveCameraToLocation(googleMap, location);
                    getCurrentAddress(location);
                    fusedLocationClient.removeLocationUpdates(locationCallback);
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, android.os.Looper.getMainLooper());
        }
    }

    private void getCurrentAddress(Location location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);

                currentAddress = address.getAddressLine(0);
                Log.d("Address", "Current Address: " + currentAddress);

                Toast.makeText(this, "Address: " + currentAddress, Toast.LENGTH_SHORT).show();
            } else {
                currentAddress = "No address found";
                Toast.makeText(this, "No address found", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            currentAddress = "Error getting address: " + e.getMessage();
            Log.e("Geocoder", "Error: " + e.getMessage());
            Toast.makeText(this, "Error getting address", Toast.LENGTH_SHORT).show();
        }
    }

    public void onSetStartLocationButtonClick(View view) {
        if (currentAddress != null) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("address", currentAddress);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();

        } else {
            Toast.makeText(this, "Getting the address...", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 200) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // Permissions granted, re-trigger the map setup
                SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapsConstraintLayout);
                if (supportMapFragment != null) {
                    supportMapFragment.getMapAsync(googleMap -> {
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            googleMap.setMyLocationEnabled(true);
                            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                                if (location != null) {
                                    moveCameraToLocation(googleMap, location);
                                    getCurrentAddress(location);
                                } else {
                                    requestLocationUpdates(googleMap);
                                }
                            });
                        }
                    });
                }
            } else {
                Toast.makeText(this, "Location permissions are denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

