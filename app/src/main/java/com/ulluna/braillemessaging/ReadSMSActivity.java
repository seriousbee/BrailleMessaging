package com.ulluna.braillemessaging;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import java.util.Locale;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * Activity, gdzie po wciśnięciu w tło zostaje użytkownikowi odczytana wiadomość SMS
 */
public class ReadSMSActivity extends Activity {
    private int mActivePointerId;
    GestureDetectorCompat mDetector;
    MediaPlayer mp;
    TextToSpeech tts3;
    String value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("com.ulluna.Braille", "I opened the activity");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_sms);

        Intent intent = getIntent();
        value = intent.getStringExtra("key");

        mp = MediaPlayer.create(getApplicationContext(), R.raw.startbeep);
        mp.start();

        tts3=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
            }
        }
        );//inicjacja tts
        tts3.setLanguage(Locale.ENGLISH);

        tts3.speak("I will read now your message." + value, TextToSpeech.QUEUE_FLUSH, null);
        mDetector = new GestureDetectorCompat(this, new ReadSMSActivity.MyGestureListener());
    }

    public boolean onTouchEvent(MotionEvent event){
        //listener czy użytkownik wcisnął w tło
        this.mDetector.onTouchEvent(event);
        final int actionPeformed = event.getAction();
        mActivePointerId = event.getPointerId(0);

        switch(actionPeformed){
            case MotionEvent.ACTION_DOWN:{
                tts3.speak("I will read now your message." + value, TextToSpeech.QUEUE_FLUSH, null);
                break;
            }
            case MotionEvent.ACTION_MOVE:{
                break;
            }

        }
        return true;
    }
    public class MyGestureListener extends GestureDetector.SimpleOnGestureListener {//potrzebne do onTouchEvent

        private static final int SWIPE_MIN_DISTANCE = 100;
        private static final int SWIPE_THRESHOLD_VELOCITY = 100;
        TextToSpeech tts2=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
            }
        }
        );

        @Override
        public boolean onDown(MotionEvent e) {
            //always return true since all gestures always begin with onDown and
            //if this returns false, the framework won't try to pick up onFling for example.
            //Potrzebne i ma zostać puste
            return true;
        }
    }
}
