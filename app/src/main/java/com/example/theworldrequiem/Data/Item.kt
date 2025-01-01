package com.example.theworldrequiem.Data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.theworldrequiem.R

class Item(pName: String) {
    val name: String = pName //cherry, banana, avocado, kiwi, health, coin, maggot, fire, ultimate
    var target: String = "interactor" //interactor, enemy
    var effect: String = "recover" //recover, damage, skillUp, coin
    var frames: MutableList<Bitmap> = mutableListOf()
    var endFrames: MutableList<Bitmap> = mutableListOf()

    fun initial(context: Context)
    {
        when(name)
        {
            "cherry" -> {
                target = "interactor"
                effect = "ultimate"
                frames = mutableListOf(
                    Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.cherry), 250, 250, true)
                )
                endFrames = mutableListOf(
                    Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.cherry), 250, 250, true)
                )
            }
            "banana" -> {
                target = "interactor"
                effect = "recover"
                frames = mutableListOf(
                    Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.banana), 250, 250, true)
                )
                endFrames = mutableListOf(
                    Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.banana), 250, 250, true)
                )
            }
            "avocado" -> {
                target = "interactor"
                effect = "skillUp"
                frames = mutableListOf(
                    Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.avocado), 250, 250, true)
                )
                endFrames = mutableListOf(
                    Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.avocado), 250, 250, true)
                )
            }
            "kiwi" -> {
                target = "enemy"
                effect = "damage"
                frames = mutableListOf(
                    Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.kiwi), 250, 250, true)
                )
                endFrames = mutableListOf(
                    Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.kiwi), 250, 250, true)
                )
            }
            "health" -> {
                target = "interactor"
                effect = "recover"
                frames = mutableListOf(
                    Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.health), 250, 250, true)
                )
                endFrames = mutableListOf(
                    Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.health), 250, 250, true)
                )
            }
            "coin" -> {
                target = "interactor"
                effect = "coin"
                frames = mutableListOf(
                    Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.coin), 250, 250, true)
                )
                endFrames = mutableListOf(
                    Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.coin), 250, 250, true)
                )
            }
            "maggot" -> {
                target = "interactor"
                effect = "damage"
                frames = mutableListOf(
                    Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.maggot_1), 250, 250, true),
                    Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.maggot_2), 250, 250, true)
                )
                endFrames = mutableListOf(
                    Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.dead_1), 250, 250, true),
                    Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.dead_1), 250, 250, true),
                    Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.dead_1), 250, 250, true)
                )
            }
            "burn" -> {
                target = "enemy"
                effect = "damage"
                frames = mutableListOf(
                    Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.firespot_1), 250, 250, true),
                    Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.firespot_2), 250, 250, true),
                    Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.firespot_3), 250, 250, true),
                )
                endFrames = mutableListOf(
                    Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.firebullet_4), 250, 250, true),
                    Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.firebullet_5), 250, 250, true),
                    Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.firebullet_6), 250, 250, true)
                )
            }
        }
    }

}