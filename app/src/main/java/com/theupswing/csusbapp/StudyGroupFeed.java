package com.theupswing.csusbapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.util.ArrayList;

public class StudyGroupFeed extends AppCompatActivity {

    DatabaseHelper database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_group_feed);
        database = new DatabaseHelper(StudyGroupFeed.this);

        ImageView button = findViewById(R.id.add_group_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StudyGroupFeed.this, StudyGroupNewPost.class);
                startActivity(intent);
            }
        });

        setUpSpinners();

        //showDummyPosts();
        showPosts();
    }

    /**
     * Initializes the Subject and Course number spinners.
     * To retrieve the data from a database, change the Array Adapter to a Query Adapter
     */
    private void setUpSpinners() {
        final LinearLayout linearLayout = findViewById(R.id.linear_layout);

        // Set Up Subject Spinner
        Spinner subjectSpinner = findViewById(R.id.subject_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.subjects_array, R.layout.study_group_spinner_items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subjectSpinner.setAdapter(adapter);
        subjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            int previousPosition1 = 0;

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (position != previousPosition1) {
                    linearLayout.removeAllViews();
                    showPosts();
                    previousPosition1 = position;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // Set Up Number Spinner
        Spinner numberSpinner = findViewById(R.id.course_number_spinner);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.course_numbers_array, R.layout.study_group_spinner_items);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        numberSpinner.setAdapter(adapter2);
        numberSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            int previousPosition2 = 0;

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (position != previousPosition2) {
                    linearLayout.removeAllViews();
                    showPosts();
                    previousPosition2 = position;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    /**
     * Populates the layout with the filtered posts from the database
     */
    private void showPosts() {
        Spinner subjectSpinner = findViewById(R.id.subject_spinner);
        String subject = subjectSpinner.getSelectedItem().toString();
        Spinner numberSpinner = findViewById(R.id.course_number_spinner);
        String number = numberSpinner.getSelectedItem().toString();

        ArrayList<StudyGroupBlock> studyGroupBlocks = getBlocksFromDatabase(subject, number);
        LinearLayout linearLayout = findViewById(R.id.linear_layout);
        for (StudyGroupBlock block : studyGroupBlocks) {
            block.showPost(linearLayout);
        }
    }

    /**
     * Looks for the requested information in the database and returns them in an ArrayList
     *
     * @param subject: the specific course subject (first spinner)
     * @param number:  the specific course number (second spinner)
     * @return the ArrayList containing all the queried StudyGroupBlock objects
     */
    private ArrayList<StudyGroupBlock> getBlocksFromDatabase(String subject, String number) {

        ArrayList<StudyGroupBlock> studyGroupBlocks = new ArrayList<>();
        Cursor cursor = database.getDataForStudyGroups(subject, number);
        while (cursor.moveToNext()) {
            String course = cursor.getString(cursor.getColumnIndex(database.COL_COURSE_SUBJECT))
                    + cursor.getString(cursor.getColumnIndex(database.COL_COURSE_NUMBER));
            String instructor = cursor.getString(cursor.getColumnIndex(database.COL_INSTRUCTOR_NAME));
            String topic = cursor.getString(cursor.getColumnIndex(database.COL_TOPIC));
            String description = cursor.getString(cursor.getColumnIndex(database.COL_DESCRIPTION));
            int totalSpots = cursor.getInt(cursor.getColumnIndex(database.COL_MAX_PARTICIPANTS));
            int sessionID = cursor.getInt(cursor.getColumnIndex(database.COL_SESSION_ID));

            studyGroupBlocks.add(new StudyGroupBlock(StudyGroupFeed.this,
                    course,
                    instructor,
                    topic,
                    description,
                    totalSpots, // Change this later to reflect the real time number of participants
                    totalSpots,
                    sessionID));
        }

        return studyGroupBlocks;
    }

}
