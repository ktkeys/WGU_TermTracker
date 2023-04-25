package com.wgu.termtracker;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AssessmentActivity extends AppCompatActivity implements AssessmentDialogFragment.OnAssessmentEnteredListener {

    private AssessmentActivity.AssessmentAdapter mAssessmentAdapter;
    private int[] mAssessmentColors;
    private Assessment mSelectedAssessment;
    private RecyclerView mRecyclerView;
    private int mSelectedAssessmentPosition = RecyclerView.NO_POSITION;
    private ActionMode mActionMode = null;

    private Assessment mDeletedAssessment;

    private TermDatabase mStudyDb;
    private String mTerm;
    private List<Assessment> mAssessmentList;
    private TextView mAnswerLabel;
    private TextView mAnswerText;
    private Button mAnswerButton;
    private TextView mAssessmentText;
    private int mCurrentAssessmentIndex;
    private ViewGroup mShowAssessmentLayout;
    private ViewGroup mNoAssessmentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment);
    }

    public void addAssessmentClick(View view) {
        // Take user to new Assessment form
        Intent intent = new Intent(AssessmentActivity.this, AddAssessmentActivity.class);
        startActivity(intent);


        /* FragmentManager manager = getSupportFragmentManager();
        AssessmentDialogFragment dialog = new AssessmentDialogFragment();
        dialog.show(manager, "assessmentDialog");*/
    }

    @Override
    public void onAssessmentEntered(String assessment) {
        //returns the subject entered in the subject dialog fragment dialog
        if(assessment.length()> 0) {
            Assessment cour = new Assessment();
            /* TODO: create the mthod in the database file
                if (mStudyDb.addAssessment(cour)){
                mAssessmentAdapter.addAssessment(cour);
                Toast.makeText(this, "added " + assessment, Toast.LENGTH_SHORT).show();
            } else {
                String message = getResources().getString(R.string.assessment_exists, assessment);
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }*/
        }
    }

    private class AssessmentHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        private Assessment mAssessment;
        private TextView mTextView;

        public AssessmentHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.recycler_view_items, parent, false));
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            mTextView = itemView.findViewById(R.id.termTextView);
        }

        public void bind(Assessment assessment, int position){
            mAssessment = assessment;
            mTextView.setText(assessment.getTitle());

            if(mSelectedAssessmentPosition == position){
                //make selected subject standout
                mTextView.setBackgroundColor(Color.RED);
            } else {
                //make background color dependent on the length of the subject string
                int colorIndex = assessment.getTitle().length() % mAssessmentColors.length;
                mTextView.setBackgroundColor(mAssessmentColors[colorIndex]);
            }
        }

        @Override
        public boolean onLongClick(View view){
            if(mActionMode != null) {
                return false;
            }
            mSelectedAssessment = mAssessment;
            mSelectedAssessmentPosition = getAdapterPosition();

            //rebind the selected item
            mAssessmentAdapter.notifyItemChanged(mSelectedAssessmentPosition);

            //Show the CAB
            mActionMode = AssessmentActivity.this.startActionMode(mActionModeCallback);
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
                        //mStudyDb.deleteAssessment(mSelectedAssessment);
                        mAssessmentAdapter.removeAssessment(mSelectedAssessment);

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
                mAssessmentAdapter.notifyItemChanged(mSelectedAssessmentPosition);
                mSelectedAssessmentPosition = RecyclerView.NO_POSITION;

            }
        };

        @Override
        public void onClick(View view){
            // start assessment activity
            //TODO: create the activities for viewing and adding stuff
        }
    }

    private class AssessmentAdapter extends RecyclerView.Adapter<AssessmentActivity.AssessmentHolder> {
        private List<Assessment> mAssessmentList;

        public AssessmentAdapter(List<Assessment> assessments) {mAssessmentList = assessments;}

        public void addAssessment(Assessment assessment){
            //add new assessment at beginning
            mAssessmentList.add(0, assessment);

            //notify the adapter that the item was added
            notifyItemInserted(0);

            //scroll to top
            mRecyclerView.scrollToPosition(0);
        }

        public void removeAssessment(Assessment assessment) {
            //find subject in list
            int index = mAssessmentList.indexOf(assessment);
            if (index >= 0) {
                mAssessmentList.remove(index);

                //notify adapter
                notifyItemRemoved(index);
            }
        }

        @Override
        public AssessmentActivity.AssessmentHolder onCreateViewHolder(ViewGroup parent, int viewType){
            LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
            return new AssessmentActivity.AssessmentHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder (AssessmentActivity.AssessmentHolder holder, int position){
            holder.bind(mAssessmentList.get(position), position);
        }

        @Override
        public int getItemCount(){
            return mAssessmentList.size();
        }

    }
}
