package com.example.lostandfound;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CreateAdvertActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 2;

    private AdvertDbHelper dbHelper;
    private FusedLocationProviderClient fusedLocationClient;

    private CheckBox isLostCheckBox;
    private CheckBox isFoundCheckBox;
    private EditText nameEditText;
    private EditText phoneEditText;
    private EditText descriptionEditText;
    private EditText locationEditText;
    private Button saveButton;
    private Button getCurrentLocationButton;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_advert);

        dbHelper = new AdvertDbHelper(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        isLostCheckBox = findViewById(R.id.isLostCheckBox);
        isFoundCheckBox = findViewById(R.id.isFoundCheckBox);
        nameEditText = findViewById(R.id.nameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        locationEditText = findViewById(R.id.locationEditText);
        saveButton = findViewById(R.id.saveButton);
        getCurrentLocationButton = findViewById(R.id.getCurrentLocationButton);
        btnBack = findViewById(R.id.btnBackToMenu);

        locationEditText.setVisibility(View.GONE); // hide from UI

        // Initialize Places API
        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));

        // Setup Places Autocomplete Fragment
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setPlaceFields(Arrays.asList(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG
        ));
        autocompleteFragment.setHint("Search location");

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                String address = place.getAddress();
                locationEditText.setText(address); // store value for DB
            }

            @Override
            public void onError(@NonNull Status status) {
                Toast.makeText(CreateAdvertActivity.this, "Error: " + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        getCurrentLocationButton.setOnClickListener(view -> {
            if (checkLocationPermission()) {
                getCurrentLocation();
            } else {
                requestLocationPermission();
            }
        });

        saveButton.setOnClickListener(view -> {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(AdvertDbHelper.COLUMN_IS_LOST, isLostCheckBox.isChecked() ? 1 : 0);
            values.put(AdvertDbHelper.COLUMN_IS_FOUND, isFoundCheckBox.isChecked() ? 1 : 0);
            values.put(AdvertDbHelper.COLUMN_NAME, nameEditText.getText().toString());
            values.put(AdvertDbHelper.COLUMN_PHONE, phoneEditText.getText().toString());
            values.put(AdvertDbHelper.COLUMN_DESCRIPTION, descriptionEditText.getText().toString());
            values.put(AdvertDbHelper.COLUMN_LOCATION, locationEditText.getText().toString());

            double latitude = 0.0;
            double longitude = 0.0;
            String address = locationEditText.getText().toString();
            if (!address.isEmpty()) {
                Geocoder geocoder = new Geocoder(CreateAdvertActivity.this, Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocationName(address, 1);
                    if (!addresses.isEmpty()) {
                        Address location = addresses.get(0);
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            values.put(AdvertDbHelper.COLUMN_LATITUDE, latitude);
            values.put(AdvertDbHelper.COLUMN_LONGITUDE, longitude);

            String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
            values.put(AdvertDbHelper.COLUMN_DATE, date);

            db.insert(AdvertDbHelper.TABLE_NAME, null, values);
            Toast.makeText(CreateAdvertActivity.this, "Advert created successfully", Toast.LENGTH_SHORT).show();
            finish();
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private boolean checkLocationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void getCurrentLocation() {
        if (checkLocationPermission()) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            getAddressFromLocation(latitude, longitude);
                        } else {
                            Toast.makeText(CreateAdvertActivity.this, "Unable to retrieve current location", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void getAddressFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (!addresses.isEmpty()) {
                locationEditText.setText(addresses.get(0).getAddressLine(0));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
