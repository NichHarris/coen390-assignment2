// COEN 390 - Assignment 2
// Nicholas Harris - 40111093
// harris.nicholas1998@gmail.com

package com.example.coen390_assignment2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    // Initialize variables
    protected TextView userCount;
    protected Button addUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userCount = (TextView) findViewById(R.id.num_profiles);
        addUser = (Button) findViewById(R.id.add_user);
        addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogHelper dialog = new DialogHelper();
                dialog.show(getFragmentManager(), "Helper");
            }
        });


    }

    // Display options menu in task-bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.taskbarmenu, menu);
        return true;
    }

    // Create the action when an option on the task-bar is selected
    @Override
    public  boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.display_mode) {
            // some event
        }
        return super.onOptionsItemSelected(item);
    }
}