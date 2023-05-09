package com.example.mapbox2;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.mapbox2.databinding.ActivityMainBinding;
import com.example.mapbox2.models.FathersItem;
import com.example.mapbox2.responses.ChangeLocationResponse;
import com.example.mapbox2.responses.EndTripResponse;
import com.example.mapbox2.responses.NavigateBackResponse;
import com.example.mapbox2.services.LocationService;
import com.example.mapbox2.storage.SharedPreferencesManager;
import com.example.mapbox2.viewmodels.TripViewModel;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import stream.customalert.CustomAlertDialogue;

public class NavigateBackActivity extends AppCompatActivity implements OnMapReadyCallback, PermissionsListener {

    private MapView mapView;
    private MapboxMap map;
    // permissions helper class
    private PermissionsManager permissionsManager;
    private LocationEngine locationEngine;
    private LocationLayerPlugin locationLayerPlugin;
    private Location myLocation;
    private List<FathersItem> listOfLocations;
    private Marker markeri;
    private NavigationMapRoute navigationMapRoute;
    private Point origin;
    private Point destination;
    private double latitude;
    private double longitude;
    // to check gps if open
    private LocationManager manager;
    DirectionsRoute currentRoute;
    private Button arrived;
    private ProgressBar progressBar;
    private int trip_id;
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1002;
    private ActivityMainBinding mainBinding;

