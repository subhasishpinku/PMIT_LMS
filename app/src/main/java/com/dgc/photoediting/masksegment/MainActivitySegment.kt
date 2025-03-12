package com.dgc.photoediting.masksegment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ListView
import com.dgc.photoediting.R
import com.google.android.material.card.MaterialCardView
import com.google.android.material.datepicker.MaterialCalendar


class MainActivitySegment : AppCompatActivity() {

//    val list_col = arrayOf<String>("Mask Segment Demo","Video Background","Image Segment")
     
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_sagment)
        val cardmask:MaterialCardView = findViewById(R.id.card_mask)
        val cardvideo:MaterialCardView = findViewById(R.id.card_video)
        val cardimage:MaterialCardView = findViewById(R.id.card_image)
//        val listView:ListView = findViewById(R.id.mlistView)
//        val myListAdapter = TableListAdapter(this,list_col)
//        listView.adapter = myListAdapter
//
//        listView.setOnItemClickListener(){adapterView, view, position, id ->
//            val itemAtPos = adapterView.getItemAtPosition(position)
//            val itemIdAtPos = adapterView.getItemIdAtPosition(position)
//            if(itemIdAtPos.toInt() == 0) {
//                startActivity(Intent(this, MaskCameraView::class.java))
//            }
//            if(itemIdAtPos.toInt() == 1) {
//                startActivity(Intent(this, BackgroundChangeView::class.java))
//            }
//            if(itemIdAtPos.toInt() == 2) {
//                startActivity(Intent(this, ImageSegmentView::class.java))
//            }
//
//        }
        cardmask.setOnClickListener(){
            startActivity(Intent(this, MaskCameraView::class.java))
        }

        cardvideo.setOnClickListener(){
            startActivity(Intent(this, BackgroundChangeView::class.java))
        }
        cardimage.setOnClickListener(){
            startActivity(Intent(this, ImageSegmentView::class.java))
        }

    }
}
