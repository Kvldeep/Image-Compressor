package com.example.simple_image_compressor

import android.health.connect.datatypes.units.Percentage
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ImageCompressorViewModel(private val repository: ImageRepository) : ViewModel(){

    private val _compressedImage = MutableStateFlow<Uri?>(null)
    val compressedImage = _compressedImage.asStateFlow()

    fun compressImage(imageUri: Uri,compressionPercentage: Int){
        viewModelScope.launch {
            val compressedUri = repository.compressImage(
                imageUri,compressionPercentage
            )

            _compressedImage.value = compressedUri
        }
    }

}