    private Location currentLocation;
    private TripViewModel tripViewModel;
    private StringBuilder parentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(this, getString(R.string.access_token));
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mapView = (MapView) findViewById(R.id.mapView);
        tripViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())).get(TripViewModel.class);
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("GPSLocationUpdates"));
        arrived = findViewById(R.id.arrived);
        //  mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        // we used manager to check if driver opened gps before start the trip
        manager = (LocationManager) getSystemService(LOCATION_SERVICE);

        listOfLocations = new ArrayList<>();
        if (SharedPreferencesManager.getInstance(NavigateBackActivity.this).getWayPointsOfNavigateBack() != null &&
                SharedPreferencesManager.getInstance(NavigateBackActivity.this).getTripIdOfNavigateBack() != -1) {
            //Log.d("opa", SharedPreferencesManager.getInstance(MainActivity.this).getWayPoints().get(0).getLit() + "asd");
            listOfLocations.addAll(SharedPreferencesManager.getInstance(NavigateBackActivity.this).getWayPointsOfNavigateBack());
        }

        /**Start Trip**/
        mainBinding.startNavigating.setOnClickListener(view -> {
            /** Check First If The Trip Is Started Or Not **/
            dialogOfStartNavigation("Do you want to start navigate?");
        });
    }


    /**
     * BroadcastReceiver To Get GPS Location From Our Service
     **/
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("Status");
            Bundle b = intent.getBundleExtra("Location");
            currentLocation = (Location) b.getParcelable("Location");
            navigateBack();

            if (currentLocation != null) {
                markeri.setPosition(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
                cameraPosition(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
            }

            /** if  the trip did not ended yet **/
            if (!listOfLocations.isEmpty()) {
                //Log.d("trip", listOfLocations.get(0).getLit() + "");
                // destination = Point.fromLngLat(30.913832, 29.955715);
                getRoute(currentLocation, listOfLocations);
            }

            changeLocation();

            mainBinding.progress.setVisibility(View.INVISIBLE);
        }
    };

    /**
     * Dialog To Ask Driver If He Want To Start The Trip
     **/
    private void dialogOfStartNavigation(String message) {
        CustomAlertDialogue.Builder alert = new CustomAlertDialogue.Builder(NavigateBackActivity.this)
                .setStyle(CustomAlertDialogue.Style.DIALOGUE)
                .setCancelable(false)
                .setTitle("Start Navigate")
                .setMessage(message)
                .setPositiveText("Confirm")
                .setPositiveColor(R.color.positive)
                .setPositiveTypeface(Typeface.DEFAULT_BOLD)
                .setOnPositiveClicked(new CustomAlertDialogue.OnPositiveClicked() {
                    @Override
                    public void OnClick(View view, Dialog dialog) {
                        dialog.dismiss();
                        /** Start Navigation **/
                        startLocationService();
                    }
                })
                .setNegativeText("Cancel")
                .setNegativeColor(R.color.negative)
                .setOnNegativeClicked(new CustomAlertDialogue.OnNegativeClicked() {
                    @Override
                    public void OnClick(View view, Dialog dialog) {
                        /** Dismissing The Dialog **/
                        dialog.dismiss();
                    }
                })
                .setDecorView(getWindow().getDecorView())
                .build();
        alert.show();
    }

    private void changeLocation() {
        if (SharedPreferencesManager.getInstance(NavigateBackActivity.this).getTripIdOfNavigateBack() != -1) {
            tripViewModel.sendLocationToParent(currentLocation.getLatitude(),
                    currentLocation.getLongitude(),
                    SharedPreferencesManager.getInstance(NavigateBackActivity.this).getTripIdOfNavigateBack(),
                    parentId,
                    "Bearer " + SharedPreferencesManager.getInstance(NavigateBackActivity.this).getToken()).observe(NavigateBackActivity.this, new Observer<ChangeLocationResponse>() {
                @Override
                public void onChanged(ChangeLocationResponse changeLocationResponse) {
                    Log.d("trip", changeLocationResponse.getMessage());
                    Toast.makeText(NavigateBackActivity.this, changeLocationResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        map = mapboxMap;
        enableLocation();
        /** Initialized The Driver Marker **/
        markeri = map.addMarker(new MarkerOptions()
                .setPosition(new LatLng(0, 0))
                .setTitle(SharedPreferencesManager.getInstance(NavigateBackActivity.this).getUser().getName())
                .icon(IconFactory.getInstance(this).fromResource(R.drawable.bus_marker))
        );
        /** When Driver Click On Arrived **/
        arrived.setOnClickListener(view -> {
            arrived();
        });
    }

    private void arrived() {
        if (!listOfLocations.isEmpty() && isLocationServiceRunning()) {
            mainBinding.progress.setVisibility(View.VISIBLE);
            double distance = distance(Double.parseDouble(listOfLocations.get(0).getLit()), currentLocation.getLatitude(), Double.parseDouble(listOfLocations.get(0).getLng()), currentLocation.getLongitude());

            /** If The Driver Distance Between Him And Next Point Bigger than 600 Meters **/
            if (150 < 1000 * distance) {
                showDialog("You are So Far From Parent House", "You're Not Closer Yet , The Distance is  " + Math.floor(1000 * distance) + " Meters Between You And The Next Point");
                 mainBinding.progress.setVisibility(View.INVISIBLE);
                 return;
            } else if (listOfLocations.size() == 1) {
                mainBinding.progress.setVisibility(View.INVISIBLE);
                listOfLocations.remove(0);
                stopLocationService();
                navigationMapRoute.removeRoute();
                SharedPreferencesManager.getInstance(NavigateBackActivity.this).clearInfoOfNavigateBack();
                // end op.
                tripEnded();
            } else {
                listOfLocations.remove(0);
                SharedPreferencesManager.getInstance(NavigateBackActivity.this).saveWayPointsOfNavigateBack(listOfLocations);
            }
        } else if (!isLocationServiceRunning()) {
            showDialog("Trip Is Not Active", "You Cannot Arrive Since Trip Is Not Active");
        }
    }

    private void tripEnded(){
        mainBinding.progressLinear.setVisibility(View.VISIBLE);
        tripViewModel.EndTrip( "Bearer " +SharedPreferencesManager.getInstance(NavigateBackActivity.this).getToken()).observe(this, new Observer<EndTripResponse>() {
            @Override
            public void onChanged(EndTripResponse endTripResponse) {
                mainBinding.progressLinear.setVisibility(View.INVISIBLE);
                if(endTripResponse!=null && endTripResponse.isSuccess()){
                    showDialog("Trip Ended Successfully",endTripResponse.getMessage());
                }else if(endTripResponse!=null && !endTripResponse.isSuccess()){
                    showDialog("Sorry",endTripResponse.getData());
                }
            }
        });
    }


    public static double distance(double lat1,
                                  double lat2, double lon1,
                                  double lon2) {

        // The math module contains a function
        // named toRadians which converts from
        // degrees to radians.
        lon1 = Math.toRadians(lon1);
        lon2 = Math.toRadians(lon2);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        // Haversine formula
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2), 2);

        double c = 2 * Math.asin(Math.sqrt(a));

        // Radius of earth in kilometers. Use 3956
        // for miles
        double r = 6371;

        // calculate the result
        return (c * r);
    }


    // Location Engine Listener
    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        // the user denied access to his location
        Toast.makeText(this, "we need access", Toast.LENGTH_SHORT).show();
    }


    private void enableLocation() {
        // the permission is granted.
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            // initializeLocationEngine();
            // initializeLocationLayer();
            // tracking();
        } else {
            // the permissions is not granted.
            permissionsManager = new PermissionsManager(this);
            //here we request for location from user
            permissionsManager.requestLocationPermissions(this);
        }
    }

    private void cameraPosition(LatLng location) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 17.0), 10000);
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocation();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "READ_PHONE_STATE Denied", Toast.LENGTH_SHORT)
                            .show();
                }
        }
    }

    @SuppressWarnings("MissingPermission")
    @Override
    protected void onStart() {
        super.onStart();
        if (locationEngine != null) {
            locationEngine.requestLocationUpdates();
        }
        if (locationLayerPlugin != null) {
            locationLayerPlugin.onStart();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            int res = checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE);
            if (res != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.READ_PHONE_STATE}, 123);
            }

        }
        enableLocation();
        mapView.onStart();
    }


    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();

    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationEngine != null) {
            locationEngine.deactivate();
        }
        mapView.onDestroy();

    }

    /**
     * Function That Get The Route To The Parent's houses
     **/
    private void getRoute(Location user, List<FathersItem> list) {
        NavigationRoute.Builder builder = NavigationRoute.builder()// Initializing the route builder
                .accessToken(Mapbox.getAccessToken())
                .origin(Point.fromLngLat(user.getLongitude(), user.getLatitude())); // Since we are creating a route with the same start and end points; // Both origin and destination is user's current location


        if (list != null) {
            for (int i = 0; i < list.size(); i++) { // Adding all the waypoints as pitstops to the route
                builder.addWaypoint(Point.fromLngLat(Double.parseDouble(list.get(i).getLng()), Double.parseDouble(list.get(i).getLit())));
                map.addMarker(new MarkerOptions()
                        .setTitle(list.get(i).getName())
                        .position(new LatLng(Double.parseDouble(list.get(i).getLit()), Double.parseDouble(list.get(i).getLng())))
                        .icon(IconFactory.getInstance(this).fromResource(R.drawable.home_marker))
                );
                //map.addMarker(new MarkerOptions().setTitle(list.get(i).getName()).position(new LatLng(Double.parseDouble(list.get(i).getLit()),Double.parseDouble(list.get(i).getLng()))));
            }
        }
        try {
            builder.build() // Building the route
                    .getRoute(new Callback<DirectionsResponse>() {
                        @Override
                        public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                            if (response.body() == null) {
                                Log.e("hossam", "NO VALID RESPONSE");
                                return;
                            } else {
                                if (response.body().routes().size() == 0) {
                                    Log.e("hossam", "NO ROUTES FOUND");
                                    return;
                                }
                                currentRoute = response.body().routes().get(0);
                                try {
                                    currentRoute = response.body().routes().get(0);
                                } catch (NullPointerException e) {
                                    Log.e("hossam", "BAD ROUTE, NULL POINTER EXCEPTION: " + e.getMessage());
                                }
                                if (navigationMapRoute != null) {
                                    navigationMapRoute.removeRoute();
                                    Log.d("hossam", "Old route removed");
                                } else {
                                    try {
                                        navigationMapRoute = new NavigationMapRoute(mapView, map);
                                        Log.d("hossam", "New route created");

                                    } catch (Exception e) {
                                        Log.d("hossam", "null object reference " + e.getMessage().toString());
                                    }

                                }
                                try {
                                    navigationMapRoute.addRoute(currentRoute);
                                    Log.d("hossam", "New route added");
                                } catch (Exception e) {
                                    Log.d("hossam", "FAILED TO ADD NEW ROUTE" + e.getMessage());

                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                            Log.e("hossam", t.getMessage() + "--error--");
                        }
                    });
        } catch (Exception e) {
            Log.d("hossam", e.getMessage());
        }
    }


    private void showDialog(String title, String message) {
        CustomAlertDialogue.Builder alert = new CustomAlertDialogue.Builder(NavigateBackActivity.this)
                .setStyle(CustomAlertDialogue.Style.DIALOGUE)
                .setTitle(title)
                .setMessage(message)
                .setNegativeText("OK")
                .setNegativeColor(R.color.purple_200)
                .setNegativeTypeface(Typeface.DEFAULT_BOLD)
                .setOnNegativeClicked(new CustomAlertDialogue.OnNegativeClicked() {
                    @Override
                    public void OnClick(View view, Dialog dialog) {
                        dialog.dismiss();
                    }
                })
                .setDecorView(getWindow().getDecorView())
                .build();
        alert.show();
    }

    private void navigateBack() {
        if (listOfLocations.isEmpty()) {
            mainBinding.progressLinear.setVisibility(View.VISIBLE);
            tripViewModel.navigateBack(currentLocation.getLatitude(), currentLocation.getLongitude(), "Bearer " + SharedPreferencesManager.getInstance(this).getToken()).observe(this, new Observer<NavigateBackResponse>() {
                @Override
                public void onChanged(NavigateBackResponse navigateBackResponse) {
                    mainBinding.progressLinear.setVisibility(View.INVISIBLE);
                    if (navigateBackResponse != null && navigateBackResponse.isSuccess()) {
                        Log.d("navigateBack",navigateBackResponse.getMessage());
                        Toast.makeText(NavigateBackActivity.this, navigateBackResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        trip_id = navigateBackResponse.getData().getTrip().getId();
                        for (int i = 0; i < navigateBackResponse.getData().getFathers().size(); i++) {
                            listOfLocations.add(navigateBackResponse.getData().getFathers().get(i));
                        }
                        SharedPreferencesManager.getInstance(NavigateBackActivity.this).saveWayPointsOfNavigateBack(listOfLocations);
                        SharedPreferencesManager.getInstance(NavigateBackActivity.this).saveTripIdOfNavigateBack(navigateBackResponse.getData().getTrip().getId());

                    } else if (navigateBackResponse != null && !navigateBackResponse.isSuccess()) {
                        stopLocationService();
                        Log.d("navigateBack",navigateBackResponse.getMessage());
                        Toast.makeText(NavigateBackActivity.this, navigateBackResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    private boolean isLocationServiceRunning() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if (LocationService.class.getName().equals(service.service.getClassName())) {
                    if (service.foreground) {
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }

    /**
     * Function That Start The Service..
     **/
    private void startLocationService() {
        /** Check First If User OPended It's GPS Before Start Service**/
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Intent intent = new Intent(getApplicationContext(), LocationService.class);
            intent.setAction("102");
            startService(intent);
            SharedPreferencesManager.getInstance(NavigateBackActivity.this).startTrip(true);
            Toast.makeText(this, "Location Service Started", Toast.LENGTH_SHORT).show();
        } else {
            /** If User Not Opened The Gps , Show Toast To Tell Him To Open It **/
            Toast.makeText(this, "Please Open GPS To Start The Trip", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopLocationService() {
        if (isLocationServiceRunning()) {
            Intent intent = new Intent(getApplicationContext(), LocationService.class);
            intent.setAction(String.valueOf(103));
            startService(intent);
            Toast.makeText(this, "Location Service stopped", Toast.LENGTH_SHORT).show();
        }
    }


}
