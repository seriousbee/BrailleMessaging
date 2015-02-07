package com.ulluna.braillemessaging;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.v4.view.GestureDetectorCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends Activity {

    public MediaPlayer mp;
    GestureDetectorCompat mDetector;
    public TextToSpeech tts;
    String[] litery = {"", "A", "", "B", "", "K", "", "L", "", "C", "I", "F", "", "M", "S", "P", "", "E", "", "H", "", "O", "", "R", "", "D", "J", "G", "", "N", "T", "Q", "", "Ą", "", "Ł", "", "U", "", "V", "", "Ć", "Ś", "", "Ó", "X", "Ź", "Ż", "", "Ę", "", "", "", "Z", "", "", "", "Ń", "W", "", "", "Y", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};
    String[] liczby = {"", "1", "", "2", "", "K", "", "L", "", "3", "9", "6", "", "M", "S", "P", "", "5", "", "8", "", "O", "", "R", "", "4", "0", "7", "", "N", "T", "Q", "", "Ą", "", "Ł", "", "U", "", "V", "", "Ć", "Ś", "", "Ó", "X", "Ź", "Ż", "", "Ę", "", "", "", "Z", "", "", "", "Ń", "W", "", "", "Y", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};
    int[] bests = new int[70];
    int screenWidth;
    int screenHeight;
    float[][] pointsArr = new float[10][2];
    private TextView textview, textview2, textview3;
    private int mActivePointerId;
    private String DEBUG_TAG = "Brille1";
    int pressedValue, counter=0;
    boolean isNumber = false, phoneNumber=false;
    public String message = "";
    public String numberPhone = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        tts=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
            }
        }
        );
        tts.setLanguage(Locale.getDefault());

        super.onCreate(savedInstanceState);

        Log.d(DEBUG_TAG, "I made it");
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        ActionBar bar = getActionBar();
        bar.hide();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);// ukrycie status bar (powiadomienia, zegar itp)

        WindowManager w = getWindowManager();

        Point size = new Point();
        w.getDefaultDisplay().getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;

        mDetector = new GestureDetectorCompat(this, new MyGestureListener());//rejestracja listnera gestów
        mp = MediaPlayer.create(getApplicationContext(), R.raw.startbeep);
        mp.start();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event){ //co się ma stać po wciśnięciu w ekran
        this.mDetector.onTouchEvent(event);
        pressedValue=0;
        final int actionPeformed = event.getAction();
        mActivePointerId = event.getPointerId(0);
        textview = (TextView)findViewById(R.id.textView1);
        textview2 = (TextView)findViewById(R.id.textView);
        textview3 = (TextView)findViewById(R.id.textView2);


        Log.d(DEBUG_TAG, event.getPointerCount()+"");

        printSamples(event);

        for(int i=5; i>=0; i--){ //sprawdzenie w które części ekranu/kropki wciska użytkownik i zsumowanie wartości wciśniętej przez użytkownika. Każdy obszar odpowiada kolejnemu rzędowi wielkości z systemu binarnego. Pozostały obszar(środek ekranu to 64)
            for (int k=0; k<event.getPointerCount(); k++){
                if(pointsArr[k][0]>=screenWidth-(screenHeight/3)*1.5 && pointsArr[k][1]>=screenHeight-screenHeight/3){
                    pressedValue+=32;
                }
                else if(pointsArr[k][0]>=screenWidth-(screenHeight/3)*1.5 && pointsArr[k][1]<screenHeight-screenHeight/3 && pointsArr[k][1]>screenHeight/3){
                    pressedValue+=16;
                }
                else if(pointsArr[k][0]>=screenWidth-(screenHeight/3)*1.5 && pointsArr[k][1]<=screenHeight/3){
                    pressedValue+=8;
                }
                else if(pointsArr[k][0]<=(screenHeight/3)*1.5 && pointsArr[k][1]>=screenHeight-screenHeight/3){
                    pressedValue+=4;
                }
                else if(pointsArr[k][0]<=(screenHeight/3+50)*1.5 && pointsArr[k][1]<screenHeight-screenHeight/3 && pointsArr[k][1]>screenHeight/3){
                    pressedValue+=2;
                }
                else if(pointsArr[k][0]<=(screenHeight/3)*1.5 && pointsArr[k][1]<=screenHeight/3){
                    pressedValue+=1;
                }
                else{
                    pressedValue+=64;
                }
            }

        }
        pressedValue/=6;//z jakiegoś powodu wynik jest 6 razy większy od oczekiwanego

        textview.setText(String.valueOf(pressedValue)); //wyświetlenie wyniku
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (pressedValue>0 && pressedValue<65){
            v.vibrate(50);
            bests[pressedValue]++; //zliczanie, którą kombinację użytkownik wciskał najdłużej (działa dopuki użytkownik wciska w ekran)
        }

        switch(actionPeformed){
            case MotionEvent.ACTION_UP:{

                pressedValue=0;
                textview.setText(String.valueOf(pressedValue));
                int maxIndex = 0;
                for (int i = 1; i < bests.length; i++){//szukanie która wartość była najdłużej wciskana
                    int newnumber = bests[i];
                    if ((newnumber > bests[maxIndex])){
                        maxIndex = i;
                    }
                }
                for (int i =0; i< bests.length; i++){//resetowanie tablicy
                    bests[i]=0;
                }
                if (maxIndex==64){//jeżeli użytkownik nie wcisnął w żaden obszar/kropkę
                    break;
                }
                if (maxIndex==60){//znak liczby
                    isNumber=true;
                    textview2.setText("Number sign");
                    tts.speak("Numbers are on", TextToSpeech.QUEUE_FLUSH, null);
                    break;
                }
                if (!isNumber&& !phoneNumber){//ustawienie odpowiedniej litery/liczby
                    textview2.setText(litery[maxIndex]);
                }else {
                    textview2.setText(liczby[maxIndex]);
                    if (phoneNumber){//dodanie cyfry do numeru telefonu
                        numberPhone += textview2.getText().toString();
                        textview3.setText(numberPhone);
                    }
                    isNumber=false;
                }
                if (!phoneNumber){//dodanie znaku do wiadomości
                    message = message + textview2.getText().toString();
                    textview3.setText(message);
                }
                counter=0;
                tts.speak(textview2.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
                break;
            }
            case MotionEvent.ACTION_MOVE:{
                break;
            }

        }
        return true;
    }
    void printSamples(MotionEvent ev) {//zdobywanie współrzędnych wciśniętych punktów
        final int historySize = ev.getHistorySize();
        final int pointerCount = ev.getPointerCount();
        for (int h = 0; h < historySize; h++) {
            Log.d(DEBUG_TAG, "At time "+ ev.getHistoricalEventTime(h));
            for (int p = 0; p < pointerCount; p++) {
                pointsArr[p][0]=ev.getHistoricalX(p, h);
                pointsArr[p][1]=ev.getHistoricalY(p, h);
                Log.d(DEBUG_TAG, "pointer "+ ev.getPointerId(p)+ " ("+ev.getHistoricalX(p, h)+";  "+ev.getHistoricalY(p, h)+")");

            }
        }
    }


    public class MyGestureListener extends GestureDetector.SimpleOnGestureListener {//tworzenie listnera gestów

        private static final int SWIPE_MIN_DISTANCE = 120;
        private static final int SWIPE_THRESHOLD_VELOCITY = 120;
        TextToSpeech tts2=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
            }
        }
        );

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2,
                               float velocityX, float velocityY) {//listener swipe
            Context context;
            int duration = Toast.LENGTH_SHORT;
            Toast toast;

            if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE &&
                    Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                //From Right to Left
                if (textview3.getText().toString().length()>0){//usunięcie znaku z wiadomości lub numeru tel
                    tts2.speak("Backspace", TextToSpeech.QUEUE_FLUSH, null);
                    if (!phoneNumber){
                        message = removeLastChar(message);
                        textview3.setText(message);
                    }
                    else {
                        numberPhone = removeLastChar(numberPhone);
                        textview3.setText(numberPhone);
                    }
                }
                else{
                    tts2.speak("There is nothing more to remove.", TextToSpeech.QUEUE_FLUSH, null);

                }
                return true;

            }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE &&
                    Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {//dodanie spacji
                //From Left to Right
                if(!phoneNumber){
                    tts2.speak("Spacebar", TextToSpeech.QUEUE_FLUSH, null);
                    message = message + " ";
                    textview3.setText(message);
                }
                return true;

            }

            if(e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE &&
                    Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                //From Bottom to Top

                if (textview3.getText().toString().length()>0 &&!phoneNumber){//prośba o potwierdzenie wiadomości
                    if(counter==0){
                        tts2.speak(endWithPoint(message)+"Swipe up again to confirm", TextToSpeech.QUEUE_FLUSH, null);
                        //tts2.playSilence(50, TextToSpeech.QUEUE_FLUSH, null);
                        context = getApplicationContext();
                        toast = Toast.makeText(context, "Swipe up again to confirm", duration);
                        toast.show();
                        counter++;
                    }
                    else{//po potwierdzeniu prośba o podanie numeru
                        tts2.speak("Please give phone number", TextToSpeech.QUEUE_FLUSH, null);
                        phoneNumber=true;
                        textview3.setText("");
                        numberPhone="";
                        counter=0;
                        context = getApplicationContext();
                        toast = Toast.makeText(context, "Give phone number", duration);
                        toast.show();
                    }
                }
                if (phoneNumber && textview3.getText().toString().length()>0){//prośba o potwiedzenie wysłania SMS
                    if (counter==0){
                        tts2.speak(numberToRead(numberPhone) + "Swipe up again to send", TextToSpeech.QUEUE_FLUSH, null);
                        //tts2.playSilence(50, TextToSpeech.QUEUE_FLUSH, null);
                        counter++;
                        context = getApplicationContext();
                        toast = Toast.makeText(context, "Swipe up again to send", duration);
                        toast.show();
                    }
                    else{//wysłanie SMS
                        counter = 0;
                        sendSMS(numberPhone, message);
                        tts2.speak("Message sent", TextToSpeech.QUEUE_FLUSH, null);
                        phoneNumber=false;
                        numberPhone="";
                        message="";
                        context = getApplicationContext();
                        toast = Toast.makeText(context, "Message sent", duration);
                        toast.show();
                    }
                }
                return true;
            }  else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE &&
                    Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {//czytanie, co do tej pory zawiera wiadomość lub numer telefonu
                //From Top to Bottom
                if (!phoneNumber){
                    tts2.speak(message, TextToSpeech.QUEUE_FLUSH, null);
                }
                else{
                    tts2.speak(numberToRead(numberPhone), TextToSpeech.QUEUE_FLUSH, null);
                }
                return true;
            }
            return false;
        }
        @Override
        public boolean onDown(MotionEvent e) {
            //potrzebne np do onFling
            return true;
        }
    }
    public static String removeLastChar(String str) {
        return str.substring(0,str.length()-1);
    }
    private void sendSMS(String phoneNumber, String message)
    {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
    }
    private String numberToRead(String number){//dodanie spacji między cyfry, by tts nie czytał numeru tel jako 610 mln itd
        String x="";
        for(int i=0; i<number.length();i++){
            x+=number.charAt(i)+" ";
        }
        return x;
    }
    private String endWithPoint(String text){//dodanie kropki na końcu wiadomości
        if(text.charAt(text.length()-1)!='.'){
            return text + ".";
        }
        return text;
    }

}