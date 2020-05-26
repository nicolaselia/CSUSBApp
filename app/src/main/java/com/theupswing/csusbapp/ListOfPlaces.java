package com.theupswing.csusbapp;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ListOfPlaces extends AppCompatActivity {

    /**
     * This is the section of the app that shows the list of visited places.
     * To edit the list items, you must edit the location_item.xml file and the LocationItem java file.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_places);

        final ArrayList<LocationItem> locationItems = loadData();

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        final LocationAdapter adapter = new LocationAdapter(locationItems);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new LocationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String location = locationItems.get(position).getLocation();
                setUpAlertDialog(location);
            }
        });
    }

    /**
     * Returns an ArrayList of LocationItem objects to be displayed to the user
     */
    private ArrayList<LocationItem> loadData() {
        ArrayList<LocationItem> locationItems = new ArrayList<>();
        DatabaseHelper database = new DatabaseHelper(this);
        Cursor cursor = database.getAllData();

        String location;
        int visited;
        int imageResource;
        while (cursor.moveToNext()) {
            location = cursor.getString(cursor.getColumnIndex(database.COL_LOC));
            visited = cursor.getInt(cursor.getColumnIndex(database.COL_VISITED));
            imageResource = (visited == 0) ? R.drawable.ic_check_box_empty : R.drawable.ic_check_box;
            locationItems.add(new LocationItem(location, imageResource));
        }

        return locationItems;
    }

    private void setUpAlertDialog(final String location) {

        // Get data from database to display
        DatabaseHelper database = new DatabaseHelper(ListOfPlaces.this);
        String description = database.getDescription(location);
        int image = database.getImage(location);

        // Inflate alert_dialog.xml in order to edit its views
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootLayout = inflater.inflate(R.layout.alert_dialog, (ViewGroup) findViewById(R.id.root_container));

        // Edit the views
        TextView descriptionText = rootLayout.findViewById(R.id.description_text);
        descriptionText.setText(description);
        ImageView imageView = rootLayout.findViewById(R.id.location_image);
        imageView.setImageResource(image);

        // Build and show the alert dialog
        new AlertDialog.Builder(ListOfPlaces.this)
                .setView(rootLayout)
                .setTitle(location)
                .setPositiveButton("Show on Map", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(ListOfPlaces.this, ScavengerHunt.class);
                        intent.putExtra("Location", location);
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
    }
}
