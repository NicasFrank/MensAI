package com.example.mensai.data

import android.graphics.Bitmap
import com.example.mensai.domain.Foods
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageClassificationRepository @Inject constructor(
    private val cnnModel: CnnModel
) {
    fun classifyImage(image : Bitmap) : Foods{
        return cnnModel.inference(image)
    }
}