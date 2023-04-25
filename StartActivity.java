package com.wgu.termtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }

    public void termClick(View view) {
        Intent intent = new Intent(this, TermActivity.class);
        startActivity(intent);

    }

    public void courseClick(View view) {
        Intent intent = new Intent(this, CourseActivity.class);
        startActivity(intent);
    }

    public void assessmentClick(View view) {
        Intent intent = new Intent(this, AssessmentActivity.class);
        startActivity(intent);
    }
}