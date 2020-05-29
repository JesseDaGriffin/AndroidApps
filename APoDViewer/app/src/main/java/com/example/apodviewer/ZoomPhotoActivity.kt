package com.example.apodviewer

import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.squareup.picasso.Picasso

class ZoomPhotoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zoom_photo)

        // Get the Intent that started this activity and extract the string
        val photo_url = intent.getStringExtra(EXTRA_MESSAGE)

        var image = findViewById<ImageView>(R.id.imageViewZoom)
        Picasso.get().load(photo_url).into(image)
    }
}
