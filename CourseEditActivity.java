package com.wgu.termtracker;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class CourseEditActivity extends AppCompatActivity {

    public static final String EXTRA_COURSE_ID = "com.wgu.termtracker.ID";
    public static final String EXTRA_TERM = "com.wgu.termtracker.title";

    private EditText mCourseText;
    private EditText mAnswerText;

    private TermDatabase mStudyDb;
    private int mCourseId;
    private Course mCourse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_edit);

        mCourseText = findViewById(R.id.courseText);
        mAnswerText = findViewById(R.id.answerText);

        mStudyDb = TermDatabase.getInstance(getApplicationContext());

        //Get course ID from Questoin Activity
        Intent intent = getIntent();
        mCourseId = intent.getIntExtra(EXTRA_COURSE_ID, -1);

        ActionBar actionBar = getSupportActionBar();

        if (mCourseId == -1) {
            //Add new course
            mCourse = new Course();
            setTitle(R.string.add_course);
        } else {
            //update existing course
            mCourse = mStudyDb.getCourse(mCourseId);
            mCourseText.setText(mCourse.getTitle());

            setTitle(R.string.update_course);
        }

        String subject = intent.getStringExtra(EXTRA_TERM);
        //mCourse.setTerm(subject);
    }

    public void saveButtonClick(View view){
        mCourse.setTitle(mCourseText.getText().toString());
       // mCourse.setAnswer(mAnswerText.getText().toString());

        if(mCourseId == -1){
            //New Course
            mStudyDb.addCourse(mCourse);
        } else {
            //existing course
            mStudyDb.updateCourse(mCourse);
        }

        //send back Course ID
        Intent intent = new Intent();
        intent.putExtra(EXTRA_COURSE_ID, mCourse.getID());
        setResult(RESULT_OK, intent);
        finish();
    }

    }
