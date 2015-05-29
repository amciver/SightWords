package com.experiment.sightwords.listeners;

import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amciver on 1/24/15.
 */
public class SpeechRecognitionListener implements RecognitionListener{

    private final String TAG = "SpeechRecoListener";
    private List<SpeechRecognitionCompleteListener> mSpeechRecognitionCompleteListeners = new ArrayList();

        public void setOnSpeechRecognitionComplete(SpeechRecognitionCompleteListener listener){
            mSpeechRecognitionCompleteListeners.add(listener);
        }

        @Override
        public void onReadyForSpeech(Bundle params) {
            Log.d(TAG, "onReadyForSpeech called") ;

        }

        @Override
        public void onBeginningOfSpeech() {
            Log.d(TAG, "onBeginningOfSpeech called");
        }

        @Override
        public void onEndOfSpeech() {
            Log.d(TAG, "onEndOfSpeech called");
        }

        @Override
        public void onBufferReceived(byte[] buffer)
        {
            Log.d(TAG, "onBufferReceived called " + buffer + new String(new byte[] {0x63}));
        }

        @Override
        public void onError(int error) {
            Log.d(TAG, "onError called with error of " + String.valueOf(error)) ;

            //notify all com.experiment.sightwords.listeners that work is complete
            for (SpeechRecognitionCompleteListener listener : mSpeechRecognitionCompleteListeners) {
                listener.onSpeechRecognitionComplete("");
            }
        }

        @Override
        public void onEvent(int eventType, Bundle params) {

        }

        @Override
        public void onPartialResults(Bundle partialResults) {

        }

        @Override
        public void onResults(Bundle results)
        {
            Log.d(TAG, "onResults called");

            ArrayList data = results.getStringArrayList(android.speech.SpeechRecognizer.RESULTS_RECOGNITION);
            float[] confidence = results.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES);


            //get the result with the highest confidence which is the first one
            String result = "";
            if(data.size() >= 1){
               result = (String)data.get(0);
            }

            Log.d(TAG, "onResults result is [" + result +"] with a confidence of [" + confidence[0] + "]");

//            StringBuffer result = new StringBuffer();
//            for (int i = 0; i < data.size(); i++)
//            {
//                result.append((String)data.get(i));
//                Log.d(TAG, "onResults called " + (String)data.get(i));
//            }

            //notify all com.experiment.sightwords.listeners that work is complete
            for (SpeechRecognitionCompleteListener listener : mSpeechRecognitionCompleteListeners) {
                listener.onSpeechRecognitionComplete(result);
            }
        }

        @Override
        public void onRmsChanged(float rmsdB) {
        }
}
