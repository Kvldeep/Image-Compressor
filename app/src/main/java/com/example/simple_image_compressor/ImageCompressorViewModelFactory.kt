package com.example.simple_image_compressor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ImageCompressorViewModelFactory(private val repository: ImageRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ImageCompressorViewModel(repository) as T
    }
}