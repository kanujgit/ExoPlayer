package com.example.andy.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Typewriter writer = new Typewriter(this);

        setContentView(writer);
        writer.setCharacterDelay(150);
        writer.animateText("Sample String...Sample String...Sample String...");

    }
}
