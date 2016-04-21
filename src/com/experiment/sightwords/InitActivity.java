package com.experiment.sightwords;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import com.experiment.sightwords.activities.PlayLevelActivity;
import com.experiment.sightwords.adapters.LevelsAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.experiment.sightwords.database.SightWordsSQLiteHelper;
import com.experiment.sightwords.util.Constants;

/**
 * Created by amciver on 1/12/15.
 */
public class InitActivity extends Activity {

    private final String TAG = "InitActivity";

    private final String GLOBAL_PREFS = "com.experiment.sightwords.prefs.global";
    private final String PREFS_SETUP = "prefsSetup";
    private final String PREFS_WORDS_SETUP = "wordsSetup";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.root);

        Log.d(TAG, "onCreate called");

        setupPreferences(false);
        setupDatabase(true);

        final ListView levels = (ListView) findViewById(R.id.list_levels);
        String[] values = new String[] { "Kindergarten", "1st grade", "2nd grade",
                "3rd grade", "4th grade", "5th grade" };

        final ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < values.length; ++i) {
            list.add(values[i]);
        }

        final LevelsAdapter adapter = new LevelsAdapter(this, android.R.layout.simple_list_item_1, list);
        levels.setAdapter(adapter);
        levels.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent playLevel = new Intent(getApplicationContext(), PlayLevelActivity.class);
                playLevel.putExtra(Constants.LEVEL, position);
                startActivity(playLevel);
            }
        });

    }

    private void setupDatabase(boolean reset){

        SharedPreferences settings = getSharedPreferences(GLOBAL_PREFS, MODE_PRIVATE);
        Log.d(TAG, "setupDatabase called with reset set as [" + reset + "]");
        Log.d(TAG, "setupDatabase called with " + PREFS_WORDS_SETUP + " set as [" + String.valueOf(settings.getBoolean(PREFS_WORDS_SETUP, false)) + "]");

        if(reset) {
            Log.d(TAG, "setupDatabase called with reset");
            setupWords(true);

            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(PREFS_WORDS_SETUP, true);
            editor.apply();
        }
        else if(!settings.getBoolean(PREFS_WORDS_SETUP, false)){

            setupWords(false);

            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(PREFS_WORDS_SETUP, true);
            editor.apply();
        }
    }

    private void setupPreferences(boolean reset){

        SharedPreferences settings = getSharedPreferences(GLOBAL_PREFS, MODE_PRIVATE);
        if(reset) {
            Log.d(TAG, "setupPreferences called with reset");
            SharedPreferences.Editor editor = settings.edit();
            editor.clear();
            editor.putBoolean(PREFS_SETUP, true);
            editor.putBoolean(PREFS_WORDS_SETUP, false);
            editor.apply();
        }
        else if(!settings.getBoolean(PREFS_SETUP, false)) {
            Log.d(TAG, "setupPreferences called with " + PREFS_SETUP + " set as " + String.valueOf(settings.getBoolean(PREFS_SETUP, false)));
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(PREFS_SETUP, true);
            editor.putBoolean(PREFS_WORDS_SETUP, false);
            editor.apply();
        }
    }

    private void setupWords(boolean reset) {
        //setup the database with the words
        SightWordsSQLiteHelper helper = new SightWordsSQLiteHelper(this.getApplicationContext());
        if(reset)
            helper.reset();
        setupKindergartenGrade(helper);
        setup1stGrade(helper);
        setup2ndGrade(helper);
    }

    private void setupKindergartenGrade(SightWordsSQLiteHelper helper){
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open("kindergarten_grade.txt")));
            List<String> words = new ArrayList<String>();
            String line;
            while ((line = reader.readLine()) != null) {
                words.add(line.trim());
            }

            helper.addWords("0", words);
        }
        catch (IOException e) {
            Log.e(TAG, "Error setting up Kindergarten grade words" + e);
        }
    }

    private void setup1stGrade(SightWordsSQLiteHelper helper){
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open("first_grade.txt")));
            List<String> words = new ArrayList<String>();
            String line;
            while ((line = reader.readLine()) != null) {
                words.add(line.trim());
            }

            helper.addWords("1", words);
        }
        catch (IOException e) {
              Log.e(TAG, "Error setting up 1st grade words" + e);
        }
    }

    private void setup2ndGrade(SightWordsSQLiteHelper helper){
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open("second_grade.txt")));
            List<String> words = new ArrayList<String>();
            String line;
            while ((line = reader.readLine()) != null) {
                words.add(line.trim());
            }

            helper.addWords("2", words);
        }
        catch (IOException e) {
            Log.e(TAG, "Error setting up 2nd grade words" + e);
        }
    }

}