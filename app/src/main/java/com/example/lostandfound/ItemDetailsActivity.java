package com.example.lostandfound;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ItemDetailsActivity extends AppCompatActivity {

    private AdvertDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        dbHelper = new AdvertDbHelper(this);

        int itemId = getIntent().getIntExtra("itemId", -1);

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                AdvertDbHelper.COLUMN_NAME,
                AdvertDbHelper.COLUMN_DESCRIPTION,
                AdvertDbHelper.COLUMN_LOCATION,
                AdvertDbHelper.COLUMN_DATE,
                AdvertDbHelper.COLUMN_IS_LOST,
                AdvertDbHelper.COLUMN_IS_FOUND,
                AdvertDbHelper.COLUMN_PHONE
        };

        String selection = AdvertDbHelper.COLUMN_ID + " = ?";
        String[] selectionArgs = { Integer.toString(itemId) };

        Cursor cursor = db.query(
                AdvertDbHelper.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        TextView nameTextView = findViewById(R.id.text_view_name);
        TextView phoneTextView = findViewById(R.id.text_view_phone);
        TextView descriptionTextView = findViewById(R.id.text_view_description);
        TextView locationTextView = findViewById(R.id.text_view_location);
        TextView dateTextView = findViewById(R.id.text_view_date);
        TextView statusTextView = findViewById(R.id.text_view_is_lost_or_found);

        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(AdvertDbHelper.COLUMN_NAME));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(AdvertDbHelper.COLUMN_DESCRIPTION));
            String location = cursor.getString(cursor.getColumnIndexOrThrow(AdvertDbHelper.COLUMN_LOCATION));
            String date = cursor.getString(cursor.getColumnIndexOrThrow(AdvertDbHelper.COLUMN_DATE));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow(AdvertDbHelper.COLUMN_PHONE));
            int isLost = cursor.getInt(cursor.getColumnIndexOrThrow(AdvertDbHelper.COLUMN_IS_LOST));
            int isFound = cursor.getInt(cursor.getColumnIndexOrThrow(AdvertDbHelper.COLUMN_IS_FOUND));

            nameTextView.setText(name);
            phoneTextView.setText(phone);
            descriptionTextView.setText(description);
            locationTextView.setText(location);
            dateTextView.setText(date);

            if (isLost == 1) {
                statusTextView.setText("Status: Lost");
            } else if (isFound == 1) {
                statusTextView.setText("Status: Found");
            }
        }

        cursor.close();

        //Remove button
        Button removeButton = findViewById(R.id.button_remove_item);
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                int itemId = getIntent().getIntExtra("itemId", -1);
                String selection = AdvertDbHelper.COLUMN_ID + " = ?";
                String[] selectionArgs = { Integer.toString(itemId) };
                db.delete(AdvertDbHelper.TABLE_NAME, selection, selectionArgs);
                dbHelper.close();
                Toast.makeText(ItemDetailsActivity.this, "Item removed from database", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ItemDetailsActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        //Back button
        Button btnBack = findViewById(R.id.btnBackToMenu);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Closes current screen and returns to previous one
            }
        });
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
