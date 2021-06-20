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

import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.res.ResourcesCompat
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import java.lang.ClassCastException
import java.lang.NullPointerException
import java.nio.ByteBuffer
import org.tensorflow.lite.examples.posenet.lib.Posenet as Posenet

class TestActivity : AppCompatActivity() {
  /** Returns a resized bitmap of the drawable image.    */
  private fun drawableToBitmap(drawable: Drawable): Bitmap {
    val bitmap = Bitmap.createBitmap(257, 257, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
  }

  /** Calls the Posenet library functions.    */
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.tfe_pn_activity_test)
    val sampleImageView = findViewById<ImageView>(R.id.image)
    var mBmp: Bitmap? = null
    try {
      val bitmapArray = intent.extras?.get("BitmapImage") as ByteArray
      val bmp = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.size)
      mBmp = bmp
    } catch (e: ClassCastException) {
      try {
        val uriImage = Uri.parse(intent.extras?.get("BitmapImage") as String?)
        mBmp = MediaStore.Images.Media.getBitmap(this.contentResolver, uriImage)
      } catch (e: NullPointerException) {
        mBmp = drawableToBitmap(
          ResourcesCompat.getDrawable(
            resources,
            R.drawable.image,
            null
          ) as Drawable
        )
      }
    }


    //val drawedImage = ResourcesCompat.getDrawable(resources, R.drawable.image, null)

    val imageBitmap = drawableToBitmap(BitmapDrawable(resources, mBmp))
    sampleImageView.setImageBitmap(imageBitmap)
    val posenet = Posenet(this.applicationContext)
    val person = posenet.estimateSinglePose(imageBitmap)

    // Draw the keypoints over the image.
    val paint = Paint()
    paint.color = Color.RED
    val size = 2.0f

    val mutableBitmap = imageBitmap.copy(Bitmap.Config.ARGB_8888, true)
    val canvas = Canvas(mutableBitmap)

    var mainJsonAnims: JsonObject = JsonObject();
    val bonesMap: HashMap<String, JsonArray> = HashMap()
    for (keypoint in person.keyPoints) {

      var bodyPartJSArray: JsonArray = JsonArray()
      if (bonesMap.containsKey(keypoint.bodyPart.name))
        bodyPartJSArray = bonesMap[keypoint.bodyPart.name]!!
      var bodyPartJSObject: JsonObject = JsonObject();
      bodyPartJSObject.addProperty("frame", 1)
      bodyPartJSObject.addProperty("x", keypoint.position.x)
      bodyPartJSObject.addProperty("y", keypoint.position.y)
      bodyPartJSArray.add(bodyPartJSObject)
      bonesMap.put(keypoint.bodyPart.name, bodyPartJSArray)
      mainJsonAnims.add(keypoint.bodyPart.name, bodyPartJSArray)


      canvas.drawCircle(
        keypoint.position.x.toFloat(),
        keypoint.position.y.toFloat(), size, paint
      )
    }

    sampleImageView.adjustViewBounds = true
    sampleImageView.setImageBitmap(mutableBitmap)
  }
}
