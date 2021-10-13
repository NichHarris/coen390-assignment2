package com.example.coen390_assignment2;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;

public class DialogHelper extends DialogFragment {
    private static final String TAG = "Helper";

    public interface OnInputListener{
        void sendInput(String input);
    }
    public OnInputListener onInputListener;

    protected EditText surname, name, id, gpa;
    protected Button cancel, save;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_profile, container, false);
        cancel = (Button) view.findViewById(R.id.cancel_button);
        save = (Button) view.findViewById(R.id.save_button);

        surname = (EditText) view.findViewById(R.id.student_surname);
        name = (EditText) view.findViewById(R.id.student_name);
        id = (EditText) view.findViewById(R.id.student_id);
        gpa = (EditText) view.findViewById(R.id.student_gpa);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // save the data
                getDialog().dismiss();
            }
        });
        return view;
    }
}
