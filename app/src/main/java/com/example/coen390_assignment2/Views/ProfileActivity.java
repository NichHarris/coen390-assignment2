// COEN 390 - Assignment 2
// Nicholas Harris - 40111093
// harris.nicholas1998@gmail.com

package com.example.coen390_assignment2.Views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coen390_assignment2.Controllers.Config;
import com.example.coen390_assignment2.Controllers.DatabaseHelper;
import com.example.coen390_assignment2.R;

public class ProfileActivity extends AppCompatActivity {

    // Declare variables
    protected DatabaseHelper dbHelper;
    protected int profileId;
    protected ListView activityList;
    protected TextView surname, name, id, gpa, date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Variables
        Bundle carryOver = getIntent().getExtras();
        dbHelper = new DatabaseHelper(this, Config.DATABASE_NAME, null, Config.DATABASE_VERSION);

        if (carryOver != null) {
            profileId = carryOver.getInt("id");
            displayProfileInfo(profileId);
        } else {
            profileId = -1;
            Toast.makeText(this, "Invalid id", Toast.LENGTH_SHORT).show();
            openMainActivity();
        }

        // Add task-bar
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    // Display options menu in task-bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.deletemenu, menu);
        return true;
    }

    // Create the action when an option on the task-bar is selected
    @Override
    public  boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.delete_profile) {
            dbHelper.dropProfile(profileId);
        }
        openMainActivity();
        return super.onOptionsItemSelected(item);
    }

    // Display the profile info for the current profile
    public void displayProfileInfo(int profileId) {
        surname = (TextView) findViewById(R.id.surname);
        name = (TextView) findViewById(R.id.name);
        id = (TextView) findViewById(R.id.id);
        gpa = (TextView) findViewById(R.id.gpa);
        date = (TextView) findViewById(R.id.create_date);

        String[] profileInfo = dbHelper.getProfile(profileId);
        name.setText(String.format("Name: %s", profileInfo[0]));
        surname.setText(String.format("Surname: %s", profileInfo[1]));
        id.setText(String.format("ID: %s", profileInfo[3]));
        gpa.setText(String.format("GPA: %s", profileInfo[4]));
        date.setText(String.format("Profile created: %s", profileInfo[2]));

        activityList = (ListView) findViewById(R.id.activity_list);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dbHelper.getAccessList(profileId));
        activityList.setAdapter(arrayAdapter);
    }

    // Return to main activity
    private void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    // Navigate back to homepage on task-bar return
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        dbHelper.closeProfile(profileId);
        return true;
    }
}