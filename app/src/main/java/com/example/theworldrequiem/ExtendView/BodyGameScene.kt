package com.example.theworldrequiem.ExtendView

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.PorterDuff
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.theworldrequiem.Controllers.SkillClaim
import com.example.theworldrequiem.Data.Enemy
import com.example.theworldrequiem.Data.Item
import com.example.theworldrequiem.Data.Skill
import com.example.theworldrequiem.Data.SkillClaimed
import com.example.theworldrequiem.Data.Ultimate
import com.example.theworldrequiem.Database.DatabaseManager
import com.example.theworldrequiem.EndGameScene
import com.example.theworldrequiem.GameObject.BulletObject
import com.example.theworldrequiem.GameObject.EnemyObject
import com.example.theworldrequiem.GameObject.InteractionObject
import com.example.theworldrequiem.GameObject.ItemObject
import com.example.theworldrequiem.GameObject.UltimateObject
import com.example.theworldrequiem.R
import com.example.theworldrequiem.databinding.ActivityBodyCameraBinding
import com.example.theworldrequiem.global.MusicManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.atan2
import kotlin.math.sqrt
import kotlin.random.Random

open class BodyGameScene (context: Context?, attrs: AttributeSet) : View(context, attrs) {

    private var canvasWidth: Int = 300
    private var canvasHeight: Int = 900
    private var setup = false

    private var spawnJob: Job? = null
    private var shotJobs: MutableList<Job> = mutableListOf()
    private var flowerJob: Job? = null
    private var ultimateJob: Job? = null
    private var buffJobs: MutableList<Job> = mutableListOf()

    private lateinit var interactor: InteractionObject

    private var ultimateObject: UltimateObject? = null
    private var items: MutableList<ItemObject> = mutableListOf()
    private var enemies: MutableList<EnemyObject> = mutableListOf()
    private var bullets: MutableList<BulletObject> = mutableListOf()
    private var backgroundBitmap: Bitmap? = null // Bitmap hình nền
    var isBossDead = false

    var skillClaim: SkillClaim? = null

    private var isBackground = false

    private var hp = 5
    private var hpMax = 5
    var coin = 0
    var coinShow: TextView? = null
    private var hpShow: MutableList<ImageView> = mutableListOf()
    private var itemRandom = mutableListOf(
        Item("cherry"),
        Item("banana"),
        Item("avocado"),
        Item("kiwi"),
        Item("coin")
    )

    fun castUltimate(pUltimateName: String)
    {
        val newUltimate = Ultimate(pUltimateName)
        newUltimate.initial(context)
        ultimateObject = UltimateObject(interactor.xPos, interactor.yPos, newUltimate)
        ultimateObject!!.start(context)
        ultimating(context)
    }

    fun fetchImageShow(pHpShow: MutableList<ImageView>) {
        hpShow = pHpShow
    }

