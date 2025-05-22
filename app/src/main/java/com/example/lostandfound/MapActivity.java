package com.example.lostandfound;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        // Retrieve latitude and longitude values from the database and add markers to the map
        AdvertDbHelper dbHelper = new AdvertDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {AdvertDbHelper.COLUMN_LATITUDE, AdvertDbHelper.COLUMN_LONGITUDE};
        Cursor cursor = db.query(AdvertDbHelper.TABLE_NAME, projection, null, null, null, null, null);

        while (cursor.moveToNext()) {
            double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(AdvertDbHelper.COLUMN_LATITUDE));
            double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(AdvertDbHelper.COLUMN_LONGITUDE));

            LatLng position = new LatLng(latitude, longitude);
            googleMap.addMarker(new MarkerOptions().position(position));
        }

        cursor.close();

        // Set a default location and zoom level
        LatLng defaultLocation = new LatLng(-37.8445, 145.1123);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12f));
    }

}
