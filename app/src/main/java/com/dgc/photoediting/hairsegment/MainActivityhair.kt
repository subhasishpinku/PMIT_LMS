package com.dgc.photoediting.hairsegment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import com.dgc.photoediting.R
import com.dgc.photoediting.masksegment.BackgroundChangeView
import com.dgc.photoediting.masksegment.ImageSegmentView
import com.dgc.photoediting.masksegment.MaskCameraView
import com.google.android.material.card.MaterialCardView


class MainActivityhair : AppCompatActivity() {

    val list_col = arrayOf<String>("Mask Segment Demo","Hair Color Change","Image Segment")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_hair)
        val cardmask: MaterialCardView = findViewById(R.id.card_mask)
        val cardhair: MaterialCardView = findViewById(R.id.card_hair)
        val cardimage: MaterialCardView = findViewById(R.id.card_image)
        cardmask.setOnClickListener(){
            startActivity(Intent(this, MaskCameraViewhair::class.java))
        }
        cardhair.setOnClickListener(){
            startActivity(Intent(this, HairColorChangeView::class.java))
        }
        cardimage.setOnClickListener(){
            startActivity(Intent(this, ImageSegmentViewhair::class.java))
        }
//        val listView:ListView = findViewById(R.id.mlistView)
//        val myListAdapter = TableListAdapterhair(this,list_col)
//        listView.adapter = myListAdapter
//
//        listView.setOnItemClickListener(){adapterView, view, position, id ->
//            val itemAtPos = adapterView.getItemAtPosition(position)
//            val itemIdAtPos = adapterView.getItemIdAtPosition(position)
//            if(itemIdAtPos.toInt() == 0) {
//                startActivity(Intent(this, MaskCameraViewhair::class.java))
//            }
//            if(itemIdAtPos.toInt() == 1) {
//                startActivity(Intent(this, HairColorChangeView::class.java))
//            }
//            if(itemIdAtPos.toInt() == 2) {
//                startActivity(Intent(this, ImageSegmentViewhair::class.java))
//            }
//
//        }
   }
}
