@file:JvmName("Constants")

package org.tensorflow.lite.examples.posenet

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.tensorflow.lite.examples.posenet.lib.KeyPoint

/** Request camera and external storage permission.   */
const val REQUEST_CAMERA_PERMISSION = 1

/** Model input shape for images.   */
const val MODEL_WIDTH = 257
const val MODEL_HEIGHT = 257

const val STORAGE_RQ = 102
const val CAMERA_RQ = 103

const val PICK_IMAGE_CODE = 104
const val CAPTURE_IMAGE_CODE = 105
const val PICK_VIDEO_CODE = 106


class JsonPoints() {
    private var mainJsonAnims: JsonObject = JsonObject();
    private val bonesMap: HashMap<String, JsonArray> = HashMap()

    fun getJson(): String {
        return mainJsonAnims.toString()
    }
    fun add(keyPoint: KeyPoint, frame: Int) {
        val bodyPartJSArray: JsonArray = CreatePointJSArray(
            bonesMap[keyPoint.bodyPart.name],
            frame,
            keyPoint.position.x,
            keyPoint.position.y
        )
        bonesMap[keyPoint.bodyPart.name] = bodyPartJSArray
        mainJsonAnims.add(keyPoint.bodyPart.name, bodyPartJSArray)

    }

    fun CreatePointJSArray(jsonArr: JsonArray?, frame: Int, x: Int, y: Int): JsonArray {
        var bodyPartJSArray = JsonArray()
        bodyPartJSArray = jsonArr ?: bodyPartJSArray
        val bodyPartJSObject = JsonObject();
        bodyPartJSObject.addProperty("frame", frame)
        bodyPartJSObject.addProperty("x", x)
        bodyPartJSObject.addProperty("y", y)
        bodyPartJSArray.add(bodyPartJSObject)
        return bodyPartJSArray
    }

}