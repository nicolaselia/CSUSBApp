package com.theupswing.csusbapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class StudyGroupBlockPage extends AppCompatActivity {

    DatabaseHelper database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_group_block_page);

        Intent intent = getIntent();
        int sessionID = intent.getIntExtra("sessionID", -1);

        database = new DatabaseHelper(this);
        populatePage(sessionID);

        Button joinGroup = findViewById(R.id.join_group);
        joinGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(StudyGroupBlockPage.this, "Must be signed in to join", Toast.LENGTH_LONG).show();
            }
        });

    }

    /**
     * Fetches the session data from the database and displays it to the user
     */
    private void populatePage(int sessionID) {

        // Fetch the data
        Cursor cursor = database.getSessionInfo(sessionID);
        cursor.moveToNext();
        String course = cursor.getString(cursor.getColumnIndex(database.COL_COURSE_SUBJECT))
                + cursor.getString(cursor.getColumnIndex(database.COL_COURSE_NUMBER));
        String instructor = cursor.getString(cursor.getColumnIndex(database.COL_INSTRUCTOR_NAME));
        String classTime = cursor.getString(cursor.getColumnIndex(database.COL_CLASS_TIME));
        String meetingDate = cursor.getString(cursor.getColumnIndex(database.COL_MEETING_DATE));
        String meetingTime = cursor.getString(cursor.getColumnIndex(database.COL_MEETING_TIME));
        String meetingLocation = cursor.getString(cursor.getColumnIndex(database.COL_MEETING_LOC));
        String topic = cursor.getString(cursor.getColumnIndex(database.COL_TOPIC));
        String description = cursor.getString(cursor.getColumnIndex(database.COL_DESCRIPTION));
        int totalSpots = cursor.getInt(cursor.getColumnIndex(database.COL_MAX_PARTICIPANTS));

        // Display the data
        TextView courseData = findViewById(R.id.course_data);
        courseData.setText(Html.fromHtml("<b>Course:</b> " + course));
        TextView instructorData = findViewById(R.id.instructor_data);
        instructorData.setText(Html.fromHtml("<b>Instructor:</b> " + instructor));
        TextView classTimeData = findViewById(R.id.class_time_data);
        classTimeData.setText(Html.fromHtml("<b>Class Time:</b> " + classTime));
        TextView dateData = findViewById(R.id.date_data);
        dateData.setText(Html.fromHtml("<b>Date:</b> " + meetingDate));
        TextView timeData = findViewById(R.id.time_data);
        timeData.setText(Html.fromHtml("<b>Time:</b> " + meetingTime));
        TextView locationData = findViewById(R.id.location_data);
        locationData.setText(Html.fromHtml("<b>Location:</b> " + meetingLocation));
        TextView topicData = findViewById(R.id.topic_data);
        topicData.setText(Html.fromHtml("<b>Topic:</b> " + topic));
        TextView descriptionData = findViewById(R.id.description_data);
        descriptionData.setText(Html.fromHtml("<b>Description:</b> " + description));

        if(totalSpots != -1) {
            TextView spotsLeft = findViewById(R.id.spots_left_data);
            spotsLeft.setText(totalSpots + "/" + totalSpots + " spots remaining");  // Change this later to reflect the real time number of participants
        }

    }
}
