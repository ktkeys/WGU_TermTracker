package com.wgu.termtracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TermActivity extends AppCompatActivity implements TermDialogFragment.OnTermEnteredListener {

    private TermDatabase mStudyDb;
    private TermAdapter mTermAdapter;
    private RecyclerView mRecyclerView;
    private int[] mTermColors;

    private Term mSelectedTerm;
    private int mSelectedTermPosition = RecyclerView.NO_POSITION;
    private ActionMode mActionMode = null;

    private boolean mDarkTheme;
    private SharedPreferences mSharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mDarkTheme = mSharedPrefs.getBoolean(SettingsFragment.PREFERENCE_THEME, false);
        if(mDarkTheme){
            setTheme(R.style.DarkTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term);

        mTermColors = getResources().getIntArray(R.array.termColor);

        //singleton
        mStudyDb = TermDatabase.getInstance(getApplicationContext());
        mRecyclerView = findViewById(R.id.termRecyclerView);

        //create 2 grid layout columns
        RecyclerView.LayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        //shows the available terms
//        mTermAdapter = new TermAdapter(loadTerms());
      //  mRecyclerView.setAdapter(mTermAdapter);
    }

    @Override
    protected void onResume(){
        super.onResume();
        // if theme changed, recreate the activity so them is applied
        boolean darkTheme = mSharedPrefs.getBoolean(SettingsFragment.PREFERENCE_THEME, false);
        if(darkTheme != mDarkTheme){
            recreate();
        }

        //load term here in case settings changes
     //   mTermAdapter = new TermAdapter(loadTerms());
        // mRecyclerView.setAdapter(mTermAdapter);
    }

    /* private List<Term> loadTerms(){
        String order = mSharedPrefs.getString(SettingsFragment.PREFERENCE_TERM_ORDER, "1");
        switch (Integer.parseInt(order)){
            case 0: return mStudyDb.getTerms(TermDatabase.TermSortOrder.ALPHABETIC);
            case 1: return mStudyDb.getTerms(TermDatabase.TermSortOrder.UPDATE_DESC);
            default: return mStudyDb.getTerms(TermDatabase.TermSortOrder.UPDATE_ASC);
        }
    } */

    @Override
    public void onTermEntered(String term){
        //rturns term enterd in the term dialog fragrment dialog
        if(term.length()> 0) {
            Term sub = new Term(term);
            if (mStudyDb.addTerm(sub)){
                mTermAdapter.addTerm(sub);
                Toast.makeText(this, "added " + term, Toast.LENGTH_SHORT).show();
            } else {
                String message = getResources().getString(R.string.term_exists, term);
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void addTermClick(View view) {
        // Go to Add Term Screen
        Intent intent = new Intent(TermActivity.this, AddTermActivity.class);
        startActivity(intent);

        /*
        FragmentManager manager = getSupportFragmentManager();
        TermDialogFragment dialog = new TermDialogFragment();
        dialog.show(manager, "termDialog"); */
    }



    /* private List<Term> loadTerms(){
        return mStudyDb.getTerms(StudyDatabase.TermSortOrder.UPDATE_DESC);
    } */

    private class TermHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private Term mTerm;
        private TextView mTextView;

        public TermHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.recycler_view_items, parent, false));
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            mTextView = itemView.findViewById(R.id.termTextView);

        }

        public void bind(Term term, int position){
            mTerm = term;
            mTextView.setText(term.getText());

            if(mSelectedTermPosition == position){
                //make selected term standout
                mTextView.setBackgroundColor(Color.RED);
            } else {

                // make background color dependent on the length of the term string
                int colorIndex = term.getText().length() % mTermColors.length;
                mTextView.setBackgroundColor(mTermColors[colorIndex]);
            }
        }

        @Override
        public boolean onLongClick(View view){
            if(mActionMode != null) {
                return false;
            }

            mSelectedTerm = mTerm;
            mSelectedTermPosition = getAdapterPosition();

            //re-bind the selcted item
            mTermAdapter.notifyItemChanged(mSelectedTermPosition);

            //Show the CAB
            mActionMode = TermActivity.this.startActionMode(mActionModeCallback);
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
                        mStudyDb.deleteTerm(mSelectedTerm);
                        mTermAdapter.removeTerm(mSelectedTerm);

                        //close the cab
                        mode.finish();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                mActionMode = null;

                //CAB closing, need to deslect item if not deleted
                mTermAdapter.notifyItemChanged(mSelectedTermPosition);
                mSelectedTermPosition = RecyclerView.NO_POSITION;
            }
        };

        @Override
        public void onClick(View view){
            // start question activity, indicating what term was clicked
            Intent intent = new Intent(TermActivity.this, CourseActivity.class);
            intent.putExtra(CourseActivity.EXTRA_TERM, mTerm.getText());
            startActivity(intent);
        }

    }

    private class TermAdapter extends RecyclerView.Adapter<TermHolder> {

        private List<Term> mTermList;

        public TermAdapter(List<Term> terms){
            mTermList = terms;
        }

        public void addTerm(Term term) {
            //add the new term at the beginnig of the list
            mTermList.add(0, term);

            //notify the adapter that the item was added to the beginning of the list
            notifyItemInserted(0);

            //scoll to the top
            mRecyclerView.scrollToPosition(0);
        }

        public void removeTerm(Term term){
            // find term in the list
            int index = mTermList.indexOf(term);
            if(index >=0){
                mTermList.remove(index);

                //notify adapter of term removal
                notifyItemRemoved(index);
            }
        }

        @Override
        public TermHolder onCreateViewHolder(ViewGroup parent, int viewType){
            LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
            return new TermHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(TermHolder holder, int position){
            holder.bind(mTermList.get(position), position);
        }

        @Override
        public int getItemCount(){
            return mTermList.size();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.term_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        //handle item slection
        switch(item.getItemId()){
            case R.id.settings:
                Intent intent = new Intent(TermActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}