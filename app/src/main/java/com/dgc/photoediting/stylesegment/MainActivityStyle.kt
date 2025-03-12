package com.dgc.photoediting.stylesegment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import com.dgc.photoediting.R
import com.dgc.photoediting.masksegment.BackgroundChangeView
import com.dgc.photoediting.masksegment.MaskCameraView
import com.google.android.material.card.MaterialCardView


class MainActivityStyle : AppCompatActivity() {

    val list_col = arrayOf<String>("Real time DeepStyle","Image DeepStyle")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_style)
        val carddeepstyle: MaterialCardView = findViewById(R.id.card_deepstyle)
        val carddeepimage: MaterialCardView = findViewById(R.id.card_deepimage)
        carddeepstyle.setOnClickListener(){
            startActivity(Intent(this, RealTimeDeepStyleViewStyle::class.java))
        }

        carddeepimage.setOnClickListener(){
            startActivity(Intent(this, ImageSegmentViewStyle::class.java))
        }
//        val listView:ListView = findViewById(R.id.mlistView)
//        val myListAdapter = TableListStyleAdapter(this,list_col)
//        listView.adapter = myListAdapter
//
//        listView.setOnItemClickListener(){adapterView, view, position, id ->
//            val itemAtPos = adapterView.getItemAtPosition(position)
//            val itemIdAtPos = adapterView.getItemIdAtPosition(position)
//
//            if(itemIdAtPos.toInt() == 0) {
//                startActivity(Intent(this, RealTimeDeepStyleViewStyle::class.java))
//            }
//            if(itemIdAtPos.toInt() == 1) {
//                startActivity(Intent(this, ImageSegmentViewStyle::class.java))
//            }
//
//        }
    }
}
