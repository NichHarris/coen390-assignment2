package com.example.coen390_assignment2.Models;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.coen390_assignment2.Controllers.Config;
import com.example.coen390_assignment2.Controllers.DatabaseHelper;
import com.example.coen390_assignment2.Views.MainActivity;
import com.example.coen390_assignment2.R;

import java.text.DecimalFormat;

public class DialogHelper extends DialogFragment {
    private static final String TAG = "Helper";

    public interface OnInputListener{
        void sendInput(String input);
    }
    public OnInputListener onInputListener;

    // Declare variables
    protected EditText surname, name, id, gpa;
    protected Button cancel, save;
    protected DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_profile, container, false);

        // Initialize Variables
        dbHelper = new DatabaseHelper(getActivity(), Config.DATABASE_NAME, null, Config.DATABASE_VERSION);

        // buttons
        cancel = (Button) view.findViewById(R.id.cancel_button);
        save = (Button) view.findViewById(R.id.save_button);

        // texts
        surname = (EditText) view.findViewById(R.id.student_surname);
        name = (EditText) view.findViewById(R.id.student_name);
        id = (EditText) view.findViewById(R.id.student_id);
        gpa = (EditText) view.findViewById(R.id.student_gpa);

        // cancel dialog, return to MainActivity
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        // Verify and save info, return to MainActivity
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
