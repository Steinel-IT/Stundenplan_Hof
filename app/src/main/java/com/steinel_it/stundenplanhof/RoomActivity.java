package com.steinel_it.stundenplanhof;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.steinel_it.stundenplanhof.data_manager.StorageManager;

public class RoomActivity extends AppCompatActivity {

    private StorageManager storageManager;

    private LocationManager locationManager;

    private WebView webView;
    private String room, building;
    private Location location;
    private Location currLocation;
    private boolean gpsStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        webView = findViewById(R.id.webViewRoomLoc);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        storageManager = new StorageManager();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //TODO Fehler bei der Rotation Überprüfen. Werden Objecte gespeichert automatisch bei der Rotation?
        if (savedInstanceState != null) {
            room = savedInstanceState.getString("room");
            building = savedInstanceState.getString("building");
            gpsStatus = savedInstanceState.getBoolean("gpsStatus");
            System.out.println(savedInstanceState.getDouble("currLocationLat") != 0.0 && savedInstanceState.getDouble("currLocationLong") != 0.0);
            if(savedInstanceState.getDouble("currLocationLat") != 0.0 && savedInstanceState.getDouble("currLocationLong") != 0.0) {
                currLocation = new Location("");
                currLocation.setLatitude(savedInstanceState.getDouble("currLocationLat"));
                currLocation.setLongitude(savedInstanceState.getDouble("currLocationLong"));
                currLocation.setAltitude(savedInstanceState.getDouble("currLocationAlt"));
            }
        } else {
            Bundle extras = getIntent().getExtras();
            room = extras.getString(MainActivity.EXTRA_MESSAGE_ROOM);
            building = extras.getString(MainActivity.EXTRA_MESSAGE_BUILDING);
        }
        gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        getSupportActionBar().setTitle(getString(R.string.room) + ": " + room);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loadCoordinates();
        setLocationListener();
        setupContent();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("room", room);
        savedInstanceState.putString("building", building);
        savedInstanceState.putBoolean("gpsStatus", gpsStatus);
        if(currLocation != null) {
            savedInstanceState.putDouble("currLocationLat", currLocation.getLatitude());
            savedInstanceState.putDouble("currLocationLong", currLocation.getLongitude());
            savedInstanceState.putDouble("currLocationAlt", currLocation.getAltitude());
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadCoordinates() {
        location = storageManager.getRoomGPS(RoomActivity.this, "roomCoord", room);
    }

    private void setupContent() {
        TextView textViewHeader = findViewById(R.id.textViewRoomHeader);
        TextView textViewBuilding = findViewById(R.id.textViewRoomBuilding);
        TextView textViewFloor = findViewById(R.id.textViewRoomFloor);
        TextView textViewGPS = findViewById(R.id.textViewRoomGPS);
        TextView textViewCurrLoc = findViewById(R.id.textViewCurrLoc);
        textViewHeader.setText(room);
        textViewBuilding.setText(String.format("%s: %s", getString(R.string.building), building));
        if (room.contains("F")) {
            int roomNumber = Integer.parseInt(room.substring(2, 5));
            textViewFloor.setText(String.format("%s: %s", getString(R.string.floor), roomNumber / 100));
        } else {
            textViewFloor.setText(getString(R.string.virtuel));
        }
        if (location != null) {
            textViewGPS.setText(String.format("%s:\nLat:%s Long:%s Alt:%s", getString(R.string.coordinates), location.getLatitude(), location.getLongitude(), location.getAltitude()));
            webView.setVisibility(View.VISIBLE);
            webView.loadUrl("https://www.google.com/maps/search/?api=1&query=" + location.getLatitude() + "," + location.getLongitude());
        } else {
            textViewGPS.setText(String.format("%s: %s", getString(R.string.coordinates), getString(R.string.unknown)));
        }
        textViewCurrLoc.setText(String.format("%s: %s", getString(R.string.currPos), getString(R.string.isLoading)));
    }

    private void setLocationListener() {
        if (ContextCompat.checkSelfPermission(RoomActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(RoomActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(RoomActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            if (gpsStatus) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5, new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        //TODO: Wird nicht geladen auf Physical Device
                        TextView textViewGPS = findViewById(R.id.textViewCurrLoc);
                        currLocation = location;
                        textViewGPS.setText(String.format("%s: Lat:%s Long:%s Alt:%s", getString(R.string.isLoading), location.getLatitude(), location.getLongitude(), location.getAltitude()));
                        Toast.makeText(RoomActivity.this, getString(R.string.locationLoaded), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onProviderEnabled(@NonNull String provider) {
                        System.out.println("Provider Enabled");
                    }

                    @Override
                    public void onProviderDisabled(@NonNull String provider) {
                        System.out.println("Provider Disabled");
                    }
                });
            } else {
                Toast.makeText(RoomActivity.this, getString(R.string.enableLoc), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void onClickGpsSave(View view) {
        if (currLocation == null) {
            Toast.makeText(RoomActivity.this, getString(R.string.loadCoord), Toast.LENGTH_SHORT).show();
        } else {
            TextView textViewGPS = findViewById(R.id.textViewRoomGPS);
            storageManager.saveRoomGPS(RoomActivity.this, "roomCoord", room, currLocation);
            textViewGPS.setText(String.format("%s: Lat:%s Long:%s Alt:%s", getString(R.string.coordinates), currLocation.getLatitude(), currLocation.getLongitude(), currLocation.getAltitude()));
            webView.setVisibility(View.VISIBLE);
            webView.loadUrl("https://www.google.com/maps/search/?api=1&query=" + currLocation.getLatitude() + "," + currLocation.getLongitude());
            if(location == null) location = new Location("");
            location.set(currLocation);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String per[], int[] resultPerm) {
        if (requestCode == 1) {
            if (resultPerm.length > 0 && resultPerm[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(RoomActivity.this, getString(R.string.permissionGranted), Toast.LENGTH_SHORT).show();
                setLocationListener();
            } else {
                Toast.makeText(RoomActivity.this, getString(R.string.permissionNotGranted), Toast.LENGTH_SHORT).show();
            }
        }
    }
}