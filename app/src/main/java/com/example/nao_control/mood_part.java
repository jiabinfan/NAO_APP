package com.example.nao_control;

import android.content.Context;
import android.content.Intent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Locale;

public class mood_part extends AppCompatActivity {

    private Context contex;
    private TextToSpeech mTTS;
    private TextView txvResult;
    private TextView txvResult2;
    private TextView txvResult3;
    private String sever_ip = "";
    private String emotion = "none";
    receive_socket receive = new receive_socket();
    public static String speech_input="";
    public static final String EXTRA_MESSAGE = "com.example.Nao_control.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contex = getApplicationContext();
        setContentView(R.layout.activity_mood_part);
        txvResult = (TextView) findViewById(R.id.txvResult);
        txvResult2 = (TextView) findViewById(R.id.textView2);
        txvResult3 = (TextView) findViewById(R.id.textView3);
        receive.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i != TextToSpeech.ERROR) {
                    //mTTS.setLanguage(Locale.US);
                    if (i == TextToSpeech.LANG_MISSING_DATA || i == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.d("TTS", "This Language is not supported");
                    }
                } else {
                    Log.d("TTS", "Initilization Failed!");
                }


            }
        });
    }

    public void getSpeechInput(View view) {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());


        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 10);
        } else {
            Toast.makeText(this, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ArrayList<String> result;

        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK && data != null) {
                    result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    txvResult3.setText(result.get(0));
                    speech_input = result.get(0);
                    user_Sorket socket = new user_Sorket();
                    try {
                        JSONObject json_send = new JSONObject();
                        json_send.put("message",result.get(0));
                        json_send.put("emotion",emotion);
                        socket.setJson(json_send);
                        //socket.setIp(this.sever_ip);
                        socket.execute();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    public void Button_click(View v) {
        EditText editIp = (EditText) findViewById(R.id.editText);
        this.sever_ip = editIp.getText().toString();
        txvResult.setText(this.sever_ip);

        txvResult.setText(receive.get_json());

    }

    public void Button2_click(View v) {
        JSONObject jsonObject;
        Intent intent = null;

        String Thevalue = "";

        Intent intent_text = new Intent(this, text_present.class);
        Intent intent_speech = new Intent(this, testToSpeech.class);
        //Intent intent_voice = new Intent(this, text_to_voice.class);
        //Toast.makeText(this, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();

        try {

            String json = receive.get_json();
            jsonObject = new JSONObject(json);
            txvResult.setText(json);

            if (jsonObject.get("action").equals("url")){//jsonObject.names().get(0).toString().equals("url") && jsonObject.getString(jsonObject.names().get(0).toString()).substring(0, 4).equals("http")) {
                //Toast.makeText(this, "do not suuport", Toast.LENGTH_SHORT).show();

                Thevalue = jsonObject.getString(jsonObject.names().get(0).toString());
                Uri uri = Uri.parse(Thevalue);

                startActivity(new Intent(Intent.ACTION_VIEW, uri));


            } if (!jsonObject.get("response").equals("")){//jsonObject.names().get(2).toString().equals("speech") && !jsonObject.getString(jsonObject.names().get(2).toString()).equals("")) {
                //Toast.makeText(this, jsonObject.getString(jsonObject.names().get(2).toString()), Toast.LENGTH_SHORT).show();

                Thevalue = jsonObject.get("response").toString();
                intent_speech.putExtra(EXTRA_MESSAGE, Thevalue);
                startActivity(intent_speech);

            } if (jsonObject.get("action").equals("reminder")){//jsonObject.names().get(1).toString().equals("text") && !jsonObject.getString(jsonObject.names().get(1).toString()).equals("")) {

                Thevalue ="Anna say: "+ jsonObject.getString(jsonObject.names().get(1).toString());
                //intent_text.putExtra(EXTRA_MESSAGE, Thevalue);
                //txvResult2.setText(Thevalue);
                //startActivity(intent_text);

            } if (jsonObject.get("action").equals("set_calendar")){//jsonObject.names().get(4).toString().equals("set_calendar") && !jsonObject.getString(jsonObject.names().get(4).toString()).equals("")) {
                //String event = jsonObject.getString(jsonObject.names().get(4).toString());


                //String[] event_array = event.split("\\|");

                CalendarReminderUtils my_calendar = new CalendarReminderUtils();
                //Calendar cal = Calendar.getInstance();

                //long t_start = cal.getTime().getTime(); // Long.parseLong("String")
                //long t_end = cal.getTime().getTime()+10*60; //Long.parseLong("String")

                String t1 = jsonObject.get("start_time").toString();
                String t2 = jsonObject.get("end_time").toString();
                String d1 = jsonObject.get("start_date").toString();
                String d2 = jsonObject.get("end_date").toString();


                //long t_start = ((Number) jsonObject.get("start_date")).longValue();//.toString();
                //long t_end = ((Number) jsonObject.get("end_date")).longValue();//.toString();

                long t_start = 1564581600; //Long.parseLong(t1);
                long t_end = 1564581600; //Long.parseLong(t2);

                String title = jsonObject.get("title").toString(); //
                String description = "du zi mo ping lan";
                my_calendar.addCalendarEvent(this, title, description, d1,d2,t1,t2,t_start+10*60, 1);

                //CalendarContentResolver my2_calender = CalendarContentResolver(contex);

                //saveCalender(v);
                //get_cal_event();
            }

            if (jsonObject.get("action").equals("get_calendar_time")){//jsonObject.names().get(3).toString().equals("get_calendar") && !jsonObject.getString(jsonObject.names().get(3).toString()).equals("")) {

                Thevalue = jsonObject.getString(jsonObject.names().get(3).toString());

                calendar my2_cal = new calendar();
                String t1 = jsonObject.get("start_time").toString();
                String t2 = jsonObject.get("end_time").toString();
                String d1 = jsonObject.get("start_date").toString();
                String d2 = jsonObject.get("end_date").toString();



                JSONArray json_event = my2_cal.getcalendar(this, d1, d2, t1, t2);

                //long t_start = ((Number) jsonObject.get("start_date")).longValue();//.toString();
                //long t_end = ((Number) jsonObject.get("end_date")).longValue();//.toString();

                //user_Sorket socket2 = new user_Sorket();
                //socket2.setJsonArray(json_event);
                //socket.setIp(this.sever_ip);
                //socket2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                String json_message = "1";

                for (int i = 0; i < json_event.length(); i++) {
                    JSONObject o = json_event.getJSONObject(i);
                    String o_title = o.get("eventTitle").toString();
                    String o_start = o.get("startTime").toString();
                    json_message = json_message+ "You have "+o_title+" at "+o_start+".";
                }
                intent_speech.putExtra(EXTRA_MESSAGE, json_message);
                startActivity(intent_speech);

                txvResult2.setText(json_event.toString());

            }
            if (jsonObject.get("action").equals("get_calendar_keyword")){//jsonObject.names().get(3).toString().equals("get_calendar") && !jsonObject.getString(jsonObject.names().get(3).toString()).equals("")) {

                Thevalue = jsonObject.getString(jsonObject.names().get(3).toString());

                calendar my2_cal = new calendar();

                String title = jsonObject.get("title").toString();
                String description = jsonObject.get("description").toString();

                JSONArray json_event = my2_cal.getcalendar_intent(this,title,description);

                /* send json to server by socket connection
                user_Sorket socket2 = new user_Sorket();
                socket2.setJsonArray(json_event);
                //socket.setIp(this.sever_ip);
                socket2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);*/

                String json_message = "";

                for (int i = 0; i < json_event.length(); i++) {
                    JSONObject o = json_event.getJSONObject(i);
                    String o_title = o.get("eventTitle").toString();
                    String o_start = o.get("startTime").toString();
                    json_message = json_message+ "You have "+o_title+" at "+o_start+".";
                }
                txvResult2.setText(json_event.toString());
                intent_speech.putExtra(EXTRA_MESSAGE, json_message);
                startActivity(intent_speech);


            }
            if (jsonObject.get("action").equals("get_calendar_both")){//jsonObject.names().get(3).toString().equals("get_calendar") && !jsonObject.getString(jsonObject.names().get(3).toString()).equals("")) {

                Thevalue = jsonObject.getString(jsonObject.names().get(3).toString());

                calendar my2_cal = new calendar();

                long start_time=0;
                long end_time=0;
                String title = jsonObject.get("title").toString();
                String description = jsonObject.get("description").toString();

                JSONArray json_event = my2_cal.getcalendar_both(this,title,description,start_time,end_time);

                user_Sorket socket2 = new user_Sorket();
                socket2.setJsonArray(json_event);
                //socket.setIp(this.sever_ip);
                socket2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                txvResult2.setText(json_event.toString());

            }
            if (jsonObject.get("action").equals("read_book")){//.names().get(0).toString().equals("read_book") && !jsonObject.getString(jsonObject.names().get(5).toString()).equals("")){

                Thevalue = jsonObject.getString(jsonObject.names().get(5).toString());  // paragraph to be read



                mTTS.setLanguage(Locale.CANADA);
                read_books(Thevalue);

            }
            if (jsonObject.get("action").equals("stop")){//.names().get(6).toString().equals("stop") && !jsonObject.getString(jsonObject.names().get(5).toString()).equals("stop reading")){

                if (mTTS != null){
                    mTTS.stop();
                    mTTS.shutdown();
                }
                super.onDestroy();
                user_Sorket socket2 = new user_Sorket();
                socket2.setMessage("stop reading");
                socket2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void read_books(String book_text){

        //mTTS.speak(book_text, TextToSpeech.QUEUE_FLUSH, null);

        String book = read_text_file("Internal storage/Download/shumei.txt");
        String[] sentences = book.split("&");
        read_book read = new read_book();
        read.set_context(this);
        read.set_sentence(sentences);
        read.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        user_Sorket socket_book = new user_Sorket();
        int i = read.get_index();
        socket_book.setMessage(Integer.toString(read.get_index()));
        socket_book.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        txvResult2.setText(book);
    }

    public String read_text_file(String strFilePath){

        String content = "";
        File file = new File(strFilePath);
        if (file.isDirectory()){
            Log.d("TestFile", "The File doesn't not exist.");
        }
        else{
            try{
                InputStream instream = new FileInputStream(file);
                if (instream != null){
                    InputStreamReader inputreader = new InputStreamReader(instream);
                    BufferedReader buffreader = new BufferedReader(inputreader);
                    String line;
                    while ((line = buffreader.readLine())!=null){
                        content += line +"\n";
                    }
                    instream.close();
                }
            }
            catch (java.io.FileNotFoundException e){
                Log.d("TestFile", "The File doesn't not exist.");
            }
            catch (IOException e){
                Log.d("TestFile","e what the hell");
            }
        }

        content = "CHAPTER I. Down the Rabbit-Hole\n" +
                "&Alice was beginning to get very tired of sitting by her sister on the bank, &and of having nothing to do: once or twice she had peeped into the book her sister was reading, &but it had no pictures or conversations in it, ‘and what is the use of a book,’ thought Alice ‘without pictures or conversations?’\n" +
                "&So she was considering in her own mind (as well as she could, for the hot day made her feel very sleepy and stupid), &whether the pleasure of making a daisy-chain would be worth the trouble of getting up and picking the daisies, when suddenly a White Rabbit with pink eyes ran close by her.\n" +
                "&There was nothing so very remarkable in that; &nor did Alice think it so very much out of the way to hear the Rabbit say to itself, &‘Oh dear! Oh dear! I shall be late!’ (when she thought it over afterwards, it occurred to her that she ought to have wondered at this, but at the time it all seemed quite natural); but when the Rabbit actually took a watch out of its waistcoat-pocket, and looked at it, and then hurried on, Alice started to her feet, for it flashed across her mind that she had never before seen a rabbit with either a waistcoat-pocket, or a watch to take out of it, and burning with curiosity, she ran across the field after it, and fortunately was just in time to see it pop down a large rabbit-hole under the hedge.\n" +
                "&In another moment down went Alice after it,& never once considering how in the world she was to get out again.\n" +
                "&The rabbit-hole went straight on like a tunnel for some way, and then dipped suddenly down, &so suddenly that Alice had not a moment to think about stopping herself before she found herself falling down a very deep well.\n" +
                "&Either the well was very deep, or she fell very slowly, for she had plenty of time as she went down to look about her and to wonder what was going to happen next. $First, she tried to look down and make out what she was coming to, but it was too dark to see anything; then she looked at the sides of the well, and noticed that they were filled with cupboards and book-shelves; $here and there she saw maps and pictures hung upon pegs. She took down a jar from one of the shelves as she passed; it was labelled ‘ORANGE MARMALADE’, but to her great disappointment it was empty: she did not like to drop the jar for fear of killing somebody, so managed to put it into one of the cupboards as she fell past it.\n" +
                "&‘Well!’ thought Alice to herself, ‘after such a fall as this, I shall think nothing of tumbling down stairs! How brave they’ll all think me at home! $Why, I wouldn’t say anything about it, even if I fell off the top of the house!’ (Which was very likely true.)\n";
        return content;
    }

    public void saveCalender(View view) {
        Intent calendarIntent = new Intent(Intent.ACTION_INSERT, CalendarContract.Events.CONTENT_URI);
        Calendar calendar = Calendar.getInstance();
        calendar.set(2019, 8, 1, 7, 30);
        calendarIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, calendar.getTimeInMillis());
        calendar.set(2019, 9, 1, 10, 30);
        calendarIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, calendar.getTimeInMillis());
        calendarIntent.putExtra(CalendarContract.Events.TITLE, "上课");
        calendarIntent.putExtra(CalendarContract.Events.EVENT_LOCATION, "CCIS 1-140");
        calendarIntent.putExtra(CalendarContract.Events.DESCRIPTION, "Osmar's course, do not miss");
        calendarIntent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, CalendarContract.EXTRA_EVENT_ALL_DAY);
        startActivity(calendarIntent);
    }

    public void button_anger(View v){
        emotion = "anger";

    }
    public void button_fear(View v){
        emotion = "sad";

    }
    public void button_joy(View v){
        emotion = "joy";

    }
    public void button_love(View v){
        emotion = "love";

    }
    public void button_sadness(View v){
        emotion = "sadness";

    }
    public void button_suprise(View v){
        emotion = "suprise";

    }
    public void button_thankfulness(View v){
        emotion = "thankfulness";

    }
    public void button_disgust(View v){
        emotion = "disgust";

    }
    public void button_guilt(View v){
        emotion = "guilt";

    }

}

