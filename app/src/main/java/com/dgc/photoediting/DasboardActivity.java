package com.dgc.photoediting;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class DasboardActivity extends AppCompatActivity {
    CardView card_edit,card_effect,card_catgor,card_share;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        card_edit = (CardView)findViewById(R.id.card_edit);
        card_effect = (CardView)findViewById(R.id.card_effect);
        card_catgor = (CardView)findViewById(R.id.card_catgor);
        card_share = (CardView)findViewById(R.id.card_share);
        card_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(), NavigationDrawerActivity.class);
                startActivity(i);
            }
        });
        card_effect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(), NavigationDrawerActivity.class);
                startActivity(i);
            }
        });
        card_catgor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(), NavigationDrawerActivity.class);
                startActivity(i);
            }
        });
        card_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(), NavigationDrawerActivity.class);
                startActivity(i);
            }
        });
    }
}
