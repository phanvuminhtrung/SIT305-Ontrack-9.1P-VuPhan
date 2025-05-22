package com.example.lostandfound;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import java.util.ArrayList;

public class ShowItemsActivity extends AppCompatActivity {

    private AdvertDbHelper dbHelper;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_items);

        dbHelper = new AdvertDbHelper(this);

        ListView listView = findViewById(R.id.list_view);

        ArrayList<String> itemsList = new ArrayList<>();
        final ArrayList<Integer> itemIdList = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                AdvertDbHelper.COLUMN_ID,
                AdvertDbHelper.COLUMN_NAME,
                AdvertDbHelper.COLUMN_IS_LOST,
                AdvertDbHelper.COLUMN_IS_FOUND
        };

        String sortOrder = AdvertDbHelper.COLUMN_DATE + " DESC";

        Cursor cursor = db.query(
                AdvertDbHelper.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );

        while (cursor.moveToNext()) {
            int isLost = cursor.getInt(cursor.getColumnIndexOrThrow(AdvertDbHelper.COLUMN_IS_LOST));
            int isFound = cursor.getInt(cursor.getColumnIndexOrThrow(AdvertDbHelper.COLUMN_IS_FOUND));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(AdvertDbHelper.COLUMN_NAME));
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(AdvertDbHelper.COLUMN_ID));

            String itemString = "Name: " + name;

            if (isLost == 1) {
                itemString += "\nLost";
            } else if (isFound == 1) {
                itemString += "\nFound";
            }

            itemsList.add(itemString);
            itemIdList.add(id);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, itemsList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int itemId = itemIdList.get(position);
                Intent intent = new Intent(ShowItemsActivity.this, ItemDetailsActivity.class);
                intent.putExtra("itemId", itemId);
                startActivity(intent);
            }
        });

        cursor.close();

        btnBack = findViewById(R.id.btnBackToMenu);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // This will return to the previous activity (main menu)
            }
        });
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
