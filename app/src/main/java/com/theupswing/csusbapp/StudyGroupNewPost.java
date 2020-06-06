package com.theupswing.csusbapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StudyGroupNewPost extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private String courseSubjectText;
    private String courseNumberString;
    private String instructorNameString;
    private String classTimeString;
    private String meetingDateString;
    private String meetingTimeString;
    private String locationString;
    private String maxParticipantsString;
    private String topicString;
    private String descriptionString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_group_new_post);

        setUpSpinner();
        setUpDateAndTimePickers();

        Button submitPost = findViewById(R.id.submit_post);
        submitPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(allFieldsValid()) {
                    saveToDatabase();
                    Intent intent = new Intent(StudyGroupNewPost.this, StudyGroupFeed.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void setUpSpinner(){
        Spinner subjectSpinner = findViewById(R.id.course_subject);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.subjects_array, R.layout.study_group_spinner_items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subjectSpinner.setAdapter(adapter);
    }

    private void setUpDateAndTimePickers(){
        EditText dateEditText = findViewById(R.id.new_post_meeting_date);
        dateEditText.setKeyListener(null);
        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialogFragment = new DatePickerFragment();
                dialogFragment.show(getSupportFragmentManager(), "date picker");
            }
        });


        EditText timeEditText = findViewById(R.id.new_post_meeting_time);
        timeEditText.setKeyListener(null);
        timeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialogFragment = new TimePickerFragment();
                dialogFragment.show(getSupportFragmentManager(), "time picker");
            }
        });
    }

    /**
     * Displays the date that the user picked
     */
    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        EditText dateEditText = findViewById(R.id.new_post_meeting_date);
        dateEditText.setText(month + "/" + day + "/" + year);
    }

    /**
     * Displays the time that the user picked in 12 hour format (the time is initially received in 24 hour format)
     */
    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
        EditText timeEditText = findViewById(R.id.new_post_meeting_time);
        SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
        Date dateObj;
        try {
            dateObj = sdf.parse(hourOfDay + ":" + minute);
            timeEditText.setText(new SimpleDateFormat("hh:mm aa").format(dateObj));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Make sure that the user entered valid values into all the fields
     */
    private boolean allFieldsValid(){
        // Check if course is valid
        Spinner courseSubjectSpinner = (Spinner) findViewById(R.id.course_subject);
        courseSubjectText = courseSubjectSpinner.getSelectedItem().toString();
        EditText courseNumberEditText = findViewById(R.id.new_post_course_number);
        courseNumberString = courseNumberEditText.getText().toString();

        if(courseSubjectText.equals("--") || courseNumberString.isEmpty()){
            Toast.makeText(StudyGroupNewPost.this, "Please select a valid course", Toast.LENGTH_LONG).show();
            return false;
        }

        // Check Instructor Name
        EditText instructorNameEditText = findViewById(R.id.new_post_instructor_name);
        instructorNameString = instructorNameEditText.getText().toString();
        if(instructorNameString.isEmpty()){
            Toast.makeText(StudyGroupNewPost.this, "Please enter an instructor name", Toast.LENGTH_LONG).show();
            return false;
        }

        // Check Class Time
        EditText classTimeEditText = findViewById(R.id.new_post_class_time);
        classTimeString = classTimeEditText.getText().toString();
        if(classTimeString.isEmpty()){
            Toast.makeText(StudyGroupNewPost.this, "Please enter a class time", Toast.LENGTH_LONG).show();
            return false;
        }

        // Check Date and Time
        EditText meetingDateEditText = findViewById(R.id.new_post_meeting_date);
        meetingDateString = meetingDateEditText.getText().toString();
        EditText meetingTimeEditText = findViewById(R.id.new_post_meeting_time);
        meetingTimeString = meetingTimeEditText.getText().toString();
        if(meetingDateString.isEmpty() || meetingTimeString.isEmpty()){
            Toast.makeText(StudyGroupNewPost.this, "Please select a meeting date and time", Toast.LENGTH_LONG).show();
            return false;
        }

        // Check location
        EditText locationEditText = findViewById(R.id.new_post_location);
        locationString = locationEditText.getText().toString();
        if(locationString.isEmpty()){
            Toast.makeText(StudyGroupNewPost.this, "Please enter a location", Toast.LENGTH_LONG).show();
            return false;
        }

        // Check max number of participants
        EditText maxParticipantsEditText = findViewById(R.id.new_post_max_participants);
        maxParticipantsString = maxParticipantsEditText.getText().toString();
        if(maxParticipantsString.isEmpty()){
            maxParticipantsString = "-1";
        }

        // Check topic
        EditText topicEditText = findViewById(R.id.new_post_topic);
        topicString = topicEditText.getText().toString();
        if(topicString.isEmpty()){
            Toast.makeText(StudyGroupNewPost.this, "Please enter a topic", Toast.LENGTH_LONG).show();
            return false;
        }

        // Check description
        EditText descriptionEditText = findViewById(R.id.new_post_description);
        descriptionString = descriptionEditText.getText().toString();
        if(descriptionString.isEmpty()){
            Toast.makeText(StudyGroupNewPost.this, "Please enter a description", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private void saveToDatabase(){
        DatabaseHelper database = new DatabaseHelper(StudyGroupNewPost.this);
        int success = database.insertDataStudyGroup(courseSubjectText,
                Integer.parseInt(courseNumberString),
                instructorNameString,
                classTimeString,
                meetingDateString,
                meetingTimeString,
                locationString,
                Integer.parseInt(maxParticipantsString),
                topicString,
                descriptionString);
        Toast.makeText(StudyGroupNewPost.this, success != -1?"Success":"Fail", Toast.LENGTH_LONG).show();
    }
}
