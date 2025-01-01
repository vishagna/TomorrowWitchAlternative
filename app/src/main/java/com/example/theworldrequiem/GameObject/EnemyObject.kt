package com.example.theworldrequiem.GameObject

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.PointF
import android.graphics.PorterDuff
import android.graphics.RectF
import android.icu.text.Transliterator.Position
import android.media.Image
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.theworldrequiem.Data.Enemy
import com.example.theworldrequiem.Data.Skill
import com.example.theworldrequiem.R
import com.example.theworldrequiem.global.MusicManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.atan2
import kotlin.math.sqrt

class EnemyObject(
    pEnemy: Enemy,
    pXPos: Float,
    pYPos:Float
) {
    var enemy = pEnemy.copy()

    private var primaryFrames: MutableList<Bitmap> = mutableListOf()
    private var currentFrameIndex = 0
    private var currentDeadIndex = 0
    private var job: Job? = null
    private var abilityJob: Job? = null
    private var bossAbilityJob: MutableList<Job> = mutableListOf()

    var xPos = pXPos
    var yPos = pYPos

    private var targetXPos = 0f;
    private var targetYPos = 0f;

    var isDead = false
    var isDisappear = false
    var canShotWeb = false
    var canShotPoison = false
    var canSpawnServant = false

//    fun move(canvas: Canvas) {
//        // Draw the current frame tại vị trí ngẫu nhiên
//        yPos -= speed
//        canvas.drawBitmap(imageFrames[currentFrameIndex], xPos, yPos, null)
//    }

    fun move(canvas: Canvas) {


        if(!isDead)
        {
            if(enemy.name != "Bat")
            {
                val movingRight = targetXPos > xPos
                val deltaX = targetXPos - xPos
                val deltaY = targetYPos - yPos
                val distance = sqrt(deltaX * deltaX + deltaY * deltaY)
                if (distance > enemy.speed) {
                    xPos += (deltaX / distance) * enemy.speed
                    yPos += (deltaY / distance) * enemy.speed
                } else {
                    xPos = targetXPos
                    yPos = targetYPos
                }
                canvas.save()
                if (movingRight) {
                    canvas.scale(
                        -1f, 1f,  // Lật ngang (trục X)
                        xPos + enemy.frames[currentFrameIndex].width / 2,  // Tâm lật ngang X
                        yPos + enemy.frames[currentFrameIndex].height / 2  // Tâm lật ngang Y
                    )
                }
                canvas.drawBitmap(enemy.frames[currentFrameIndex], xPos, yPos, null)
                canvas.restore()
            }
            else if(enemy.name == "Bat")
            {
                canvas.drawBitmap(enemy.frames[currentFrameIndex], xPos, yPos, null)
            }
        }
        else
        {
            canvas.drawBitmap(enemy.deadFrames[currentDeadIndex], xPos, yPos, null)
        }

        // Di chuyen theo player


        // Vẽ đối tượng tại vị trí mới
    }

    fun start(context: Context) {
        primaryFrames = enemy.frames
        castAbility(context)
        if (context is LifecycleOwner) {
            job = context.lifecycleScope.launch(Dispatchers.Main) {
                while (true) {
                    delay(100)  // Wait for 100ms
                    currentFrameIndex = (currentFrameIndex + 1) % enemy.frames.size  // Update frame
                }
            }
        } else {
            throw IllegalArgumentException("Context must be a LifecycleOwner")
        }
    }

    fun end(context: Context) {
        if(!isDead)
        {
            isDead = true
            MusicManager.playMusic(context, R.raw.enemy_dead)
            job?.cancel()
            abilityJob?.cancel()
            if (context is LifecycleOwner) {
                job = context.lifecycleScope.launch(Dispatchers.Main) {
                    for (i in currentDeadIndex until enemy.deadFrames.size-1) {
                        delay(150)  // Wait for 100ms
                        currentDeadIndex = (currentDeadIndex + 1)
                    }
                    delay(150)  // Wait for 100ms
                    isDisappear = true

//                    while (true) {
//                        if(currentEndIndex < endFrames.size)
//                        {
//                            delay(150)  // Wait for 100ms
//                            currentEndIndex = (currentEndIndex + 1)  // Update frame
//                        }
//                    }
                }
            } else {
                throw IllegalArgumentException("Context must be a LifecycleOwner")
            }
        }

    }

    fun stop() {
        job?.cancel()  // Stop the animation when needed
        abilityJob?.cancel()
        for(jb in bossAbilityJob)
        {
            jb.cancel()
        }
    }

//    fun isColliding(interactorX: Float, interactorY: Float, interactorWidth: Int, interactorHeight: Int): Boolean {
//        val enemyWidth = imageFrames[0].width
//        val enemyHeight = imageFrames[0].height
//
//        return interactorX < xPos + enemyWidth &&
//                interactorX + interactorWidth > xPos &&
//                interactorY < yPos + enemyHeight &&
//                interactorY + interactorHeight > yPos
//    }

    fun castAbility(context: Context)
    {
        if(enemy.ability == "boost")
        {
            if(context is LifecycleOwner)
            {
                abilityJob = context.lifecycleScope.launch(Dispatchers.Main) {
                    while (true)
                    {
                        delay(4000)
                        enemy.speed += 4
                        delay(1500)
                        enemy.speed -= 4
                    }
                }
            }
        }
        else if(enemy.ability == "shot_web")
        {
        if(context is LifecycleOwner)
            {
                abilityJob = context.lifecycleScope.launch(Dispatchers.Main) {
                    while (true)
                    {
                        delay(4100)
                        canShotWeb = true
                    }
                }
            }
        }
        else if(enemy.ability == "shot_poison")
        {
            if(context is LifecycleOwner)
            {
                abilityJob = context.lifecycleScope.launch(Dispatchers.Main) {
                    while (true)
                    {
                        delay(4100)
                        canShotPoison = true
                    }
                }
            }
        }
        else if(enemy.ability == "boss")
        {
            if(context is LifecycleOwner)
            {
                bossAbilityJob.add(
                    context.lifecycleScope.launch(Dispatchers.Main) {
                    while (true)
                    {
                        delay(4000)
                        enemy.speed += 2
//                        currentFrameIndex = 0
//                        enemy.frames = enemy.alternativeFrames
                        delay(2500)
//                        enemy.frames = primaryFrames
                        enemy.speed -= 2
                    }
                }
                )

                bossAbilityJob.add(
                    context.lifecycleScope.launch(Dispatchers.Main) {
                        while (true)
                        {
                            delay(4100)
                            canShotPoison = true
                        }
                    }
                )

                bossAbilityJob.add(
                    context.lifecycleScope.launch(Dispatchers.Main) {
                        while (true)
                        {
                            delay(5100)
                            canSpawnServant = true
                        }
                    }
                )

            }
        }

    }

    fun shot(context: Context): BulletObject?
    {
        if(canShotWeb)
        {
            canShotWeb = false
            val skill = Skill(0, "attack", false, "web", BitmapFactory.decodeResource(context.resources, R.drawable.web_4))
            skill.initial(context)
                // Lấy vị trí của kẻ thù
                val targetX = targetXPos
                val targetY = targetYPos

                // Tạo viên đạn nhắm đến kẻ thù
                val angle = Math.toDegrees(
                    atan2((targetY - getExactPosition().y.toDouble()), (targetX - getExactPosition().x.toDouble()))
                ).toFloat()

                val bullet = BulletObject( "enemy", "lock",
                    1, skill.name, 1, skill.frames, skill.endFrames,
                    getExactPosition().x, getExactPosition().y, angle
                )
                bullet.start(context)
                return bullet
        }
        else if(canShotPoison)
        {
            canShotPoison = false
            val skill = Skill(0, "attack", false, "poison", BitmapFactory.decodeResource(context.resources, R.drawable.web_4))
            skill.initial(context)
            // Lấy vị trí của kẻ thù
            val targetX = targetXPos
            val targetY = targetYPos

            // Tạo viên đạn nhắm đến kẻ thù
            val angle = Math.toDegrees(
                atan2((targetY - yPos.toDouble()), (targetX - xPos.toDouble()))
            ).toFloat()

            val bullet = BulletObject( "enemy", "damage",
                1, skill.name, 1, skill.frames, skill.endFrames,
                xPos, yPos, angle
            )
            bullet.start(context)
            return bullet
        }
        return null
    }

    fun takeDamage(damage: Int)
    {
        enemy.hp -= damage
    }

    fun canDead(): Boolean
    {
        if( enemy.hp <= 0 && !isDead)
        {
            return true
        }
        else return false
    }

    fun getExactPosition(): PointF
    {
        return PointF(xPos + enemy.frames[currentFrameIndex].width/2,
            yPos + enemy.frames[currentFrameIndex].height/2)
    }

    fun updateTarget(x: Float, y: Float)
    {
        targetXPos = x;
        targetYPos = y;
    }


    fun getBoundingBox(): RectF {
        return RectF(
            xPos + enemy.frames[currentFrameIndex].width / 4,
            yPos + enemy.frames[currentFrameIndex].height / 4,
            xPos + enemy.frames[currentFrameIndex].width / 4 * 3,
            yPos + enemy.frames[currentFrameIndex].height / 4 * 3
        )
    }

}