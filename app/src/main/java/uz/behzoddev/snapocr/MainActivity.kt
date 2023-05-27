package uz.behzoddev.snapocr

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.google.mlkit.vision.text.TextRecognizer
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.util.Log
import android.view.View
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.content.Intent
import android.net.Uri
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.view.Menu
import android.view.MenuItem
import androidx.core.graphics.drawable.toBitmap
import com.github.dhaval2404.imagepicker.ImagePicker
import dagger.hilt.android.AndroidEntryPoint
import uz.behzoddev.snapocr.databinding.ActivityMainBinding
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var recognizer: TextRecognizer

    private val TAG = "Testing"
    private val SAVED_TEXT_TAG = "SavedText"

    private val REQUEST_CODE_PERMISSIONS = 10
    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

    lateinit var camera: Camera
    var savedBitmap: Bitmap? = null

    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        if (savedInstanceState != null) {
            val savedText = savedInstanceState.getString(SAVED_TEXT_TAG)
            binding.apply {
                if (isTextValid(savedText)) {
                    textInImageLayout.visibility = View.VISIBLE
                    textInImage.text = savedInstanceState.getString(SAVED_TEXT_TAG)
                }
                if (savedBitmap != null) {
                    previewImage.visibility = View.VISIBLE
                    previewImage.setImageBitmap(savedBitmap)
                }
            }
        }
        init()
        setContentView(binding.root)
    }

    private fun init() {
        cameraExecutor = Executors.newSingleThreadExecutor()

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions()
        }

        binding.apply {
            extractTextButton.setOnClickListener {
                when {
                    previewImage.visibility == View.VISIBLE -> {
                        savedBitmap = previewImage.drawable.toBitmap()
                        runTextRecognition(savedBitmap!!)
                    }
                    viewFinder.bitmap != null -> {
                        previewImage.visibility = View.VISIBLE
                        savedBitmap = viewFinder.bitmap
                        previewImage.setImageBitmap(viewFinder.bitmap!!)
                        runTextRecognition(savedBitmap!!)
                    }
                    else -> {
                        showMessage(getString(R.string.camera_error_default_msg))
                    }
                }
            }

            copyToClipboard.setOnClickListener {
                val textToCopy = textInImage.text
                if (isTextValid(textToCopy.toString())) {
                    copyToClipboard(textToCopy)
                } else {
                    showMessage(getString(R.string.no_text_found))
                }
            }

            share.setOnClickListener {
                val textToCopy = textInImage.text.toString()
                if (isTextValid(textToCopy)) {
                    shareText(textToCopy)
                } else {
                    showMessage(getString(R.string.no_text_found))
                }
            }

            close.setOnClickListener {
                textInImageLayout.visibility = View.GONE
            }

        }

    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
        )
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                showMessage(
                    getString(R.string.permission_denied_msg)
                )
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.gallery) {
            binding.textInImageLayout.visibility = View.GONE
            ImagePicker.with(this)
                .galleryOnly()
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start()

            return true
        } else if (item.itemId == R.id.camera) {
            if(!allPermissionsGranted()){
                requestPermissions()
            } else {
                binding.previewImage.visibility = View.GONE
                savedBitmap = null
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                val uri: Uri = data?.data!!

                binding.previewImage.apply {
                    visibility = View.VISIBLE
                    setImageURI(uri)
                }
            }
            ImagePicker.RESULT_ERROR -> {
                showMessage(ImagePicker.getError(data))
            }
            else -> {
                showMessage("No Image Selected")
            }
        }
    }

    private fun runTextRecognition(bitmap: Bitmap) {
        val inputImage = InputImage.fromBitmap(bitmap, 0)

        recognizer
            .process(inputImage)
            .addOnSuccessListener { text ->
                binding.textInImageLayout.visibility = View.VISIBLE
                processTextRecognitionResult(text)
            }.addOnFailureListener { e ->
                e.printStackTrace()
                showMessage(e.localizedMessage ?: getString(R.string.error_default_msg))
            }
    }

    private fun processTextRecognitionResult(result: Text) {
        val finalText = buildString {
            result.textBlocks.forEach { block ->
                block.lines.forEach { line ->
                    append(line.text).append(" \n")
                }
                append("\n")
            }
        }

        with(binding.textInImage) {
            text = if (finalText.isNotEmpty()) {
                finalText
            } else {
                getString(R.string.no_text_found)
            }
            movementMethod = LinkMovementMethod.getInstance()
            Linkify.addLinks(this, Linkify.ALL)
        }

    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()

                // Bind use cases to camera
                camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview
                )


                binding.apply {

                    camera.apply {
                        torchImage.setBackgroundColor(resources.getColor(R.color.purple_200))
                        if (cameraInfo.hasFlashUnit()) {
                            torchButton.setOnClickListener {
                                cameraControl.enableTorch(cameraInfo.torchState.value == TorchState.OFF)
                            }
                        } else {
                            torchButton.setOnClickListener {
                                showMessage(getString(R.string.torch_not_available_msg))
                            }
                        }

                        cameraInfo.torchState.observe(this@MainActivity) { torchState ->
                            if (torchState == TorchState.OFF) {
                                torchImage.setImageResource(R.drawable.ic_flashlight_on)
                            } else {
                                torchImage.setImageResource(R.drawable.ic_flashlight_off)
                            }
                        }

                    }

                }
            } catch (exc: Exception) {
                showMessage(getString(R.string.error_default_msg))
            }

        }, ContextCompat.getMainExecutor(this))

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.clear()
        val textInImage = (binding.textInImage.text).toString()
        if (isTextValid(textInImage)) {
            outState.putString(SAVED_TEXT_TAG, textInImage)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

}