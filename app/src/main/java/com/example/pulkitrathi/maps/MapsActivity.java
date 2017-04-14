package com.example.pulkitrathi.maps;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerClickListener,
        LocationListener {

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    long n=285;
    Firebase root,child;
    String image_path,latitude,longitude,place;
    public static DatabaseReference dustbin_db , complaint_db, image_db, latitude_db , longitude_db, place_db,type_db;
    Marker marker;
    DatabaseReference databaseReference, db;
    StorageReference storage;
    int i =0;
    ImageButton full,smelly;
    LinearLayout ll,ll2;
    String lat_com = "" , lon_com = "";
    TextView t1,t2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        full = (ImageButton)findViewById(R.id.full_);
        smelly = (ImageButton)findViewById(R.id.smelly_);
        ll = (LinearLayout)findViewById(R.id.ll);
        ll2 = (LinearLayout)findViewById(R.id.ll2);
        t1 = (TextView) findViewById(R.id.t1);
        t2 = (TextView) findViewById(R.id.t2);


        full.bringToFront();
        smelly.bringToFront();
        ll.bringToFront();
        ll2.bringToFront();
        t1.bringToFront();
        t2.bringToFront();


        ll.setVisibility(View.GONE);
        ll2.setVisibility(View.GONE);

        full.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!lat_com.equals("") && !lon_com.equals("") ) {
                    // PUT YOUR DATABASE ADDRESS BELOW
                    databaseReference = FirebaseDatabase.getInstance().getReference().child("complaints").child("full");

                    dustbin_db = databaseReference.push();
                    latitude_db = dustbin_db.child("latitude");
                    longitude_db = dustbin_db.child("longitude");
                    type_db = dustbin_db.child("type");

                    latitude_db.setValue(lat_com);
                    longitude_db.setValue(lon_com);
                    type_db.setValue("FULL");

                    Toast.makeText(getApplicationContext(),"complaint submitted", Toast.LENGTH_SHORT).show();

                    lat_com = "";
                    lon_com = "";
                    ll.setVisibility(View.GONE);
                    ll2.setVisibility(View.GONE);
                }
                else
                    Toast.makeText(getApplicationContext(),"please select a dustbin to complain", Toast.LENGTH_SHORT).show();

            }
        });

        smelly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!lat_com.equals("") && !lon_com.equals("") ) {
                    databaseReference = FirebaseDatabase.getInstance().getReference.child("complaints").child("smelly");

                    dustbin_db = databaseReference.push();
                    latitude_db = dustbin_db.child("latitude");
                    longitude_db = dustbin_db.child("longitude");
                    type_db = dustbin_db.child("type");

                    latitude_db.setValue(lat_com);
                    longitude_db.setValue(lon_com);
                    type_db.setValue("SMELLY");

                    Toast.makeText(getApplicationContext(),"Complaint Submitted", Toast.LENGTH_SHORT).show();

                    lat_com = "";
                    lon_com = "";
                    ll.setVisibility(View.GONE);
                    ll2.setVisibility(View.GONE);
                }

                else
                    Toast.makeText(getApplicationContext(),"Please Select a Dustbin to Complain", Toast.LENGTH_SHORT).show();

            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }

        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener()
        {
            @Override
            public void onMapClick(LatLng arg0)
            {
                ll.setVisibility(View.GONE);
                ll2.setVisibility(View.GONE);

            }
        });

        for (i = 0; i < n; i++) {
            db = FirebaseDatabase.getInstance().getReference().child("dustbin").child(Integer.toString(i + 1));
            db.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Map<String, String> map_new = (Map<String, String>) dataSnapshot.getValue();

                    image_path = map_new.get("image");
                    latitude = map_new.get("latitude");
                    longitude = map_new.get("longitude");
                    place = map_new.get("place");

                    LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);

                    markerOptions.title(place);

                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.dustbin_1));

                    mCurrLocationMarker = mMap.addMarker(markerOptions);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {


        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(26.25046847,78.17095402)));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(19));

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;
        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(19));

        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                ll.setVisibility(View.VISIBLE);
                ll2.setVisibility(View.VISIBLE);

                lat_com = String.valueOf(marker.getPosition().latitude);
                lon_com = String.valueOf(marker.getPosition().longitude);
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                ll.setVisibility(View.VISIBLE);
                ll2.setVisibility(View.VISIBLE);

                lat_com = String.valueOf(marker.getPosition().latitude);
                lon_com = String.valueOf(marker.getPosition().longitude);
                return null;
            }

        });
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        ll.setVisibility(View.VISIBLE);
        ll2.setVisibility(View.VISIBLE);
        lat_com = String.valueOf(marker.getPosition().latitude);
        lon_com = String.valueOf(marker.getPosition().longitude);
        return true;
    }

}
