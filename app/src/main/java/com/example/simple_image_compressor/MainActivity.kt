package com.example.simple_image_compressor

import android.content.ContentValues
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.simple_image_compressor.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import java.io.File
import java.io.OutputStream
import java.net.URI

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    private lateinit var viewModel : ImageCompressorViewModel
    //for getting image uri
    private var selectedImageUri : Uri? = null

    //getting the image uri here i.e registry
    private val pickImage = registerForActivityResult(ActivityResultContracts.PickVisualMedia()){uri ->
        if (uri != null){
            selectedImageUri = uri
            handleSelectedImage(uri)
        }
    }

    //for camera click
    private lateinit var latestImageUri: Uri

    private val CameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()){
        success: Boolean ->
        if (success){
            selectedImageUri = latestImageUri
            handleSelectedImage(latestImageUri)
        }
    }

    //original size
    private var originalFileSize = 0L

    //percentage
    private var quality = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = ImageRepository(applicationContext)
        val factory = ImageCompressorViewModelFactory(repository)
        viewModel = ViewModelProvider(this,factory)[ImageCompressorViewModel::class.java]

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.galleryBtn.setOnClickListener {
            pickImage.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }

        binding.cameraBtn.setOnClickListener {
            latestImageUri = getTempFileUri()
            //here getting the image from uri then setting it to the image view
            CameraLauncher.launch(latestImageUri)
        }

        //for seekbar
        binding.seekbar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener{
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                   binding.percentage.text = "$progress%"

                    updateEstimatedSize(progress)

                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {

                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {

                }

            }
        )

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.compressedImage.collect { uri ->

                    uri?.let {
                        binding.compressedImage.setImageURI(it)

                        //here we are going to do get the compression image
                        val compressedFile = File(it.path!!)
                        val compressedSize = compressedFile.length()

                        binding.compressedSize.text = formatFileSize(compressedSize)

                        //compressed dimension
                        val option = BitmapFactory.Options().apply {
                            inJustDecodeBounds = true
                        }

                        contentResolver.openInputStream(it)?.use { inputStream ->
                            BitmapFactory.decodeStream(
                                inputStream, null, option
                            )
                        }

                        binding.compressedDimen.text = "${option.outWidth} x ${option.outHeight}"
                    }

                }
            }

        }

        binding.compressBtn.setOnClickListener {
            selectedImageUri?.let { uri ->
                val compressionPercentage = binding.seekbar.progress

                viewModel.compressImage(
                    uri,compressionPercentage
                )
            }
        }

            binding.downloadBtn.setOnClickListener {
                viewModel.compressedImage.value?.let { uri ->
                    saveCompressedImage(uri)
                }
            }

    }

    //for size and dimension
    private fun handleSelectedImage(uri : Uri){

        //image shown
        binding.OriginalImage.setImageURI(uri)

        val option = BitmapFactory.Options()
        option.inJustDecodeBounds = true //this means android does not get the image directly in the memory

        contentResolver.openInputStream(uri)?.use { inputStream ->
            BitmapFactory.decodeStream(inputStream,null,option)
        }

        val width = option.outWidth
        val height = option.outHeight

        contentResolver.query(
            uri,
            arrayOf(OpenableColumns.SIZE),
            null,
            null,
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()){
                originalFileSize = cursor.getLong(
                    cursor.getColumnIndexOrThrow(OpenableColumns.SIZE)
                )
            }
        }

        binding.OriginalDimen.text = "$width x $height"
        binding.OriginalSize.text = formatFileSize(originalFileSize)

        updateEstimatedSize(binding.seekbar.progress)
    }

    //for eastimated size
    private fun updateEstimatedSize(compression : Int){

        if (originalFileSize <=0) return

        val estimatedSize = originalFileSize * (1 - compression /100.0)


        binding.estimatedSize.text = formatFileSize(estimatedSize.toLong())

        binding.estimatedReduction.text = "$compression%"
    }


    //saving image in mobile
    private fun saveCompressedImage(uri: Uri){
        val fileName = "Compressed_${System.currentTimeMillis()}.jpg"

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME,fileName)
            put(MediaStore.Images.Media.MIME_TYPE,"iamge/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }

        val savedUri = contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues
        )

        if (savedUri != null){
            contentResolver.openInputStream(uri)?.use { inputStream ->
                contentResolver.openOutputStream(savedUri)?.use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            Toast.makeText(this,"Image saved to Pictures", Toast.LENGTH_SHORT).show()
        }
        else{
            Toast.makeText(this,"Failed to save image", Toast.LENGTH_SHORT).show()
        }
    }

}

//for getting the file from user
private fun MainActivity.getTempFileUri(): Uri {
    val tempFile = File.createTempFile("temp_image_file",".jpg",cacheDir).apply {
        deleteOnExit()
    }
    return FileProvider.getUriForFile(
        applicationContext,
        "${packageName}.fileProvider", tempFile
    )
}


//for size format
private fun MainActivity.formatFileSize(bytes: Long): String{
    return when{
        bytes < 1024 ->
            "$bytes B"
        bytes < 1024 * 1024 ->
            "%.2f kb".format(bytes/1024.0)

        else ->
            "%.2f MB".format(bytes/(1024.0 * 1024.0))
    }
}
