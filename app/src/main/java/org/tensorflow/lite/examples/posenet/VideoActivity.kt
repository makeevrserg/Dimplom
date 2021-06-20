/*
 * Copyright 2019 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.tensorflow.lite.examples.posenet

import android.content.Context
import android.database.Cursor
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_main.*
import org.jcodec.api.FrameGrab
import org.jcodec.common.AndroidUtil
import org.jcodec.common.io.NIOUtils
import org.jcodec.common.model.Picture
import org.tensorflow.lite.examples.posenet.lib.KeyPoint
import org.tensorflow.lite.examples.posenet.lib.Posenet
import org.tensorflow.lite.examples.posenet.main.POST
import java.io.File
import java.net.URI


class VideoActivity : AppCompatActivity() {
    /** Returns a resized bitmap of the drawable image.    */
    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        val bitmap = Bitmap.createBitmap(257, 257, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        drawable.setBounds(0, 0, canvas.width, canvas.height)

        drawable.draw(canvas)
        return bitmap
    }

    override fun onPause() {
        if (threadVideo != null) {
            threadAlive = false
            threadVideo!!.interrupt()
            threadVideo!!.stop()
        }

        super.onPause()
    }

    var threadVideo: Thread? = null
    var threadAlive = false


    /** Calls the Posenet library functions.    */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tfe_pn_activity_test)
        val sampleImageView = findViewById<ImageView>(R.id.image)
        var mBmp: Bitmap? = null


        val path = intent.extras?.get("Video") as String?
        val uriVideo: Uri = Uri.parse(path)

        var jsonPoints: JsonPoints = JsonPoints()

        Log.d(TAG, uriVideo.path!!.substring(4))
        val file = File(uriVideo.path!!.substring(4))

        val grab = FrameGrab.createFrameGrab(NIOUtils.readableChannel(file))
        var picture: Picture? = grab.nativeFrame

        Log.d(TAG, "onCreate: ")
        threadAlive = true


        var i = -1;
        Thread(Runnable {
            while (picture != null) {
                i++
                var frame = i
                mBmp = AndroidUtil.toBitmap(picture)
                if (!threadAlive)
                    return@Runnable
                var imageBitmap = drawableToBitmap(BitmapDrawable(resources, mBmp))

                var matrix: Matrix = Matrix();
                //
                matrix.postRotate(90F);

                imageBitmap = Bitmap.createBitmap(
                    imageBitmap,
                    0,
                    0,
                    imageBitmap.getWidth(),
                    imageBitmap.getHeight(),
                    matrix,
                    true
                )


                val mutableBitmap = imageBitmap.copy(Bitmap.Config.ARGB_8888, true)
                runOnUiThread {
                    sampleImageView.adjustViewBounds = true
                    sampleImageView.setImageBitmap(mutableBitmap)
                }

                val posenet = Posenet(this.applicationContext)
                val person = posenet.estimateSinglePose(imageBitmap)

                for (keypoint in person.keyPoints) {
                    synchronized(this) {
                        jsonPoints.add(keypoint, frame)
                        //Log.d(TAG, mainJsonAnims.toString())
                    }
                }
                picture = grab.nativeFrame
            }
            val pst: POST = POST(
                (applicationContext.getSharedPreferences("SharedPrefs", MODE_PRIVATE)
                    ?.getString("TOKEN", "") as String)
            )
            pst.CreateAnim(jsonPoints.getJson())
            runOnUiThread { Toast.makeText(applicationContext, "Успешно создано!", Toast.LENGTH_SHORT).show() }

        }).start()

    }


    // threadVideo?.start()


    var TAG = "VideoActivity"
}
