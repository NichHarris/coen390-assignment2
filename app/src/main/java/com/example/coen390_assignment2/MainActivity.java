// COEN 390 - Assignment 2
// Nicholas Harris - 40111093
// harris.nicholas1998@gmail.com

package com.example.coen390_assignment2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.coen390_assignment2.Database.Config;
import com.example.coen390_assignment2.Database.DatabaseHelper;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Initialize variables
    protected SharePreferenceHelper sharePreferenceHelper;
    protected DatabaseHelper dbHelper;
    protected TextView userCount;
    protected Button addUser;
    protected ListView userList;
    protected List<Integer> profileIds;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharePreferenceHelper = new SharePreferenceHelper(MainActivity.this);
        dbHelper = new DatabaseHelper(this, Config.DATABASE_NAME, null, Config.DATABASE_VERSION);
        userCount = (TextView) findViewById(R.id.num_profiles);
        userList = (ListView) findViewById(R.id.user_list);
        updatePage();

        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Object listItem = userList.getItemAtPosition(i);
                openUser(profileIds.get(i));
            }
        });

        addUser = (Button) findViewById(R.id.add_user);
        addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogHelper dialog = new DialogHelper();
                dialog.show(getFragmentManager(), "Helper");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePage();
    }

    // Display options menu in task-bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.taskbarmenu, menu);
        return true;
    }

    public void setUserList(List<String[]> users) {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, convertList(users, sharePreferenceHelper.getDisplayMode()));
        userList.setAdapter(arrayAdapter);
    }

    public void setUserCount(String mode, int count) {
        userCount.setText(String.format("%d Profiles, by %s", count, mode));
    }

    public List<String> convertList(List<String[]> iList, boolean mode) {
        List<String> returnList = new ArrayList<>();
        profileIds = new ArrayList<>();
        if (mode) {
            for (int i = 0; i < iList.size(); i++) {
                returnList.add(String.format("%d. %s, %s", i + 1, iList.get(i)[1], iList.get(i)[0]));
                profileIds.add(Integer.parseInt(iList.get(i)[3]));
            }
        } else {
            for (int i = 0; i < iList.size(); i++) {
                returnList.add(String.format("%d. %s", i + 1, iList.get(i)[3]));
                profileIds.add(Integer.parseInt(iList.get(i)[3]));
            }
        }
        return returnList;
    }

    // Create the action when an option on the task-bar is selected
    @Override
    public  boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.display_mode) {
            if (sharePreferenceHelper.getDisplayMode()) {
                sharePreferenceHelper.setDisplayMode(false);
                setUserList(dbHelper.getAllProfiles("profileId"));
                setUserCount("ID", dbHelper.getNumUsers());
            } else {
                sharePreferenceHelper.setDisplayMode(true);
                setUserList(dbHelper.getAllProfiles("surname"));
                setUserCount("Surname", dbHelper.getNumUsers());
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void openUser(int userId) {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("id", userId);
        startActivity(intent);
    }

    public void updatePage() {
        if (sharePreferenceHelper.getDisplayMode()) {
            setUserList(dbHelper.getAllProfiles("surname"));
            setUserCount("Surname", dbHelper.getNumUsers());
        } else {
            setUserList(dbHelper.getAllProfiles("profileId"));
            setUserCount("ID", dbHelper.getNumUsers());
        }
    }
}