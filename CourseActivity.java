package com.wgu.termtracker;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class CourseActivity extends AppCompatActivity implements CourseDialogFragment.OnCourseEnteredListener {

    public static final String EXTRA_TERM = "com.wgu.termtracker";

    private final int REQUEST_CODE_NEW_COURSE = 0;
    private final int REQUEST_CODE_UPDATE_COURSE = 1;

    private CourseAdapter mCourseAdapter;
    private int[] mCourseColors;
    private Course mSelectedCourse;
    private RecyclerView mRecyclerView;
    private int mSelectedCoursePosition = RecyclerView.NO_POSITION;
    private ActionMode mActionMode = null;

    private Course mDeletedCourse;

    private TermDatabase mStudyDb;
    private String mTerm;
    private List<Course> mCourseList;
    private TextView mAnswerLabel;
    private TextView mAnswerText;
    private Button mAnswerButton;
    private TextView mCourseText;
    private int mCurrentCourseIndex;
    private ViewGroup mShowCoursesLayout;
    private ViewGroup mNoCoursesLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        /*
        //Hosting activity provides the term of the courses to dipslay
        Intent intent = getIntent();
        mTerm = intent.getStringExtra(EXTRA_TERM);

        // Load all courses for this term
        mStudyDb = TermDatabase.getInstance(getApplicationContext());
        mCourseList = mStudyDb.getCourses(mTerm);

        mCourseText = findViewById(R.id.courseText);
        mAnswerLabel = findViewById(R.id.answerLabel);
        mAnswerText = findViewById(R.id.answerText);
        mAnswerButton = findViewById(R.id.answerButton);
        mShowCoursesLayout = findViewById(R.id.showCoursesLayout);
        mNoCoursesLayout = findViewById(R.id.noCoursesLayout);

        //show first qustion
        showCourse(0); */
    }

    @Override
    protected void onStart(){
        super.onStart();

        /*
        //are there courses to display?
        if (mCourseList.size() == 0){
            updateAppBarTitle();
            displayCourse(false);
        } else {
            displayCourse(true);
            toggleAnswerVisibility();
        } */
    }

    public void addCourseClick(View view) {
        // Enter new course form
        Intent intent = new Intent(CourseActivity.this, AddCourseActivity.class);
        startActivity(intent);


        /*
        FragmentManager manager = getSupportFragmentManager();
        CourseDialogFragment dialog = new CourseDialogFragment();
        dialog.show(manager, "courseDialog"); */
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        //inflate menu for the app bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.course_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        //determine which app bar item was chosen
        switch (item.getItemId()){
            case R.id.previous:
                showCourse(mCurrentCourseIndex - 1);
                return true;
            case R.id.next:
                showCourse(mCurrentCourseIndex + 1);
                return true;
            case R.id.add:
                addCourse();
                return true;
            case R.id.delete:
                deleteCourse();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void addCourseButtonClick(View view){
        addCourse();
    }

    public void answerButtonClick(View view){
        toggleAnswerVisibility();
    }

    private void displayCourse (boolean display){
        //show or hid the appropriate scrren
        if(display){
            mShowCoursesLayout.setVisibility(View.VISIBLE);
            mNoCoursesLayout.setVisibility(View.GONE);
        } else {
            mShowCoursesLayout.setVisibility(View.GONE);
            mNoCoursesLayout.setVisibility(View.VISIBLE);
        }
    }

    private void updateAppBarTitle() {
        //display term & number of courses in app bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            String title = getResources().getString(R.string.course_number, mTerm, mCurrentCourseIndex + 1, mCourseList.size());
            setTitle(title);
        }
    }

    private void addCourse(){
        Intent intent = new Intent(this, CourseEditActivity.class);
        intent.putExtra(CourseEditActivity.EXTRA_TERM, mTerm);
        startActivityForResult(intent, REQUEST_CODE_NEW_COURSE);
    }

    private void editCourse(){
        if(mCurrentCourseIndex >= 0){
            Intent intent = new Intent(this, CourseEditActivity.class);
            intent.putExtra(EXTRA_TERM, mTerm);
            int courseId = mCourseList.get(mCurrentCourseIndex).getID();
            intent.putExtra(CourseEditActivity.EXTRA_COURSE_ID, courseId);
            startActivityForResult(intent, REQUEST_CODE_UPDATE_COURSE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == REQUEST_CODE_NEW_COURSE){
            //get added course
            int courseID = data.getIntExtra(CourseEditActivity.EXTRA_COURSE_ID, -1);
            Course newCourse = mStudyDb.getCourse(courseID);

            //add newly created course to the course list and show
            mCourseList.add(newCourse);
            showCourse(mCourseList.size() - 1);

            Toast.makeText(this, R.string.course_added, Toast.LENGTH_SHORT).show();
        } else if(resultCode == RESULT_OK && requestCode == REQUEST_CODE_UPDATE_COURSE){
            // get updated quetion
            int courseId = data.getIntExtra(CourseEditActivity.EXTRA_COURSE_ID, -1);
            Course updatedCourse = mStudyDb.getCourse(courseId);

            //replace qustion in list with updatd course
            Course currentCourse = mCourseList.get(mCurrentCourseIndex);
            currentCourse.setTitle(updatedCourse.getTitle());
            //currentCourse.setAnswer(updatedCourse.getAnswer());
            showCourse(mCurrentCourseIndex);

            Toast.makeText(this, R.string.course_updated, Toast.LENGTH_SHORT).show();
        } {

        }
    }

    private void deleteCourse(){
        if(mCurrentCourseIndex >= 0){
            // save course in case user undoes delete
            mDeletedCourse = mCourseList.get(mCurrentCourseIndex);
            mStudyDb.deleteCourse(mDeletedCourse.getID());
            int courseId = mCourseList.get(mCurrentCourseIndex).getID();
            mStudyDb.deleteCourse(courseId);
            mCourseList.remove(mCurrentCourseIndex);

            if(mCourseList.size() == 0){
                //no courses left to show
                mCurrentCourseIndex = -1;
                updateAppBarTitle();
                displayCourse(false);
            } else {
                showCourse(mCurrentCourseIndex);
            }

            //show delete message with udno button
            Snackbar snackbar = Snackbar.make(findViewById(R.id.coordinatorLayout), R.string.course_deleted, Snackbar.LENGTH_LONG);
            snackbar.setAction(R.string.undo, new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    //add course back
                    mStudyDb.addCourse(mDeletedCourse);
                    mCourseList.add(mDeletedCourse);
                    showCourse(mCourseList.size() - 1);
                    displayCourse(true);
                }
            });
        }

        Toast.makeText(this, R.string.course_deleted, Toast.LENGTH_SHORT).show();
    }

    private void showCourse(int courseIndex){
        //show course at given index
        if(mCourseList.size() > 0){
            if(courseIndex < 0){
                courseIndex = mCourseList.size() - 1;
            } else if (courseIndex >= mCourseList.size()){
                courseIndex = 0;
            }

            mCurrentCourseIndex = courseIndex;
            updateAppBarTitle();

            Course course = mCourseList.get(mCurrentCourseIndex);
            mCourseText.setText(course.getTitle());
           // mAnswerText.setText(course.getAnswer());
        } else {
            //no courses yet
            mCurrentCourseIndex = -1;
        }
    }

    private void toggleAnswerVisibility(){
        if(mAnswerText.getVisibility() == View.VISIBLE) {
            mAnswerButton.setText(R.string.show_answer);
            mAnswerText.setVisibility(View.INVISIBLE);
            mAnswerLabel.setVisibility(View.INVISIBLE);
        } else {
            mAnswerButton.setText(R.string.hide_answer);
            mAnswerText.setVisibility(View.VISIBLE);
            mAnswerLabel.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCourseEntered(String course) {
        //returns the subject entered in the subject dialog fragment dialog
        if(course.length()> 0) {
            Course cour = new Course();
            /* TODO: create the mthod in the database file
                if (mStudyDb.addCourse(cour)){
                mCourseAdapter.addCourse(cour);
                Toast.makeText(this, "added " + course, Toast.LENGTH_SHORT).show();
            } else {
                String message = getResources().getString(R.string.course_exists, course);
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }*/
        }
    }

    private class CourseHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        private Course mCourse;
        private TextView mTextView;

        public CourseHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.recycler_view_items, parent, false));
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            mTextView = itemView.findViewById(R.id.termTextView);
        }

        public void bind(Course course, int position){
            mCourse = course;
            mTextView.setText(course.getTitle());

            if(mSelectedCoursePosition == position){
                //make selected subject standout
                mTextView.setBackgroundColor(Color.RED);
            } else {
                //make background color dependent on the length of the subject string
                int colorIndex = course.getTitle().length() % mCourseColors.length;
                mTextView.setBackgroundColor(mCourseColors[colorIndex]);
            }
        }

        @Override
        public boolean onLongClick(View view){
            if(mActionMode != null) {
                return false;
            }
            mSelectedCourse = mCourse;
            mSelectedCoursePosition = getAdapterPosition();

            //rebind the selected item
            mCourseAdapter.notifyItemChanged(mSelectedCoursePosition);

            //Show the CAB
            mActionMode = CourseActivity.this.startActionMode(mActionModeCallback);
            return true;
        }

        private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                //process action item selected
                switch(item.getItemId()){
                    case R.id.delete:
                        //delete from the db and remove from the recycler view
                        //TODO: create the delet ethod in db
                        //mStudyDb.deleteCourse(mSelectedCourse);
                        mCourseAdapter.removeCourse(mSelectedCourse);

                        //close the CAB
                        mode.finish();
                        return true;
                    default:
                        return false;
                }

            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                mActionMode = null;

                //cab closing, need to deselect item ifnot deletd
                mCourseAdapter.notifyItemChanged(mSelectedCoursePosition);
                mSelectedCoursePosition = RecyclerView.NO_POSITION;

            }
        };

        @Override
        public void onClick(View view){
            // start course activity
            //TODO: create the activities for viewing and adding stuff
        }
    }

    private class CourseAdapter extends RecyclerView.Adapter<CourseHolder> {
        private List<Course> mCourseList;

        public CourseAdapter(List<Course> courses) {mCourseList = courses;}

        public void addCourse(Course course){
            //add new course at beginning
            mCourseList.add(0, course);

            //notify the adapter that the item was added
            notifyItemInserted(0);

            //scroll to top
            mRecyclerView.scrollToPosition(0);
        }

        public void removeCourse(Course course) {
            //find subject in list
            int index = mCourseList.indexOf(course);
            if (index >= 0) {
                mCourseList.remove(index);

                //notify adapter
                notifyItemRemoved(index);
            }
        }

            @Override
            public CourseHolder onCreateViewHolder(ViewGroup parent, int viewType){
                LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
                return new CourseHolder(layoutInflater, parent);
            }

            @Override
                    public void onBindViewHolder (CourseHolder holder, int position){
                holder.bind(mCourseList.get(position), position);
            }

            @Override
                    public int getItemCount(){
                return mCourseList.size();
            }

        }
    }
