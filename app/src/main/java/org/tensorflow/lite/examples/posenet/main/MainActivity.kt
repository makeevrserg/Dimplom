package org.tensorflow.lite.examples.posenet.main

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_sheet_layout.view.*
import org.tensorflow.lite.examples.posenet.*
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity() {

    var imageView: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        val buttonRealTime: Button = findViewById(R.id.buttonRealTime)
        val buttonOpenExisting: Button = findViewById(R.id.buttonOpenExisting)
        val buttonOpenCamera: Button = findViewById(R.id.buttonOpenCamera)
        val buttonTestPost: Button = findViewById(R.id.buttonTestPost)
        buttonTestPost.setOnClickListener {
            val post:POST = POST( (getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE)?.getString("TOKEN", "") as String))
            val callback:String = post.CreateAnim("TestTodo")
            if (callback.contains("created"))
                Toast.makeText(this, "Всё работает, вы авторизованы!", Toast.LENGTH_SHORT).show()
            //post.CreateTodo(applicationContext!!,"New Todo")
        }
        buttonOpenExisting.setOnClickListener {
            if (checkForPermission(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    "Storage",

                    STORAGE_RQ
                )
            ) {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, PICK_IMAGE_CODE)
            }
        }
        buttonOpenExistingVideo.setOnClickListener {
            if (checkForPermission(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    "Storage",

                    STORAGE_RQ
                )
            ) {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "video/*"
                startActivityForResult(intent, PICK_VIDEO_CODE)
            }
        }
        buttonOpenCamera.setOnClickListener {
            if (checkForPermission(
                            android.Manifest.permission.CAMERA,
                            "Camera",
                            CAMERA_RQ
                    )
            ) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, CAPTURE_IMAGE_CODE)
            }
        }
        buttonRealTime.setOnClickListener {
            if (checkForPermission(android.Manifest.permission.CAMERA, "Camera", CAMERA_RQ)) {
                val intent = Intent(this, CameraActivity::class.java)
                startActivity(intent)
            }

        }
        buttonTestImage.setOnClickListener {
            startActivity(Intent(this, TestActivity::class.java))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAPTURE_IMAGE_CODE) {
            data ?: return
            var bmp: Bitmap = data?.extras?.get("data") as Bitmap
            val stream = ByteArrayOutputStream()
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            val byteArray = stream.toByteArray()

            val intent = Intent(this, TestActivity::class.java)
            intent.putExtra("BitmapImage", byteArray)
            startActivity(intent)
        } else if (requestCode == PICK_IMAGE_CODE) {
            data ?: return

            val intent = Intent(this, TestActivity::class.java)
            intent.putExtra("BitmapImage", data?.data.toString())
            startActivity(intent)

        }else if (requestCode == PICK_VIDEO_CODE) {
            data ?: return

            val intent = Intent(this, VideoActivity::class.java)

            intent.putExtra("Video", data.data!!.path)
            startActivity(intent)

        }
        else return
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //return super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //return super.onOptionsItemSelected(item)
        var itemView = item.itemId
        when (itemView) {
            R.id.anims->{
                val intent = Intent(applicationContext, AnimsActivity::class.java)
                startActivity(intent)
            }
            R.id.settings -> {
                val bottomSheetDialog = BottomSheetDialog(this)
                val view = layoutInflater.inflate(R.layout.bottom_sheet_layout, null)
                bottomSheetDialog.setContentView(view)
                bottomSheetDialog.show()

                var minConfidence =
                        (getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE)?.getFloat("minConfidence", 0.5f) as Float) * 100.0f
                view.seekBarMinConfidence.setProgress((minConfidence/100.0f).toInt())
                view.textViewMinConfidence.text = "Min Confidence: ${minConfidence/100.0f}"
                view.buttonRegister.setOnClickListener {
                    val post: POST = POST()
                    val login: String = view.textInputLogin.text.toString()
                    val password: String = view.textInputPassword.text.toString()
                    val msg:String = post.Register(login, password)
                    if (msg.contains("already exists"))
                        Toast.makeText(this, "Пользователь существует", Toast.LENGTH_SHORT).show()
                    else
                        Toast.makeText(this, "Успешно зарегистрированы!", Toast.LENGTH_SHORT).show()
                    Log.d("POST", "Register Message:"+msg)


                }
                view.buttonLogin.setOnClickListener {
                    val post: POST = POST()
                    val login: String = view.textInputLogin.text.toString()
                    val password: String = view.textInputPassword.text.toString()

                    val TOKEN = post.GetToken(login, password)
                    println("Got token"+TOKEN)

                    if (TOKEN != null && !TOKEN.isEmpty()) {
                        Toast.makeText(this, "Успешно авторизованы!", Toast.LENGTH_SHORT).show()
                        val sharedPref = getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE)
                        with(sharedPref.edit()) {
                            putString("TOKEN", TOKEN)
                            apply()
                        }
                    }
                }
                view.textViewMinConfidence.setText("Min Confidence: " + minConfidence / 100.0)
                view.seekBarMinConfidence.setProgress(minConfidence.toInt())
                view.seekBarMinConfidence.setOnSeekBarChangeListener(object :
                        SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(
                            seekBar: SeekBar?,
                            progress: Int,
                            fromUser: Boolean
                    ) {
                        view.textViewMinConfidence.setText("Min Confidence: " + progress / 100.0)
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {
                        return
                        //TODO("Not yet implemented and not gonna be")
                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar?) {

                        val sharedPref = getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE)
                                ?: return
                        with(sharedPref.edit()) {
                            putFloat("minConfidence", (seekBar?.progress as Int) / 100.0f)
                            apply()
                        }

                        return
                        //TODO("Not yet implemented ")
                    }

                })

                var captureTime =
                        (getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE)?.getInt("captureTime", 10))
                view.textViewCaptureTime.setText("Caputure Time: $captureTime second")
                view.seekBarCaptureTime.setProgress(captureTime as Int)
                view.seekBarCaptureTime.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                        view.textViewCaptureTime.setText("Caputure Time: $progress second")

                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {
                        //TODO("Not yet implemented")
                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar?) {
                        val sharedPref = getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE)
                                ?: return
                        with(sharedPref.edit()) {
                            putInt("captureTime", (seekBar?.progress as Int))
                            apply()
                        }
                    }
                })

            }

        }
        return false
    }

    private fun checkForPermission(permission: String, name: String, requestCode: Int): Boolean {
        when {
            ContextCompat.checkSelfPermission(
                    applicationContext,
                    permission
            ) == PackageManager.PERMISSION_GRANTED -> {
//                Toast.makeText(applicationContext, "$name permission granted", Toast.LENGTH_SHORT)
//                    .show();
                return true
            }
            shouldShowRequestPermissionRationale(permission) -> showDialog(
                    permission,
                    name,
                    requestCode
            )
            else -> ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
        }
        return false
    }


    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        fun innerCheck(name: String) {
            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(applicationContext, "$name permision refused", Toast.LENGTH_SHORT)
                        .show()
            }
        }
        when (requestCode) {
            CAMERA_RQ -> innerCheck("Camera")
        }
    }

    private fun showDialog(permission: String, name: String, requestCode: Int) {
        val builder = AlertDialog.Builder(this)
        builder.apply {
            setMessage("Permission to acsess your $name is required to use this app")
            setTitle("Permission required")
            setPositiveButton("Ok") { dialog, which ->
                ActivityCompat.requestPermissions(
                        this@MainActivity,
                        arrayOf(permission),
                        requestCode
                )
            }
        }
        val dialog: AlertDialog = builder.create();
        dialog.show();
    }


}