package org.tensorflow.lite.examples.posenet.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_gif_view.*
import org.tensorflow.lite.examples.posenet.R

class GifViewActivity : AppCompatActivity() {
    var TAG = "GifViewActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gif_view)
        val intent = intent
        val id = intent.getIntExtra("ID", 0)
        if (id == 0)
            Toast.makeText(
                applicationContext,
                "Не удалось загрузить анимаци. Загружена тестовая",
                Toast.LENGTH_SHORT
            ).show()
        Log.d(TAG, "onCreate: " + "http://192.168.1.3:5000/imgs/" + id + ".gif")
        webView.loadUrl("http://192.168.1.3:5000/imgs/" + id + ".gif")
    }
}