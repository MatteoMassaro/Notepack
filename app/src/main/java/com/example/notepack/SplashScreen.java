package com.example.notepack;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {

    //Variabili
    Handler handler;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //Rimozione dell'action bar
        getSupportActionBar().hide();

        //Inizializzazione variabili
        handler = new Handler();
        intent = new Intent(this, MainActivity.class);

        //Passaggio alla prossima activity
        handler.postDelayed(() -> startActivity(intent), 2000);
        handler.postDelayed(() -> this.finish(), 2000);
    }

}