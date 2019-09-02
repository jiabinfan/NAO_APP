package com.example.nao_control;

import android.content.Context;
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

public class read_book extends AsyncTask<String, Void, String> {
    private TextToSpeech mTTS;
    private Context mainActivity;
    private int this_stop = 0;
    private String sentences[];
    private int book_index = 0;

    @Override
    protected String doInBackground(String... params) {

        mTTS = new TextToSpeech(mainActivity, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i != TextToSpeech.ERROR) {
                    mTTS.setLanguage(Locale.US);
                    if (i == TextToSpeech.LANG_MISSING_DATA || i == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.d("TTS", "This Language is not supported");
                    }
                } else {
                    Log.d("TTS", "Initilization Failed!");
                }

                mTTS.speak("read book", TextToSpeech.QUEUE_FLUSH, null);
            }
        });
        for (int i = 0; i < sentences.length; i++) {
            if (!receive_stop()){
                mTTS.speak(sentences[i], TextToSpeech.QUEUE_FLUSH, null);
            } else {
                book_index = i;
                break;
            }

        }
        return " ";
    }

    public void set_context(Context mainAct) {
        mainActivity = mainAct;
    }

    public void set_stop(int stop) {

        this_stop = 1;
    }

    public void set_sentence(String[] book) {

        sentences = book;
    }
    public boolean receive_stop(){
        if (mood_part.speech_input.contains("stop")){
            return true;
        }
        else{
            return false;
        }
    }
    public int get_index(){
        return book_index;
    }
}
