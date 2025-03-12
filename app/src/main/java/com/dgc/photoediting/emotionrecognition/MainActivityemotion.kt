package com.dgc.photoediting.emotionrecognition

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import com.dgc.photoediting.R
import com.dgc.photoediting.masksegment.BackgroundChangeView
import com.dgc.photoediting.masksegment.ImageSegmentView
import com.dgc.photoediting.masksegment.MaskCameraView
import com.google.android.material.card.MaterialCardView


class MainActivityemotion : AppCompatActivity() {

    val list_col = arrayOf<String>("Real time Emotion","Without Face Detect","Emotion From Image")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_emotion)
        val cardrealtime: MaterialCardView = findViewById(R.id.card_realtime)
        val cardfacedetect: MaterialCardView = findViewById(R.id.card_facedetect)
        val cardemotionimage: MaterialCardView = findViewById(R.id.card_emotionimage)
        cardrealtime.setOnClickListener(){
            startActivity(Intent(this, MaskCameraView::class.java))
        }
        cardfacedetect.setOnClickListener(){
            startActivity(Intent(this, WithoutFaceDetectView::class.java))
        }
        cardemotionimage.setOnClickListener(){
            startActivity(Intent(this, EmotionImageView::class.java))
        }

//        val listView:ListView = findViewById(R.id.mlistView)
//        val myListAdapter = TableListAdapteremotin(this,list_col)
//        listView.adapter = myListAdapter
//
//        listView.setOnItemClickListener(){adapterView, view, position, id ->
//            val itemAtPos = adapterView.getItemAtPosition(position)
//            val itemIdAtPos = adapterView.getItemIdAtPosition(position)
//            if(itemIdAtPos.toInt() == 0) {
//                startActivity(Intent(this, MaskCameraView::class.java))
//            }
//            if(itemIdAtPos.toInt() == 1) {
//                startActivity(Intent(this, WithoutFaceDetectView::class.java))
//            }
//            if(itemIdAtPos.toInt() == 2) {
//                startActivity(Intent(this, EmotionImageView::class.java))
//            }
//
//        }
    }
}
