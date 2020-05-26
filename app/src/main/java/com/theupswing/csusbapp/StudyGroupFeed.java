package com.theupswing.csusbapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

public class StudyGroupFeed extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_group_feed);

        ImageView button = findViewById(R.id.add_group_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StudyGroupFeed.this, StudyGroupNewPost.class);
                startActivity(intent);
            }
        });

        setUpSpinners();

        showPosts();
    }

    /**
     * Initializes the Subject and Course number spinners.
     * To retrieve the data from a database, change the Array Adapter to a Query Adapter
     */
    private void setUpSpinners(){
        Spinner subjectSpinner = findViewById(R.id.subject);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.subjects_array, R.layout.study_group_spinner_items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subjectSpinner.setAdapter(adapter);

        Spinner numberSpinner = findViewById(R.id.course_number);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.course_numbers_array, R.layout.study_group_spinner_items);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        numberSpinner.setAdapter(adapter2);
    }

    /**
     * Creates block objects and posts them in the feed
     */
    private void showPosts(){
        StudyGroupBlock testBlock1 = new StudyGroupBlock(this,
                "CSE308",
                "Dr. Fadi Muheidat",
                "Midterm Review",
                "I want to go over chapter 5 in the book",
                2,5);

        StudyGroupBlock testBlock2 = new StudyGroupBlock(this,
                "CSE202",
                "Dr. Mathew Harvel",
                "Binary Search Trees",
                "I am going to be practicing Binary Search Tree exercises from the lectures and book. Feel free to join!",
                1,2);

        StudyGroupBlock testBlock3 = new StudyGroupBlock(this,
                "BIOL200",
                "Dr. Nicholas Cordero",
                "Mitosis vs Meiosis",
                "I'm struggling differenting between the two concepts. I'm going to be creating detailed notes and flashcards on each if anyone would like to join.",
                3,3);


        LinearLayout linearLayout = findViewById(R.id.linear_layout);
        testBlock1.showPost(linearLayout);
        testBlock2.showPost(linearLayout);
        testBlock3.showPost(linearLayout);
    }

}
