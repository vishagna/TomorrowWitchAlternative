package com.example.theworldrequiem.Data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import com.example.theworldrequiem.R

class Ultimate(pName: String) {
    var remainTime = 0f
    var name = pName
    var frames: MutableList<Bitmap> = mutableListOf()
    var endFrames: MutableList<Bitmap> = mutableListOf()


    fun initial(context: Context)
    {
        if(name == "holy_circle")
        {
            frames = mutableListOf(
                BitmapFactory.decodeResource(context.resources, R.drawable.ultimate_cast_1),
                BitmapFactory.decodeResource(context.resources, R.drawable.ultimate_cast_2)
                )
            endFrames = mutableListOf(
                BitmapFactory.decodeResource(context.resources, R.drawable.ultimate_cast_1),
                BitmapFactory.decodeResource(context.resources, R.drawable.ultimate_cast_2)
            )
            remainTime = 10f
        }
    }


}