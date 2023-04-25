package com.wgu.termtracker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

public class TermDialogFragment extends DialogFragment {

    //host activity must implement
    public interface OnTermEnteredListener {
        void onTermEntered(String term);
    }

    private OnTermEnteredListener mListener;

    @Override
    public AlertDialog onCreateDialog(Bundle savedInstanceState){

        final EditText termEditText = new EditText(getActivity());
        termEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        termEditText.setMaxLines(1);

        return new AlertDialog.Builder(getActivity()).setTitle(R.string.term).setView(termEditText).setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                String term = termEditText.getText().toString();
                mListener.onTermEntered(term.trim());
            }
        })
                .setNegativeButton(R.string.cancel, null).create();
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mListener = (OnTermEnteredListener) context;
    }

    public static class SettingsActivity {
    }
}
