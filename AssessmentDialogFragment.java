package com.wgu.termtracker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

public class AssessmentDialogFragment extends DialogFragment {

    //host activity must implement
    public interface OnAssessmentEnteredListener {
        void onAssessmentEntered(String assessment);
    }

    private OnAssessmentEnteredListener mListener;

    @Override
    public AlertDialog onCreateDialog(Bundle savedInstanceState){

        final EditText assessmentEditText = new EditText(getActivity());
        assessmentEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        assessmentEditText.setMaxLines(1);

        return new AlertDialog.Builder(getActivity()).setTitle(R.string.assessment).setView(assessmentEditText).setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                String assessment = assessmentEditText.getText().toString();
                mListener.onAssessmentEntered(assessment.trim());
            }
        })
                .setNegativeButton(R.string.cancel, null).create();
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mListener = (OnAssessmentEnteredListener) context;
    }

    public static class SettingsActivity {
    }
}
