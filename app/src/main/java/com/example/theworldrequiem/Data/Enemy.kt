package com.example.theworldrequiem.Data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import com.example.theworldrequiem.R

class Enemy(pName: String, pSpeed: Float, pHp: Int) {
    val name = pName
    var speed = pSpeed
    var hp = pHp
    var frames: MutableList<Bitmap> = mutableListOf()
    var deadFrames: MutableList<Bitmap> = mutableListOf()
    var alternativeFrames: MutableList<Bitmap> = mutableListOf()
    var ability: String = "none"

    fun copy(): Enemy {
        val enemy: Enemy = Enemy(name, speed, hp)
        enemy.frames = frames
        enemy.deadFrames = deadFrames
        enemy.ability = ability
        return enemy// Tạo bản sao thủ công
    }

    fun initial(context: Context)
    {
        if(name == "Lizard")
        {
            frames = mutableListOf(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.lizard_1), 450, 300, true),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.lizard_2), 450, 300, true),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.lizard_3), 450, 300, true)
            )
            deadFrames = mutableListOf(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.dead_1), 300, 300, true),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.dead_2), 300, 300, true),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.dead_3), 300, 300, true)
            )
        }

        if(name == "Spider")
        {
            frames = mutableListOf(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.spider_1), 450, 300, true),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.spider_2), 450, 300, true),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.spider_3), 450, 300, true)
            )
            deadFrames = mutableListOf(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.dead_1), 300, 300, true),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.dead_2), 300, 300, true),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.dead_3), 300, 300, true)
            )
            ability = "shot_web"
        }

        if(name == "Ant")
        {
            frames = mutableListOf(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.ant_1), 450, 300, true),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.ant_2), 450, 300, true),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.ant_3), 450, 300, true)
            )
            deadFrames = mutableListOf(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.dead_1), 300, 300, true),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.dead_2), 300, 300, true),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.dead_3), 300, 300, true)
            )
            ability = "boost"
        }

        if(name == "Centipede")
        {
            frames = mutableListOf(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.centipede_1), 450, 300, true),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.centipede_2), 450, 300, true),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.centipede_3), 450, 300, true)
            )
            deadFrames = mutableListOf(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.dead_1), 300, 300, true),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.dead_2), 300, 300, true),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.dead_3), 300, 300, true)
            )
        }

        if(name == "Scorpion")
        {
            frames = mutableListOf(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.scorpion_1), 450, 300, true),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.scorpion_2), 450, 300, true),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.scorpion_3), 450, 300, true)
            )
            deadFrames = mutableListOf(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.dead_1), 300, 300, true),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.dead_2), 300, 300, true),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.dead_3), 300, 300, true)
            )
            ability = "shot_poison"
        }

        if(name == "Bat")
        {
            frames = mutableListOf(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.bat_1), 450, 300, true),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.bat_1), 450, 300, true),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.bat_1), 450, 300, true)
            )
            alternativeFrames = mutableListOf(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.bat_1), 450, 300, true),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.bat_2), 450, 300, true),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.bat_3), 450, 300, true)
            )
            deadFrames = mutableListOf(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.dead_1), 300, 300, true),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.dead_2), 300, 300, true),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.dead_3), 300, 300, true)
            )
            ability = "sonic"
        }

        if(name == "Cockroach")
        {
            frames = mutableListOf(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.cockroach_1), 750, 500, true),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.cockroach_2), 750, 500, true),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.cockroach_3), 750, 500, true)
            )
            alternativeFrames = mutableListOf(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.cockroach_4), 750, 500, true),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.cockroach_5), 750, 500, true),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.cockroach_6), 750, 500, true)
            )
            deadFrames = mutableListOf(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.dead_1), 500, 500, true),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.dead_2), 500, 500, true),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.dead_3), 500, 500, true)
            )
            ability = "boss"
        }
    }
}



