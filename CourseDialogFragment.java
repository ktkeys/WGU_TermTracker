package com.wgu.termtracker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

import com.wgu.termtracker.R;

public class CourseDialogFragment extends DialogFragment {

    //host activity must implement
    public interface OnCourseEnteredListener {
        void onCourseEntered(String course);
    }

    private OnCourseEnteredListener mListener;

    @Override
    public AlertDialog onCreateDialog(Bundle savedInstanceState){

        final EditText courseEditText = new EditText(getActivity());
        courseEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        courseEditText.setMaxLines(1);

        return new AlertDialog.Builder(getActivity()).setTitle(R.string.course).setView(courseEditText).setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                String course = courseEditText.getText().toString();
                mListener.onCourseEntered(course.trim());
            }
        })
                .setNegativeButton(R.string.cancel, null).create();
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mListener = (OnCourseEnteredListener) context;
    }

    public static class SettingsActivity {
    }
}
