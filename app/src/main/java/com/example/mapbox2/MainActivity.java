package com.example.mapbox2;

import android.app.ActivityManager;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import com.example.mapbox2.responses.SchoolArrivedResponse;
import com.example.mapbox2.responses.StartTripResponse;
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

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, PermissionsListener {

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
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1002;
    private ActivityMainBinding mainBinding;

    private Location currentLocation;
    private TripViewModel tripViewModel;
    private ProgressDialog progressDialog;
    private String schoolName;
    private StringBuilder parent = new StringBuilder();

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
        progressDialog = new ProgressDialog(this);
        //parentId = new ArrayList<>();
        listOfLocations = new ArrayList<>();
        if (SharedPreferencesManager.getInstance(MainActivity.this).getWayPointsOfStartTrip() != null &&
                SharedPreferencesManager.getInstance(MainActivity.this).getSchool()!=null &&
        SharedPreferencesManager.getInstance(MainActivity.this).getTripIdOfStartTrip()!=-1) {
            //Log.d("opa", SharedPreferencesManager.getInstance(MainActivity.this).getWayPoints().get(0).getLit() + "asd");
            listOfLocations.addAll(SharedPreferencesManager.getInstance(MainActivity.this).getWayPointsOfStartTrip());
            destination = Point.fromLngLat(Double.parseDouble(SharedPreferencesManager.getInstance(MainActivity.this).getSchool().getLng()),Double.parseDouble(SharedPreferencesManager.getInstance(MainActivity.this).getSchool().getLit()));
            schoolName = SharedPreferencesManager.getInstance(MainActivity.this).getSchool().getName();
        }

        if(SharedPreferencesManager.getInstance(MainActivity.this).getParentsID()!=null){
            parent.append(SharedPreferencesManager.getInstance(MainActivity.this).getParentsID());
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
            startTrip();

            if(currentLocation!=null) {
                markeri.setPosition(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
                cameraPosition(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
            }

            /** if  the trip did not ended yet **/
            if (!listOfLocations.isEmpty() && destination!=null) {
                //Log.d("trip", listOfLocations.get(0).getLit() + "");
               // destination = Point.fromLngLat(30.913832, 29.955715);
                getRoute(currentLocation, listOfLocations, destination);
            }

            /** if the trip is going to school **/
            else if (SharedPreferencesManager.getInstance(MainActivity.this).getToSchool() == true && destination!=null) {
                Log.d("trip", "to school");
              //  destination = Point.fromLngLat(30.913832, 29.955715);
                getRouteToSchool(currentLocation, destination);
            }

            changeLocation();

            mainBinding.progress.setVisibility(View.INVISIBLE);
        }
    };

    /**
     * Dialog To Ask Driver If He Want To Start The Trip
     **/
    private void dialogOfStartNavigation(String message) {
        CustomAlertDialogue.Builder alert = new CustomAlertDialogue.Builder(MainActivity.this)
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
        if (SharedPreferencesManager.getInstance(MainActivity.this).getTripIdOfStartTrip() != -1) {
            tripViewModel.sendLocationToParent(currentLocation.getLatitude(),
                    currentLocation.getLongitude(),
                    SharedPreferencesManager.getInstance(MainActivity.this).getTripIdOfStartTrip(),
                    parent,
                    "Bearer " + SharedPreferencesManager.getInstance(MainActivity.this).getToken()).observe(MainActivity.this, new Observer<ChangeLocationResponse>() {
                @Override
                public void onChanged(ChangeLocationResponse changeLocationResponse) {
                    Log.d("trip", changeLocationResponse.getMessage());
                    Toast.makeText(MainActivity.this, changeLocationResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /*
        // Location Engine Listener
        @SuppressWarnings("MissingPermission")
        @Override
        public void onConnected() {
            locationEngine.requestLocationUpdates();
        }
    */

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        map = mapboxMap;
        enableLocation();
        /** Initialized The Driver Marker **/
        markeri = map.addMarker(new MarkerOptions()
                .setPosition(new LatLng(0,0))
                .setTitle(SharedPreferencesManager.getInstance(MainActivity.this).getUser().getName())
                .icon(IconFactory.getInstance(this).fromResource(R.drawable.bus_marker))
        );
        /** When Driver Click On Arrived **/
        arrived.setOnClickListener(view -> {
            arrived();
        });
    }

    private void arrived(){
        if (!listOfLocations.isEmpty() && isLocationServiceRunning() && listOfLocations!=null) {
            mainBinding.progress.setVisibility(View.VISIBLE);
            double distance = distance(Double.parseDouble(listOfLocations.get(0).getLit()), currentLocation.getLatitude(), Double.parseDouble(listOfLocations.get(0).getLng()), currentLocation.getLongitude());
           /* double smallestDistance = distance(listOfLocations.get(0).getLatitude(), currentLocation.getLatitude(), listOfLocations.get(0).getLongitude(), currentLocation.getLongitude());
            int indexOfSmallest = 0;
            for (int i = 1; i < listOfLocations.size(); i++) {
                Log.d("hossam", "distance" + i + " = " + distance(listOfLocations.get(i).getLatitude(), latitude, listOfLocations.get(i).getLatitude(), longitude) * 1000);
                if (smallestDistance > distance(listOfLocations.get(i).getLatitude(), currentLocation.getLatitude(), listOfLocations.get(i).getLatitude(), currentLocation.getLongitude())) {
                    smallestDistance = distance(listOfLocations.get(i).getLatitude(), currentLocation.getLatitude(), listOfLocations.get(i).getLatitude(), currentLocation.getLongitude());
                    indexOfSmallest = i;
                }
            }*/
            /** If The Driver Distance Between Him And Next Point Bigger than 600 Meters **/
            if (150 < 1000 * distance) {
                showDialog("You are So Far From Parent House","You're Not Closer Yet , The Distance is  " +Math.floor( 1000 * distance)+" Meters Between You And The Next Point");
                mainBinding.progress.setVisibility(View.INVISIBLE);
                return;
            }
            /** If It Is The Last Point In the List Of Parent's Locations That's Mean You're Going To school **/
            if (listOfLocations.size() == 1) {
                Log.d("yalla","sd"+listOfLocations.get(0).getId());
                parent.append(listOfLocations.get(0).getId()+",");
                SharedPreferencesManager.getInstance(MainActivity.this).arrivedAt(parent);
                listOfLocations.remove(0);
                SharedPreferencesManager.getInstance(MainActivity.this).saveWayPointsOfStartTrip(listOfLocations);
                SharedPreferencesManager.getInstance(MainActivity.this).toSchool(true);
            }
            /** If It Isn't The Last Location In The List **/
            else {
                parent.append(listOfLocations.get(0).getId()+",");
                SharedPreferencesManager.getInstance(MainActivity.this).arrivedAt(parent);
                listOfLocations.remove(0);
                SharedPreferencesManager.getInstance(MainActivity.this).saveWayPointsOfStartTrip(listOfLocations);
            }
        } else {
            if(SharedPreferencesManager.getInstance(MainActivity.this).isTripStarted()==false){
                showDialog("You've Never Start The Trip","You Cannot Use Arrived Button Since You Never Start Any Trip");
            }
            else if (!isLocationServiceRunning()) {
                showDialog("Cannot Get Your Location","Please Check Your GPS And Try Again.");
            } else if (SharedPreferencesManager.getInstance(MainActivity.this).getToSchool() == true) {
                double distanceToSchool = distance(destination.latitude(), currentLocation.getLatitude(), destination.longitude(), currentLocation.getLongitude());
                if (150 < 1000 * distanceToSchool) {
                    showDialog("You are So Far From School","You're Not Closer Yet , The Distance is  " + Math.floor(1000 * distanceToSchool)+" Meters Between You And The School Location.");
                } else {
                    arrivedSchool();
                    SharedPreferencesManager.getInstance(MainActivity.this).toSchool(false);
                    stopLocationService();
                    SharedPreferencesManager.getInstance(MainActivity.this).startTrip(false);
                    navigationMapRoute.removeRoute();
                }
            }
        }
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


/*
        // Location Engine Listener
        //  @Override
        public void onLocationChanged (Location location){
        if (location != null) {
            myLocation = location;
            //cameraPosition(location);
            destination = Point.fromLngLat(30.913832, 29.955715);
            getRoute(myLocation, listOfLocations, destination);
            progressBar.setVisibility(View.INVISIBLE);
            if (currentRoute != null) {
                Log.d("hossam", "kilo " + currentRoute.distance().toString());
            }
            Log.d("hossam", myLocation.getLatitude() + "");
        }
    }

*/

    // Location Engine Listener
    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        // the user denied access to his location
        Toast.makeText(this, "we need access", Toast.LENGTH_SHORT).show();
    }


    private void enableLocation() {
        // the permission is granted.
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            Log.d("permissions","permissions granted");
        } else {
            // the permissions is not granted.
            permissionsManager = new PermissionsManager(this);
            //here we request for location from user
            permissionsManager.requestLocationPermissions(this);
        }
    }

 /*   private void tracking() {
        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult != null && locationResult.getLastLocation() != null) {

                    latitude = locationResult.getLastLocation().getLatitude();
                    longitude = locationResult.getLastLocation().getLongitude();
                    try {
                        markeri.setPosition(new LatLng(latitude, longitude));
                        cameraPosition(new LatLng(latitude, longitude));
                        Log.d("hossam", latitude + "," + longitude);
                        destination = Point.fromLngLat(30.913832, 29.955715);
                        getRoute(locationResult.getLastLocation(), listOfLocations, destination);
                        progressBar.setVisibility(View.INVISIBLE);
                    } catch (Exception e) {
                        Log.d("hossamm", e.getMessage());
                    }
                }
            }
        };
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(4000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.getFusedLocationProviderClient(this)
                .requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }*/


/*
    @SuppressWarnings("MissingPermission")
    private void initializeLocationEngine() {
        // Location Engine is class that simplifies the process of getting user location
        // obtaining the location of the user
        locationEngine = new LocationEngineProvider(this).obtainBestLocationEngineAvailable();
        // setting the accuracy to high
        locationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
        locationEngine.setInterval(1000);
        locationEngine.setSmallestDisplacement(0);
        locationEngine.setFastestInterval(1000);
        //Activate the location engine which will connect whichever location provider you are using.
        // You'll need to call this before requesting user location updates using requestLocationUpdates().
        locationEngine.activate();
        locationEngine.addLocationEngineListener(this);
        Location lastLocation = locationEngine.getLastLocation();
        if (lastLocation != null) {
            myLocation = lastLocation;
            // cameraPosition(lastLocation);

        } else {
            //handle location connections and update events.
            locationEngine.addLocationEngineListener(this);
        }


    }

*/

    /*
    @SuppressWarnings("MissingPermission")
    private void initializeLocationLayer() {
        //provides location awareness to your mobile application.
        locationLayerPlugin = new LocationLayerPlugin(mapView, map, locationEngine);
        //This method will show or hide the location icon and enable or disable the camera tracking the location.
        locationLayerPlugin.setLocationLayerEnabled(true);
        locationLayerPlugin.setCameraMode(CameraMode.TRACKING);
        locationLayerPlugin.setRenderMode(RenderMode.NORMAL);
    }
*/
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
        Toast.makeText(this, "on-pause", Toast.LENGTH_SHORT).show();
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

    /** Function That Get The Route To The Parent's houses **/
    private void getRoute(Location user, List<FathersItem> list, Point dest) {
        NavigationRoute.Builder builder = NavigationRoute.builder()// Initializing the route builder
                .accessToken(Mapbox.getAccessToken())
                .origin(Point.fromLngLat(user.getLongitude(), user.getLatitude())) // Since we are creating a route with the same start and end points
                .destination(dest); // Both origin and destination is user's current location
        /** Initialized The Marker Of the School **/
        map.addMarker(new MarkerOptions()
                .setTitle("School")
                .position(new LatLng(dest.latitude(),dest.longitude()))
                .icon(IconFactory.getInstance(this).fromResource(R.drawable.school_marker))
        );
        if (list != null) {
            for (int i = 0; i < list.size(); i++) { // Adding all the waypoints as pitstops to the route
                builder.addWaypoint(Point.fromLngLat(Double.parseDouble(list.get(i).getLng()), Double.parseDouble(list.get(i).getLit())));
                map.addMarker(new MarkerOptions()
                        .setTitle(list.get(i).getName())
                        .position(new LatLng(Double.parseDouble(list.get(i).getLit()),Double.parseDouble(list.get(i).getLng())))
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

    private void getRouteToSchool(Location user, Point dest) {
        NavigationRoute.Builder builder = NavigationRoute.builder()// Initializing the route builder
                .accessToken(Mapbox.getAccessToken())
                .origin(Point.fromLngLat(user.getLongitude(), user.getLatitude())) // Since we are creating a route with the same start and end points
                .destination(dest) // Both origin and destination is user's current location
                ;
        /** Initialized The Marker Of the School **/
        map.addMarker(new MarkerOptions()
                .setTitle(schoolName.isEmpty() ? "school" : schoolName)
                .position(new LatLng(dest.latitude(),dest.longitude()))
                .icon(IconFactory.getInstance(this).fromResource(R.drawable.school_marker))
        );
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
        CustomAlertDialogue.Builder alert = new CustomAlertDialogue.Builder(MainActivity.this)
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

    private void startTrip(){
        if (listOfLocations.isEmpty() && SharedPreferencesManager.getInstance(MainActivity.this).getToSchool() == false) {
            mainBinding.progressLinear.setVisibility(View.VISIBLE);
            tripViewModel.startTrip(currentLocation.getLatitude(),
                    currentLocation.getLongitude(),
                    "Bearer " + SharedPreferencesManager.getInstance(MainActivity.this).getToken()).observe(MainActivity.this, new Observer<StartTripResponse>() {
                @Override
                public void onChanged(StartTripResponse startTripResponse) {
                    mainBinding.progressLinear.setVisibility(View.INVISIBLE);
                    if (startTripResponse != null && startTripResponse.isSuccess()) {
                        for (int i = 0; i < startTripResponse.getData().getFathers().size(); i++) {
                            //listOfLocations.add(new LocationPoints(Double.parseDouble(startTripResponse.getData().get(i).getLit()), Double.parseDouble(startTripResponse.getData().get(i).getLng()),startTripResponse.getData().get(i).getName(),startTripResponse.getData().get(i).getId()));
                            listOfLocations.add(startTripResponse.getData().getFathers().get(i));
                        }
                        schoolName = startTripResponse.getData().getSchool().getName();
                        destination = Point.fromLngLat(Double.parseDouble(startTripResponse.getData().getSchool().getLng()),Double.parseDouble(startTripResponse.getData().getSchool().getLit()));
                        SharedPreferencesManager.getInstance(MainActivity.this).saveWayPointsOfStartTrip(listOfLocations);
                        SharedPreferencesManager.getInstance(MainActivity.this).saveSchool(startTripResponse.getData().getSchool());
                        SharedPreferencesManager.getInstance(MainActivity.this).saveTripIdOfStartTrip(startTripResponse.getData().getTrip().getId());
                    }else if (startTripResponse!=null && !startTripResponse.isSuccess()){
                        showDialog("Sorry",startTripResponse.getMessage());
                        stopLocationService();
                        return;
                    }
                }
            });
        }
    }
    private void arrivedSchool(){
        mainBinding.progress.setVisibility(View.VISIBLE);
        tripViewModel.SchoolArrived("Bearer " +SharedPreferencesManager.getInstance(MainActivity.this).getToken()).observe(MainActivity.this, new Observer<SchoolArrivedResponse>() {
            @Override
            public void onChanged(SchoolArrivedResponse schoolArrivedResponse) {
                mainBinding.progress.setVisibility(View.INVISIBLE);
                if(schoolArrivedResponse!=null){
                    if(schoolArrivedResponse.isSuccess() && schoolArrivedResponse.getMessage()!=null){
                        showDialog("The Process Completed Successfully",schoolArrivedResponse.getMessage());
                        SharedPreferencesManager.getInstance(MainActivity.this).clearTripInfo();
                        Log.d("arrived",schoolArrivedResponse.getMessage());
                    }else if (!schoolArrivedResponse.isSuccess() && schoolArrivedResponse.getMessage()!=null){
                        Log.d("arrived",schoolArrivedResponse.getMessage());
                    }
                }
            }
        });
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
            SharedPreferencesManager.getInstance(MainActivity.this).startTrip(true);
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