    private val paint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 8f
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)




        updateHp(0)

        if(setup)
        {
            canvas.drawColor(0, PorterDuff.Mode.CLEAR)
//            canvas.drawCircle(200f, 200f, 12f, paint)
//            canvas.drawCircle(800f, 1100f, 32f, paint)
            if(isBackground)
            {
                backgroundBitmap?.let {
                    canvas.drawBitmap(it, 0f, 0f, null)
                }
            }


            ultimateObject?.updatePosition(interactor.getExactPosition())
            ultimateObject?.move(canvas)
            if(ultimateObject?.isDestroy == true)
            {
                ultimateObject = null
            }

//            enemies.forEach { enemy ->
//                enemy.updateTarget(interactor.xPos, interactor.yPos)
//                enemy.move(canvas)
//            }

            for(i in items.size - 1 downTo  0)
            {
                val item = items[i]
                item.move(canvas)
                if(!item.isEnd)
                {
                    if(item.item.target == "enemy")
                    {
                        for( enemy in enemies)
                        {
                            if(!enemy.isDead)
                            {
                                if(RectF.intersects(enemy.getBoundingBox(), item.getBoundingBox()))
                                {
                                    if(item.item.effect == "damage")
                                    {
                                        enemy.takeDamage(1)
                                        item.end(context)
                                    }
                                }
                            }
                        }
                    }
                    if(item.item.target == "interactor")
                    {
                        if(RectF.intersects(interactor.getBoundingBox(), item.getBoundingBox()))
                        {
                            if(item.item.effect == "damage")
                            {
                                interactor.takeDamage(context)
                                updateHp(-1)
                            }
                            else if(item.item.effect == "recover")
                            {
                                updateHp(+1)
                            }
                            else if(item.item.effect == "coin")
                            {
                                getCoin(1)
                            }
                            else if(item.item.effect == "skillUp")
                            {
                                skillClaim?.startClaim(context)

                            }
                            else if(item.item.effect == "ultimate")
                            {
                                skillClaim?.startUltimate()
                            }
                            item.end(context)
                        }
                    }
                }

                if(item.isDestroy)
                {
                    item.stop()
                    items.removeAt(i)
                }

            }

            for(i in enemies.size - 1 downTo 0)
            {
                val enemy = enemies[i];
                enemy.updateTarget(interactor.getExactPosition().x, interactor.getExactPosition().y)
                enemy.move(canvas)
                if(enemy.canShotWeb || enemy.canShotPoison)
                {
                    enemy.shot(context)?.let { bullets.add(it) }
                }
                else if(enemy.canSpawnServant)
                {
                    val maggot = Item("maggot")
                    maggot.initial(context)
                    val dropItem = ItemObject(PointF(enemy.getExactPosition().x, enemy.getExactPosition().y), maggot)

                    items.add(dropItem)
                    dropItem.start(context)
                    enemy.canSpawnServant = false
                }
                if(enemy.canDead())
                {
                    enemy.end(context)
                }

                if(!interactor.isInvincible && !enemy.isDead && RectF.intersects(enemy.getBoundingBox(), interactor.getBoundingBox()))
                {
                    interactor.takeDamage(context)
                    updateHp(-1)
                }

                if(enemy.isDisappear)
                {
                    enemy.stop()
                    var itemDrop: Boolean = (0..9).random() == 0
                    itemDrop = true
                    if(itemDrop)
                    {
                        val dropItem = ItemObject(PointF(enemy.getExactPosition().x, enemy.getExactPosition().y), itemRandom.shuffled().take(1)[0])
                        items.add(dropItem)
                        dropItem.start(context)
                    }
                    if(enemies[i].enemy.ability == "boss") isBossDead = true
                    enemies.removeAt(i)
                }
            }


            for (i in bullets.size - 1 downTo 0) {
                val bullet = bullets[i]
                bullet.move(canvas)

                when(bullet.bulletName)
                {
                    "poison", "web" -> {
                        if(RectF.intersects(interactor.getBoundingBox(), bullet.getBoundingBox()) && !bullet.isEnd  && !interactor.isInvincible)
                        {
                            interactor.takeDamage(context)
                            when(bullet.effect)
                            {
                                "lock" -> {
                                    interactor.getStuck(context)
                                }
                                "damage" -> {
                                    interactor.takeDamage(context)
                                    updateHp(-1)
                                }
                            }
                            bullet.end(context)
                        }
                    }
                    "negotigate" -> {

                    }
                    "harm", "burn", "flower" -> {
                        for (j in enemies.size - 1 downTo 0) {
                            val enemy = enemies[j]
                            if ( !enemy.isDead && RectF.intersects(bullet.getBoundingBox(), enemy.getBoundingBox())) {
                                if(!bullet.isEnd)
                                {
                                    enemies[j].takeDamage(bullet.damage)
                                    bullet.end(context)
                                }
                                break
                            }
                        }

                        if ((bullet.xPos <= 0f || bullet.yPos <= 0f
                                    || bullet.xPos >= canvas.width.toFloat()
                                    || bullet.yPos >= canvas.height.toFloat())
                            && !bullet.isEnd) {
                            bullet.end(context)
                        }
                    }

                    "glow" -> {
                        if(!bullet.isEnd)
                        {
                            for (j in enemies.size - 1 downTo 0) {
                                val enemy = enemies[j]
                                if ( !enemy.isDead && bullet.isLineIntersectingRect(enemy.getBoundingBox())) {
                                    if(!bullet.isEnd)
                                    {
                                        enemies[j].takeDamage(bullet.damage)
                                    }
                                }
                            }
                            bullet.end(context)
                            //Đoạn này cần xử lý va chạm đường thẳng với quái
                        }
                    }
                }



                if (bullet.isDestroy) {
                    if(bullet.bulletName == "negotigate")
                    {
                        flowerJob?.cancel()
                    }
                    bullet.stop()
                    bullets.removeAt(i)  // Loại bỏ phần tử tại chỉ số i
                }
            }
            interactor.move(canvas)
        }
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        setup = false
        canvasWidth = w
        canvasHeight = h
        //spawnEnemy(context, 1)
        //shotBullet(context)
        Log.d("Canva Size", "$w : $h")
        spawnInteractor(context, canvasWidth, canvasHeight)
        spawnBackground(context)
        setup = true
    }

    fun spawnBoss(context: Context)
    {
        val bossEnemy = Enemy("Cockroach", 2f, 20)
        bossEnemy.initial(context)
        val bossObject = EnemyObject(bossEnemy, (width/2).toFloat(), (height/2).toFloat())
        enemies.add(bossObject)
        bossObject.start(context)
    }

    fun stopSpawnEnemy()
    {
        spawnJob?.cancel()
    }


    fun spawnEnemy(context: Context?, level: Int)
    {
        if(context is LifecycleOwner)
        {
            spawnJob = context.lifecycleScope.launch(Dispatchers.Main) {

                val databaseManager = DatabaseManager(context)
                val listEnemy = databaseManager.getEnemiesInLevel(level)

                while (true)
                {
                    val enemySpawn = listEnemy.shuffled().take(1)[0]
                    val randomInt = Random.nextInt(3000, 6000)
                    delay(randomInt.toLong())

// Chọn rìa ngẫu nhiên: 0 = trên, 1 = dưới, 2 = trái, 3 = phải
                    val edge = Random.nextInt(4)

                    val x: Float
                    val y: Float

// Xác định tọa độ dựa trên rìa
                    when (edge) {
                        0 -> { // Trên
                            x = Random.nextInt(0, canvasWidth).toFloat()
                            y = 0f
                        }
                        1 -> { // Dưới
                            x = Random.nextInt(0, canvasWidth).toFloat()
                            y = canvasHeight.toFloat()
                        }
                        2 -> { // Trái
                            x = 0f
                            y = Random.nextInt(0, canvasHeight).toFloat()
                        }
                        else -> { // Phải
                            x = canvasWidth.toFloat()
                            y = Random.nextInt(0, canvasHeight).toFloat()
                        }
                    }

// Tạo enemy tại vị trí đã xác định
                    val enemy = EnemyObject(enemySpawn, x, y)
                    enemies.add(enemy)
                    enemy.start(context)

                }
            }
        }
    }

    fun buff(context: Context, skillClaimed: SkillClaimed)
    {
        if(skillClaimed.skill.name == "accelerate")
        {
            if(skillClaimed.isEnhance)
            {
                if(setup)
                    interactor.isAcceleration = 3f
            }
            else
            {
                if(setup)
                    interactor.isAcceleration = 2f
            }
        }
        else if(context is LifecycleOwner)
        {
            buffJobs.add(
                context.lifecycleScope.launch(Dispatchers.Main) {
                    while (true)
                    {
                        if(skillClaimed.skill.name == "recover")
                        {
                            delay(10000)
                            val dropItem = ItemObject(PointF((width/2).toFloat(), (height/2).toFloat()), Item("health"))
                            items.add(dropItem)
                            dropItem.start(context)
//                            if(skillClaimed.isEnhance)
//                            {
//                                val secondDropItem = ItemObject(PointF((width/2).toFloat() - 200f, (height/2).toFloat()), Item("health"))
//                                items.add(secondDropItem)
//                                secondDropItem.start(context)
//                            }
                        }
                        else break
                    }
                }
            )
        }
    }

    fun shotBullet(context: Context?, skillClaimed: SkillClaimed)
    {
        if(context is LifecycleOwner)
        {
            shotJobs.add(
                context.lifecycleScope.launch(Dispatchers.Main) {
                    val endFrame = skillClaimed.skill.endFrames
                    val frames = skillClaimed.skill.frames
                    while (true)
                    {
                        if(skillClaimed.skill.name != "negotigate")
                        {
                            delay(skillClaimed.skill.coolDown)
                        }
                        else {
                            if(!skillClaimed.isEnhance) delay(20000)
                            else delay(15000)
                        }
                        val nearbyEnemy = findNearestEnemy()
                        if (nearbyEnemy != null) {
                            // Lấy vị trí của kẻ thù
                            val nearbyEnemyPosition = nearbyEnemy.getExactPosition()
                            val targetX = nearbyEnemyPosition.x
                            val targetY = nearbyEnemyPosition.y

                            // Tạo viên đạn nhắm đến kẻ thù
                            val angle = Math.toDegrees(
                                atan2((targetY - interactor.getExactPosition().y.toDouble()), (targetX - interactor.getExactPosition().x.toDouble()))
                            ).toFloat()

                            lateinit var bullet: BulletObject
                            if(skillClaimed.skill.name != "negotigate")
                            {
                                bullet = BulletObject( "interactor", "damage",
                                    1, skillClaimed.skill.name, 1, frames, endFrame,
                                    interactor.getExactPosition().x, interactor.getExactPosition().y, angle
                                )
                            }
                            else
                            {
                                val (randomX, randomY) = getRandomPosition(width, height)
                                bullet = BulletObject("interactor", "summon",
                                    1, skillClaimed.skill.name, 1, frames, endFrame,
                                    randomX, randomY, angle
                                )
                                flowerShot(context, randomX, randomY)
                            }

                            bullets.add(bullet)
                            if(skillClaimed.skill.name == "harm" || skillClaimed.skill.name == "burn") MusicManager.playMusic(context, R.raw.shoot)
                            else if (skillClaimed.skill.name == "glow") MusicManager.playMusic(context, R.raw.laser)

                            bullet.start(context)
                            if(skillClaimed.isEnhance)
                            {
                                if(skillClaimed.skill.name == "harm" || skillClaimed.skill.name == "glow" || skillClaimed.skill.name == "burn")
                                {
                                    val secondBullet = BulletObject( "interactor", "damage",
                                        1, skillClaimed.skill.name, 1, frames, endFrame,
                                        interactor.getExactPosition().x, interactor.getExactPosition().y, angle
                                    )
                                    delay(250)
                                    bullets.add(secondBullet)
                                    if(skillClaimed.skill.name == "harm" || skillClaimed.skill.name == "burn") MusicManager.playMusic(context, R.raw.shoot)
                                    else if (skillClaimed.skill.name == "glow") MusicManager.playMusic(context, R.raw.laser)
                                    secondBullet.start(context)
                                }
                            }

                        }
//                    val bullet = BulletObject(1, "Fire Bullet", frames, endFrame, interactor.xPos, interactor.yPos, 30f )
//                    bullets.add(bullet)
//                    bullet.start(context)
                    }
                }
            )
        }
    }


    fun flowerShot(context: Context, x: Float, y: Float)
    {
        if(context is LifecycleOwner)
        {
            flowerJob = context.lifecycleScope.launch(Dispatchers.Main)
            {
                val skill = Skill(2400, "attack", false, "flower", BitmapFactory.decodeResource(context.resources, R.drawable.sun_1))
                skill.initial(context)
                val endFrame = skill.endFrames
                val frames = skill.frames
                while (true)
                {
                    delay(skill.coolDown)
                    val nearbyEnemy = findNearestEnemyFromFlower(x, y)
                    if (nearbyEnemy != null) {
                        // Lấy vị trí của kẻ thù
                        val targetX = nearbyEnemy.getExactPosition().x
                        val targetY = nearbyEnemy.getExactPosition().y

                        // Tạo viên đạn nhắm đến kẻ thù
                        val angle = Math.toDegrees(
                            atan2((targetY - y.toDouble()), (targetX - x.toDouble()))
                        ).toFloat()

                        val bullet = BulletObject( "interactor", "damage",
                                1, skill.name, 1, frames, endFrame,
                                x, y, angle
                            )


                        bullets.add(bullet)
                        bullet.start(context)
                    }
//                    val bullet = BulletObject(1, "Fire Bullet", frames, endFrame, interactor.xPos, interactor.yPos, 30f )
//                    bullets.add(bullet)
//                    bullet.start(context)
                }
            }
        }
    }



    fun updateHp(change: Int)
    {
        if(hp > 0)
        {
            hp+= change
            if(hp > 5) hp = 5
            for(i in 0 until hpShow.size)
            {
                if(i<hp)
                {
                    hpShow[i].visibility = VISIBLE
                }
                else
                {
                    hpShow[i].visibility = INVISIBLE
                }
            }
        }

    }

    fun checkDead(): Boolean
    {
        if(hp <= 0) return true
        return false
    }

    fun checkWin(): Boolean
    {
        if(hp >0) return false
        return true
    }

    fun clearShotJob()
    {
        for(shot in shotJobs)
        {
            shot.cancel()
        }
        shotJobs.clear()
    }

    fun spawnInteractor(context: Context?, width: Int, height: Int)
    {
        if(context != null)
        {
            val frames = mutableListOf(
//                BitmapFactory.decodeResource(context.resources, R.drawable.the_witch_1),
//                BitmapFactory.decodeResource(context.resources, R.drawable.the_witch_2),
//                BitmapFactory.decodeResource(context.resources, R.drawable.the_witch_3),
//                BitmapFactory.decodeResource(context.resources, R.drawable.the_witch_4),
//                BitmapFactory.decodeResource(context.resources, R.drawable.the_witch_5)

                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.the_witch_1), 257, 508, true),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.the_witch_2), 257, 508, true),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.the_witch_3), 257, 508, true),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.the_witch_4), 257, 508, true),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.the_witch_5), 257, 508, true)
            )
            interactor = InteractionObject(frames)
            interactor.start(context, width, height)
        }
    }

    fun spawnBackground(context: Context?)
    {
        backgroundBitmap = BitmapFactory.decodeResource(context?.resources, R.drawable.game_background1)?.let {
            Bitmap.createScaledBitmap(it, canvasWidth, canvasHeight, true)
        }
    }

    public fun setBackground()
    {
        if(isBackground)
        isBackground = false
        else isBackground = true

    }

    fun moveInteractor(x: Float, y: Float)
    {
        interactor.updatePosition(x, y)
    }

    private fun findNearestEnemy(): EnemyObject? {
        // Kiểm tra danh sách kẻ thù có rỗng không
        if (enemies.isEmpty()) return null

        // Vị trí của interactor

        // Tìm kẻ thù có khoảng cách nhỏ nhất
        val aliveEnemies = enemies.filter { !it.isDead }

        // Kiểm tra danh sách kẻ thù còn sống có rỗng không
        if (aliveEnemies.isEmpty()) return null

        // Tìm kẻ thù gần nhất
        return aliveEnemies.minByOrNull { enemy ->
            val dx = enemy.xPos - interactor.getExactPosition().x
            val dy = enemy.yPos - interactor.getExactPosition().y
            sqrt((dx * dx + dy * dy).toDouble())
        }
    }

    private fun findNearestEnemyFromFlower(x: Float, y: Float): EnemyObject? {
        // Kiểm tra danh sách kẻ thù có rỗng không
        if (enemies.isEmpty()) return null

        // Tìm kẻ thù có khoảng cách nhỏ nhất
        val aliveEnemies = enemies.filter { !it.isDead }

        // Kiểm tra danh sách kẻ thù còn sống có rỗng không
        if (aliveEnemies.isEmpty()) return null

        // Tìm kẻ thù gần nhất
        return aliveEnemies.minByOrNull { enemy ->
            val dx = enemy.xPos - x
            val dy = enemy.yPos - y
            sqrt((dx * dx + dy * dy).toDouble())
        }
    }

    fun getCoin(getCoin: Int)
    {
        coin += getCoin
        coinShow?.setText(coin.toString())
    }

    fun ultimating(context: Context)
    {
        if(context is LifecycleOwner)
        {
            ultimateJob?.cancel()
            ultimateJob = context.lifecycleScope.launch(Dispatchers.Main) {
                while (ultimateObject != null)
                {
                    delay(1000)
                    for(e in enemies)
                    {
                        if(ultimateObject?.let { RectF.intersects(e.getBoundingBox(), it.getBoundingBox()) } == true)
                        {
                            e.takeDamage(1)
                        }
                    }
                    updateHp(1)
                }

            }
        }

    }







}

fun getRandomPosition(width: Int, height: Int): Pair<Float, Float> {
    // Sinh tọa độ ngẫu nhiên trong phạm vi [0, width) và [0, height)
    val x = Random.nextInt(0, width).toFloat()
    val y = Random.nextInt(0, height).toFloat()

    return Pair(x, y)
}
