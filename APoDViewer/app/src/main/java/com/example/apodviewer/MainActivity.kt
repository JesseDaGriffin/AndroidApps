package com.example.apodviewer
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import java.util.*

const val EXTRA_MESSAGE = "com.example.apodviewer.MESSAGE"

class MainActivity : AppCompatActivity() {
    var custom_date = ""
    val myCalendar = Calendar.getInstance()
    val year = myCalendar.get(Calendar.YEAR)
    val month = myCalendar.get(Calendar.MONTH)
    val day = myCalendar.get(Calendar.DAY_OF_MONTH)
    var photo_url = ""
    var video_url = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val desc = findViewById<TextView>(R.id.imageDescription)
        desc.movementMethod = ScrollingMovementMethod()

        val dateText = findViewById<TextView>(R.id.textViewDate)
        var convertMonth = (month + 1).toString()
        if(convertMonth.toInt() < 10)
            convertMonth = "0$convertMonth"
        dateText.text = "$year-$convertMonth-$day"
    }

    fun zoomPic(view: View) {
        if(photo_url != "") {
            val intent = Intent(this, ZoomPhotoActivity::class.java).apply {
                putExtra(EXTRA_MESSAGE, photo_url)
            }
            startActivity(intent)
        }

        if(video_url != "") {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(video_url)
                )
            )
        }

    }

    fun setDate(view: View) {
        val dateText = findViewById<TextView>(R.id.textViewDate)
        val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, yearPicked, monthPicked, dayPicked ->
            // Display Selected date in TextView
            var convertMonth = (monthPicked + 1).toString()
            if(convertMonth.toInt() < 10)
                convertMonth = "0$convertMonth"
            custom_date = "$yearPicked-$convertMonth-$dayPicked"
            dateText.text = custom_date
        }, year, month, day)
        dpd.show()
        print(custom_date)
    }

    fun fetchInfo(view: View) {
        val title = findViewById<TextView>(R.id.imageTitle)
        val copyright = findViewById<TextView>(R.id.imageCopyright)
        val desc = findViewById<TextView>(R.id.imageDescription)
        val image : ImageView = findViewById<ImageView>(R.id.imageView)

        title.text = "Fetching"

        var url = "https://api.nasa.gov/planetary/apod?api_key=DEMO_KEY"
        if(custom_date != "")
            url += "&date=$custom_date"

        val queue = Volley.newRequestQueue(this)

        val jsonRequest = JsonObjectRequest(Request.Method.GET, url, null,
                Response.Listener { response ->
                    if(response.has("title")) {
                        title.visibility = View.VISIBLE
                        title.text = response.getString("title")
                    }
                    else {
                        title.visibility = View.GONE
                    }

                    if(response.has("title")) {
                        desc.visibility = View.VISIBLE
                        desc.text = response.getString("explanation")
                    }
                    else {
                        desc.visibility = View.GONE
                    }

                    if(response.has("hdurl")) {
                        photo_url = response.getString("hdurl")
                        Picasso.get().load(response.getString("hdurl")).into(image)
                    }
                    else {
                        photo_url = ""
                        image.setImageResource(R.drawable.noimageavailableicon)
                    }

                    if(response.has("copyright")) {
                        copyright.visibility = View.VISIBLE
                        copyright.text = response.getString("copyright")
                    }
                    else {
                        copyright.visibility = View.GONE
                        copyright.text = ""
                    }
                    if(response.has("url") && response.getString("url").contains("youtube")) {
                        video_url = response.getString("url")
                        image.setImageResource(R.drawable.youtubelink)
                    }
                    else
                        video_url = ""
                },
                Response.ErrorListener { error ->
                    desc.visibility = View.VISIBLE
                    title.text = "ERROR"
                    image.setImageResource(R.drawable.noimageavailableicon)
                    copyright.text = "ERROR"

                    if(error.localizedMessage == null) {
                        when(error.networkResponse.statusCode) {
                            400 -> desc.text = "Error: ${error.networkResponse.statusCode}\nDate can't be in the future!"
                            429 -> desc.text = "Error: ${error.networkResponse.statusCode}\nYou have exceeded your rate limit. Try again later or contact us at https://api.nasa.gov:443/contact/ for assistance."
                        }
                    }
                    else
                        desc.text = "Error: ${error.localizedMessage}"
                })

        queue.add(jsonRequest)
    }
}
