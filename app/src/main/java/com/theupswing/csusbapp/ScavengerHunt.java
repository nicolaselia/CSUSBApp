package com.theupswing.csusbapp;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class ScavengerHunt extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Boolean inFullScreenMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scavengerhunt);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_default);
        mapFragment.getMapAsync(this);

        setUpListeners();
        setUpFullScreen();
        setUpProgressBar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpProgressBar();
    }

    private void setUpListeners() {
        // Goes to List of Places activity
        TextView listOfPlacesText = findViewById(R.id.list_of_places_text);
        listOfPlacesText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ScavengerHunt.this, ListOfPlaces.class);
                startActivity(intent);
            }
        });

        // Goes to List of Places activity
        ImageView topArrow = findViewById(R.id.top_arrow);
        topArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ScavengerHunt.this, ListOfPlaces.class);
                startActivity(intent);
            }
        });

        // Goes to QR Scanner
        TextView checkOffText = findViewById(R.id.checkoff_place_text);
        checkOffText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(ScavengerHunt.this,
                        Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(ScavengerHunt.this, ScanQRCode.class);
                    startActivity(intent);
                } else {
                    requestCameraPermission();
                }
            }
        });

        // Goes to QR Scanner
        ImageView bottomArrow = findViewById(R.id.bottom_arrow);
        bottomArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ScavengerHunt.this, ScanQRCode.class);
                startActivity(intent);
            }
        });

        // Moves camera to Student Union when pressed (in case the user is off campus)
        ImageView pawImage = findViewById(R.id.paw);
        pawImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(34.181309518547195, -117.32371486723423), 18));
            }
        });

    }


    /**
     * Manipulates the map once available.
     * Triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        /*To find map coordinates for database
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.i("MAP", latLng.latitude + ", " + latLng.longitude);
            }
        });
        */


        styleMap();
        populateMap();
        setUpDirectionIntegration();

        Intent intent = getIntent();
        String location = intent.getStringExtra("Location");
        if (location != null) {
            showSelectedLocation(location);
        } else {
            //showCurrentLocation();
            createLocationRequest();
        }


    }

    /**
     * Removes all markers off of the map
     */
    private void styleMap() {
        String JSON = "[{\"featureType\":\"poi\", \"stylers\":[{\"visibility\":\"off\"}]}]";
        //mMap.setMapStyle(new MapStyleOptions(JSON));
    }

    /**
     * Adds images of all the locations in the database onto the map
     */
    private void populateMap() {
        DatabaseHelper database = new DatabaseHelper(this);
        Cursor cursor = database.getAllData();

        //database.updateLatLng("Gym", 34.181243783767364, -117.31866795569658);

        while (cursor.moveToNext()) {
            double lat = cursor.getDouble(cursor.getColumnIndex(database.COL_LAT));
            double lng = cursor.getDouble(cursor.getColumnIndex(database.COL_LONG));
            int image = database.getImage(cursor.getString(cursor.getColumnIndex(database.COL_LOC)));

            //Log.i("ROW", R.drawable.library + "");
            LatLng coords = new LatLng(lat, lng);

            GroundOverlayOptions overlayOptions = new GroundOverlayOptions()
                    .image(BitmapDescriptorFactory.fromResource(image))
                    .position(coords, 30f, 30f);

            mMap.addGroundOverlay(overlayOptions);
        }
    }

    /**
     * When a user clicks on a location on the map, show a dialog that lets them proceed to open the location in the GMaps app (so they can get directions)
     */
    private void setUpDirectionIntegration() {
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(final LatLng latLng) {
                new AlertDialog.Builder(ScavengerHunt.this)
                        .setTitle("Open Google Maps?")
                        .setMessage("Get directions to your selected location.")
                        .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String uri = "http://maps.google.com/maps?daddr=" + latLng.latitude + "," + latLng.longitude + " (" + "Where the party is at" + ")";
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                intent.setPackage("com.google.android.apps.maps");
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Do nothing
                            }
                        })
                        .show();

                Log.i("MAP", latLng.latitude + ", " + latLng.longitude);
            }
        });
    }

    /**
     * Enables fullscreen functionality for the map
     */
    private void setUpFullScreen() {
        final ImageView expandButton = findViewById(R.id.expand);
        expandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RelativeLayout mapParent = findViewById(R.id.map_parent);
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) mapParent.getLayoutParams();
                ProgressBar progressBar = findViewById(R.id.progress);

                ConstraintLayout constraintLayout = findViewById(R.id.constraint_layout);
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(constraintLayout);


                if (!inFullScreenMode) {
                    constraintSet.connect(R.id.map_parent, ConstraintSet.TOP, R.id.constraint_layout, ConstraintSet.TOP, 0);
                    constraintSet.connect(R.id.map_parent, ConstraintSet.BOTTOM, R.id.constraint_layout, ConstraintSet.BOTTOM, 0);
                    constraintSet.applyTo(constraintLayout);

                    layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    layoutParams.setMarginStart(0);
                    layoutParams.setMarginEnd(0);
                    expandButton.setImageResource(R.drawable.ic_exit);

                    inFullScreenMode = true;
                } else {
                    constraintSet.connect(R.id.map_parent, ConstraintSet.TOP, R.id.textView, ConstraintSet.BOTTOM, 0);
                    constraintSet.connect(R.id.map_parent, ConstraintSet.BOTTOM, R.id.progress, ConstraintSet.TOP, 0);
                    constraintSet.applyTo(constraintLayout);

                    layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 250, getResources().getDisplayMetrics());

                    int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
                    layoutParams.setMarginStart(margin);
                    layoutParams.setMarginEnd(margin);
                    expandButton.setImageResource(R.drawable.ic_expand);
                    inFullScreenMode = false;
                }

                mapParent.setLayoutParams(layoutParams);
            }
        });
    }

    /**
     * Show the current location by default
     */
    private void showCurrentLocation() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(ScavengerHunt.this);
            Task locationResult = mFusedLocationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener(this, new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Location location = (Location) task.getResult();
                        LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 18));

                        mMap.addMarker(new MarkerOptions().position(current).title("You are here"));
                    }
                }
            });
        } else {  // Show default location
            requestLocationPermission();

            LatLng current = new LatLng(34.180972800611016, -117.32337489724159);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 18));
        }
    }

    private void createLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationCallback mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 18));
                        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            mMap.setMyLocationEnabled(true);
                        }

                    }
                }
            }
        };

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, mLocationCallback, null);
        }

    }

    /**
     * If the current activity was called by the ListOfPlaces activity, this function will show the selected location
     */
    private void showSelectedLocation(String location) {
        DatabaseHelper database = new DatabaseHelper(this);
        double lat = database.getLatitude(location);
        double lng = database.getLongitude(location);

        Log.i("Called", lat + " " + lng);
        LatLng current = new LatLng(lat, lng);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 18));
    }

    /**
     * Set up the circular progress bar based on how many locations have been marked as visited
     */
    private void setUpProgressBar() {
        DatabaseHelper database = new DatabaseHelper(this);
        Cursor cursor = database.getAllData();

        int totalLocationCount = 0;
        double visitedCount = 0;

        while (cursor.moveToNext()) {
            totalLocationCount++;
            int visited = cursor.getInt(cursor.getColumnIndex(database.COL_VISITED));
            if (visited == 1) visitedCount++;
        }

        int progress = (int) (visitedCount / totalLocationCount * 100);
        ProgressBar progressBar = findViewById(R.id.progress);
        TextView progressText = findViewById(R.id.progress_text);
        progressBar.setProgress(progress);
        progressText.setText(" " + progress + "%");
    }

    /**
     * Ask for camera permission before proceeding
     */
    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("The camera is needed to scan QR Codes")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(ScavengerHunt.this,
                                    new String[]{Manifest.permission.CAMERA}, 1);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Toast.makeText(ScavengerHunt.this, "Camera is needed to scan QR codes", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .create()
                    .show();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, 1);
        }
    }

    /**
     * Ask for location permission
     */
    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("Location Permission is needed to show your current location on the map")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(ScavengerHunt.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Toast.makeText(ScavengerHunt.this, "Location Permission is needed to update the map", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .create()
                    .show();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
        }
    }

    /**
     * Executes after user responds to permission request
     *
     * @param requestCode:  "1" for camera, "2" for location
     * @param permissions:  unused
     * @param grantResults: array containing the result of the user's action
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(ScavengerHunt.this, ScanQRCode.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Camera is needed to scan QR codes", Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == 2) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showCurrentLocation();
            } else {
                Toast.makeText(this, "Location Permission is needed to update the map", Toast.LENGTH_SHORT).show();
            }
        }
    }
}