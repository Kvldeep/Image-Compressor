package com.example.simple_image_compressor

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.health.connect.datatypes.units.Percentage
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class ImageRepository(private val context: Context) {
    suspend fun compressImage(imageUri: Uri,compressPercentage: Int): Uri = withContext(Dispatchers.IO){

        //here we are reading the original image
        val inputStream = context.contentResolver.openInputStream(imageUri)
            ?: throw Exception("unable to open image")
        //if the file is null well have to handle it distinctively see in internet otherwise app will crash

        val bitmap = BitmapFactory.decodeStream(inputStream)
            ?: throw Exception("unable to decode image")

        inputStream.close() //remember to close to solve memory leak

        //convert compression percentage to jpeg quality
        val quality = 100 - compressPercentage

        //create output file
        val outputFile = File(context.cacheDir,"compressed_${System.currentTimeMillis()}.jpg")

        //comress image
        outputFile.outputStream().use { outputStream ->
            bitmap.compress(
                Bitmap.CompressFormat.JPEG, quality, outputStream
            )
        }

        bitmap.recycle()

        Uri.fromFile(outputFile)
    }
}