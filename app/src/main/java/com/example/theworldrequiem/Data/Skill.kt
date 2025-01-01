package com.example.theworldrequiem.Data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.theworldrequiem.R

class Skill(pCooldown: Long,pType: String, pEnhancable: Boolean, pName: String, pImage: Bitmap) {
    val name = pName
    val image = pImage
    val enhancable = pEnhancable
    val coolDown = pCooldown
    val type = pType
    var frames: MutableList<Bitmap> = mutableListOf()
    var endFrames: MutableList<Bitmap> = mutableListOf()

    fun initial(context: Context)
    {
        if(name == "harm")
        {
            frames = mutableListOf(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.firebullet_1), 100, 100, true),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.firebullet_2), 100, 100, true),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.firebullet_3), 100, 100, true)
            )
            endFrames = mutableListOf(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.firebullet_4), 100, 100, true),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.firebullet_5), 100, 100, true),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.firebullet_6), 100, 100, true)
            )
        }
        else if(name == "burn")
        {
            frames = mutableListOf(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.firespot_1), 300, 300, true),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.firespot_2), 300, 300, true),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.firespot_3), 300, 300, true),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.firespot_4), 300, 300, true)
            )
            endFrames = mutableListOf(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.firebullet_4), 300, 300, true),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.firebullet_5), 300, 300, true),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.firebullet_6), 300, 300, true)
            )
        }
        else if(name == "negotigate")
        {
            frames = mutableListOf(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.flower_1), 500, 500, true),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.flower_2), 500, 500, true),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.flower_3), 500, 500, true),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.flower_4), 500, 500, true)
            )
            endFrames = mutableListOf(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.flower_5), 500, 500, true),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.flower_6), 500, 500, true),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.flower_7), 500, 500, true)
            )
        }
        else if(name == "flower")
        {
            frames = mutableListOf(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.sun_1), 100, 100, true),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.sun_2), 100, 100, true),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.sun_3), 100, 100, true),
            )
            endFrames = mutableListOf(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.sun_4), 100, 100, true),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.sun_5), 100, 100, true))
        }
        else if(name == "poison")
        {
            frames = mutableListOf(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.poison_bullet_1), 100, 100, true),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.poison_bullet_2), 100, 100, true),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.poison_bullet_3), 100, 100, true),
            )
            endFrames = mutableListOf(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.poison_bullet_4), 100, 100, true),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.poison_bullet_5), 100, 100, true),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.poison_bullet_6), 100, 100, true))
        }
        else if(name == "web")
        {
            frames = mutableListOf(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.web_1), 100, 100, true),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.web_2), 100, 100, true),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.web_3), 100, 100, true),
            )
            endFrames = mutableListOf(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.web_4), 200, 200, true),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.web_4), 200, 200, true),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.web_4), 200, 200, true))
        }
    }
}