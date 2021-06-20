package org.tensorflow.lite.examples.posenet.main

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_anims.*
import org.json.JSONObject
import org.tensorflow.lite.examples.posenet.R


class AnimsActivity : AppCompatActivity() {
    var TAG = "AnimsActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anims)
        val pst: POST = POST(
                (applicationContext.getSharedPreferences("SharedPrefs", MODE_PRIVATE)?.getString(
                        "TOKEN",
                        ""
                ) as String)
        )
        Log.d(TAG, "onCreate: "+pst.GetJsonAnimsList())
        val result = pst.GetJsonAnimsList()
        if (result==null || result.isEmpty()){
            Toast.makeText(applicationContext, "Авторизуйтесь или создайте анимации", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        val json: JSONObject = JSONObject(pst.GetJsonAnimsList())
        Log.d(TAG, "onCreate: ${json.toString()}")
        val map: HashMap<Int, Long> = HashMap()
        for (i in 0..json.getJSONArray("anims").length() - 1) {
            map.put(
                    json.getJSONArray("anims").getJSONObject(i).getInt("id"),
                    json.getJSONArray("anims").getJSONObject(i).getLong("unix_time")
            )
        }
        if (map.size <= 0) {
            Toast.makeText(this, "Авторизуйтесь или создайте анимации", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = RecyclerAdapter(applicationContext, map)
        Log.d(TAG, "onCreate: ${map}")
        Log.d(TAG, "onCreate: " + pst.GetJsonAnimsList())


    }
}