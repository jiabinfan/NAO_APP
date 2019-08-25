
package com.example.nao_control;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.view.View;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button btn_mood,btn_non_mood;//先声明哦
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_mood = (Button) findViewById(R.id.mood);
        btn_non_mood = (Button) findViewById(R.id.non_mood);

        btn_mood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, mood_part.class);
                startActivity(intent);
            }
        });
        btn_non_mood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, non_mood_part.class);
                startActivity(intent);
            }
        });
    }
}


