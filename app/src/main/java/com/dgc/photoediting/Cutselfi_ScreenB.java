package com.dgc.photoediting;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class Cutselfi_ScreenB extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_b);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                final Intent i=new Intent(getApplicationContext(), Cutselfi_ScreenC.class);
                startActivity(i);
                finish();
            }
        }, 2000);
    }
}