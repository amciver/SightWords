package com.experiment.sightwords.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.experiment.sightwords.InitActivity;
import com.experiment.sightwords.R;
import com.experiment.sightwords.database.SightWordsSQLiteHelper;
import com.experiment.sightwords.listeners.SpeechRecognitionCompleteListener;
import com.experiment.sightwords.listeners.SpeechRecognitionListener;
import com.experiment.sightwords.util.Constants;

/**
 * Created by amciver on 1/24/15.
 */
public class PlayLevelActivity extends Activity {

    private final String TAG = "PlayLevelActivity";

    private String ACTIVE_WORD = "";
    private final int LEVEL_COUNT = 10;
    private int mLevelCount = LEVEL_COUNT;

    private AlertDialog mSightWordsDialog = null;
    private TimerTask mTimer = new TimerTask();

    private boolean LISTENING = false;

    private SpeechRecognizer mSpeechRecognizer;
    private SpeechRecognitionListener mSpeechRecognitionListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_level);

        //obtain level to play
        int level = this.getIntent().getExtras().getInt(Constants.LEVEL);

        Log.d(TAG, "onCreate called w/level [" + level + "]");

        setUpSpeechRecognition();
        setUpDialog();

        mSightWordsDialog.show();
    }

    private void setUpDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_start)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        setActiveWord();
                        startTimer();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });

        mSightWordsDialog = builder.create();
    }

    private void setUpSpeechRecognition() {

        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognitionListener = new SpeechRecognitionListener();
        mSpeechRecognitionListener.setOnSpeechRecognitionComplete(new SpeechRecognitionCompleteListener() {
            @Override
            public void onSpeechRecognitionComplete(String recognition) {

                LISTENING = false;

                onSaidIt(ACTIVE_WORD, recognition);

                Log.d(TAG, "onSpeechRecognitionComplete called with [" + recognition + "]");
            }
        });
        mSpeechRecognizer.setRecognitionListener(mSpeechRecognitionListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mSpeechRecognizer != null) {
            mSpeechRecognizer.stopListening();
            mSpeechRecognizer.cancel();
            mSpeechRecognizer.destroy();
            mSpeechRecognizer = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void onSayIt(View button){

        if(!LISTENING) {

            LISTENING = true;

            Intent recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

            mSpeechRecognizer.startListening(recognizerIntent);
        }
    }

    public void onSkipIt(View button){

        if(LISTENING) {
            mSpeechRecognizer.cancel();
        }

        LISTENING = false;

        modifySkips(false);
        setActiveWord();
    }

    private void onSaidIt(String expected, String actual) {

        Log.d(TAG, "onSaidIt called, received [" + actual + "] and expected [" + expected + "]");

        if ((expected.compareToIgnoreCase(actual) == 0) ||
                actual.contains((CharSequence) expected)) {
            Toast.makeText(getApplicationContext(), "Success!",
                    Toast.LENGTH_SHORT).show();
            modifyScore(true);
            setActiveWord();
        } else {
            Toast.makeText(getApplicationContext(), "Please try again!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void modifySkips(boolean add) {

        if(add) {
        //when we allow the user to when adds fill it in
        } else {
            TextView textSkips = (TextView)findViewById(R.id.text_total_skipped);
            int intSkipped = Integer.parseInt((String)textSkips.getText());
            intSkipped--;
            textSkips.setText(String.valueOf(intSkipped));

            //if no more skips exist, disable button
            if(intSkipped == 0) {
                Button btnSkip = (Button)findViewById(R.id.btn_skip_it);
                btnSkip.setEnabled(false);
            }
        }
    }

    private void modifyScore(boolean success) {
        TextView textScore = null;
        if(success) {
            textScore = (TextView)findViewById(R.id.text_total_correct);
            int intScore = Integer.parseInt((String)textScore.getText());
            intScore++;
            textScore.setText(String.valueOf(intScore));
        }
        else {
            textScore = (TextView)findViewById(R.id.text_total_incorrect);
            int intScore = Integer.parseInt((String)textScore.getText());
            intScore--;
            textScore.setText(String.valueOf(intScore));
        }
    }

    private void setActiveWord() {

        SightWordsSQLiteHelper helper = new SightWordsSQLiteHelper(this.getApplicationContext());

        //find another word and skip it if it's the one we just used
        String word = "";
        do {
            word = helper.getWord("1");
        } while (ACTIVE_WORD.compareToIgnoreCase(word) == 0);

        Log.d(TAG, "setActiveWord setting new word to [" + word + "]");
        ACTIVE_WORD = word;

        TextView wordToSay = (TextView) findViewById(R.id.text_word_to_say);
        wordToSay.setText(ACTIVE_WORD);
        wordToSay.startAnimation(AnimationUtils.loadAnimation(PlayLevelActivity.this, android.R.anim.slide_in_left));
    }

    private void startTimer() {
                mTimer.execute();
    }

    private class TimerTask extends AsyncTask<Void, Integer, Integer> {
        protected Integer doInBackground(Void... params) {

            int seconds = 0;
            while(seconds < 30){
                 try { Thread.sleep(1000); } catch (InterruptedException e){ }
                publishProgress(seconds++);
            }

            return seconds;
        }

        protected void onProgressUpdate(Integer... progress) {
            TextView time = (TextView) findViewById(R.id.text_time);
            time.setText(progress[0].toString());
        }

        protected void onPostExecute(Integer result) {

        }
    }
}