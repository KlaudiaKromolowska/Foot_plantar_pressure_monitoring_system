package com.example.engineeringthesis.others

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream

class BitmapCreator {
    @TypeConverter
    fun fromBitmap(bitMap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitMap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }

    @TypeConverter
    fun toBitmap(bytes: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }
}