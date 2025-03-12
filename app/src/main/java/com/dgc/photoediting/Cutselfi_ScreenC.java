package com.dgc.photoediting;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class Cutselfi_ScreenC extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_c);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                final Intent i=new Intent(getApplicationContext(), RegistrationActivity.class);
                startActivity(i);
                finish();
            }
        }, 2000);
    }
}