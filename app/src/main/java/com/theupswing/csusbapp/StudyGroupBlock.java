package com.theupswing.csusbapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class StudyGroupBlock {
    String course, professor, subject, description;
    int spotsLeft, totalSpots;
    Context context;
    int sessionID;

    public StudyGroupBlock(Context context, String course, String instructor, String topic, String description, int spotsLeft, int totalSpots, int sessionID){
        this.context = context;
        this.course = course;
        this.professor = instructor;
        this.subject = topic;
        this.description = description;
        this.spotsLeft = spotsLeft;
        this.totalSpots = totalSpots;
        this.sessionID = sessionID;
    }

    /**
     * Creates a post UI object in the form of a linear layout view
     */
    public LinearLayout getView(){
        // Converts number into px value to be used for margins
        Resources r = context.getResources();
        int margin_px = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                24, // margin value
                r.getDisplayMetrics()
        );

        int padding_px = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                8, // margin value
                r.getDisplayMetrics()
        );

        // Create parent Layout
        LinearLayout linearLayout = new LinearLayout(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(margin_px, margin_px, margin_px, 0);
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setBackgroundResource(R.drawable.study_group_block_bg);

        // Add first line (course + professor name)
        TextView courseAndProfessorText = new TextView(context);
        courseAndProfessorText.setText(course + " | " + professor);
        courseAndProfessorText.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        courseAndProfessorText.setTypeface(null, Typeface.ITALIC);
        courseAndProfessorText.setTextSize(TypedValue.COMPLEX_UNIT_SP,21);
        courseAndProfessorText.setTextColor(Color.parseColor("#000000"));
        courseAndProfessorText.setPadding(0, 0, 0,padding_px);
        linearLayout.addView(courseAndProfessorText);

        // Add body (subject + description)
        TextView subjectText = new TextView(context);
        subjectText.setText(subject);
        subjectText.setTypeface(null, Typeface.BOLD);
        subjectText.setTextSize(TypedValue.COMPLEX_UNIT_SP,24);
        subjectText.setTextColor(Color.parseColor("#000000"));
        TextView descriptionText = new TextView(context);
        descriptionText.setText(description);
        descriptionText.setTextSize(TypedValue.COMPLEX_UNIT_SP,24);
        descriptionText.setTextColor(Color.parseColor("#000000"));
        linearLayout.addView(subjectText);
        linearLayout.addView(descriptionText);

        // Add spots left
        if(totalSpots != -1) {
            TextView spotsLeftText = new TextView(context);
            spotsLeftText.setText(spotsLeft + "/" + totalSpots + " spots remaining");
            spotsLeftText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 21);
            spotsLeftText.setTextColor(Color.parseColor("#EE0C0C"));
            spotsLeftText.setGravity(Gravity.END);
            spotsLeftText.setPadding(0, padding_px, 0, 0);
            linearLayout.addView(spotsLeftText);
        }

        return linearLayout;
    }

    /**
     * Adds the child linear layout (the post block) into the parent linear layout (the Study Group Feed) so the users can see it
     * @param linearLayout the parent linear layout
     */
    public void showPost(LinearLayout linearLayout){
        LinearLayout layout = getView();
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, StudyGroupBlockPage.class);
                intent.putExtra("sessionID", sessionID);
                context.startActivity(intent);
            }
        });
        linearLayout.addView(layout);
    }
}
