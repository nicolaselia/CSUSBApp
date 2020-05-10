package com.theupswing.csusbapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeData();

        Button scavengerHunt = findViewById(R.id.scavenger_hunt_button);
        Button majorGuide = findViewById(R.id.major_guide_button);
        Button studyGroups = findViewById(R.id.study_groups_button);

        scavengerHunt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ScavengerHunt.class);
                startActivity(intent);
            }
        });

        majorGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Coming soon!", Toast.LENGTH_LONG).show();
            }
        });

        studyGroups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Coming soon!", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void initializeData(){
        SharedPreferences sharedPreferences = getSharedPreferences("InitializeData", MODE_PRIVATE);
        if(sharedPreferences.getBoolean("FirstTime", true)){
            DatabaseHelper databaseHelper = new DatabaseHelper(this);
            databaseHelper.setUpDatabase();

            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putBoolean("FirstTime", false);
            edit.apply();
        }
    }
}
