package com.example.lostandfound;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
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
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CreateAdvertActivity extends AppCompatActivity {

    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;
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

// Commented out Google Maps autocomplete – not required for assignment stage
//        Places.initialize(getApplicationContext(), "AIzaSyCpAX_odlXTiTVW053-e92V6i-YMUSr418");
//
//        locationEditText.setFocusable(false);
//        locationEditText.setClickable(true);
//        locationEditText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS);
//
//                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
//                        .build(CreateAdvertActivity.this);
//                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
//            }
//        });

        //Retrieve current location button
        getCurrentLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkLocationPermission()) {
                    getCurrentLocation();
                } else {
                    requestLocationPermission();
                }
            }
        });

        //Save button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                String date = sdf.format(new Date());
                values.put(AdvertDbHelper.COLUMN_DATE, date);

                db.insert(AdvertDbHelper.TABLE_NAME, null, values);

                Toast.makeText(CreateAdvertActivity.this, "Advert created successfully", Toast.LENGTH_SHORT).show();

                finish();
            }
        });

        //Back button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Closes current screen and returns to previous one
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                String address = place.getAddress();
                locationEditText.setText(address);
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // Handle error
                Toast.makeText(CreateAdvertActivity.this, "Error: " + Autocomplete.getStatusFromIntent(data).getStatusMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean checkLocationPermission() {
        int permissionResult = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionResult == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getCurrentLocation() {
        if (checkLocationPermission()) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();
                                getAddressFromLocation(latitude, longitude);
                            } else {
                                Toast.makeText(CreateAdvertActivity.this, "Unable to retrieve current location", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void getAddressFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (!addresses.isEmpty()) {
                Address address = addresses.get(0);
                String addressLine = address.getAddressLine(0);
                locationEditText.setText(addressLine);
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
