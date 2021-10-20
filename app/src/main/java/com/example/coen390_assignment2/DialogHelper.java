package com.example.coen390_assignment2;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.coen390_assignment2.Database.Config;
import com.example.coen390_assignment2.Database.DatabaseHelper;

import java.text.DecimalFormat;

public class DialogHelper extends DialogFragment {
    private static final String TAG = "Helper";

    public interface OnInputListener{
        void sendInput(String input);
    }
    public OnInputListener onInputListener;

    // Initialize variables
    protected EditText surname, name, id, gpa;
    protected Button cancel, save;
    protected DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_profile, container, false);

        dbHelper = new DatabaseHelper(getActivity(), Config.DATABASE_NAME, null, Config.DATABASE_VERSION);

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
                String setName = name.getText().toString();
                String setSurname = surname.getText().toString();
                String strId = id.getText().toString();
                String strGpa = gpa.getText().toString();
                String message = "Save Successful";

                if(setName.equals("") || setSurname.equals("") || strId.equals("") || strGpa.equals(""))
                    message = "Wrong or missing attributes. Nothing will be saved, staying in add profile mode";
                else {
                    int setId = Integer.parseInt(strId);
                    double setGpa = Double.parseDouble(strGpa);
                    String formatGpa = new DecimalFormat("#0.00").format(setGpa);
                    setGpa = Double.parseDouble(formatGpa);

                    if (setGpa > 4.3 || setGpa < 0) {
                        message = "Cannot add profile, GPA is incorrect";
                    } else if (setId < 1000000 || setId > 99999999) {
                        message = "Cannot add profile, ID is invalid";
                    } else {
                        if (dbHelper.insertProfile(setName, setSurname, setId, setGpa)) {
                            // update main activity page with new entry
                            ((MainActivity)getActivity()).updatePage();

                            // return to main activity
                            getDialog().dismiss();
                        } else {
                            message = "Cannot add profile, ID in already in use";
                        }
                    }
                }
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}
