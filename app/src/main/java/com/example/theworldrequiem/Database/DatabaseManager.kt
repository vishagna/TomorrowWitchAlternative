package com.example.theworldrequiem.Database
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.util.Log
import com.example.theworldrequiem.Data.Enemy
import com.example.theworldrequiem.Data.Level
import com.example.theworldrequiem.Data.SkillInBag

class DatabaseManager(pContext: Context) {
    private val dbHelper = DatabaseHelper(pContext)
    private val db = dbHelper.writableDatabase
    val context = pContext

    fun insertUser(name: String): Long {
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_ONLINE, 0)
            put(DatabaseHelper.COLUMN_NAME, name)
            put(DatabaseHelper.COLUMN_IDENTITY, 0)
            put(DatabaseHelper.COLUMN_LEVEL, 0)
            put(DatabaseHelper.COLUMN_COIN, 0)
            put(DatabaseHelper.COLUMN_SOUND, 0.5f)
        }
        return db.insert(DatabaseHelper.TABLE_USER, null, values)
    }

    fun insertSkillInBag(name: String): Long {
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_SKILL_NAME, name)
        }
        return db.insert(DatabaseHelper.TABLE_SKILL_IN_BAG, null, values)
    }

    fun getSound(): Float?
    {
        val query = "SELECT ${DatabaseHelper.COLUMN_SOUND} FROM ${DatabaseHelper.TABLE_USER} WHERE ${DatabaseHelper.COLUMN_IDENTITY} = ? LIMIT 1"
        val cursor: Cursor = db.rawQuery(query, arrayOf(0.toString()))
        var sound: Float? = null

        if (cursor.moveToFirst()) {
            sound = cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SOUND))
        }
        cursor.close()
        return sound
    }

    fun updateSound(sound: Float)
    {
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_SOUND, sound)
        }
        db.update(DatabaseHelper.TABLE_USER, values, "${DatabaseHelper.COLUMN_IDENTITY}=?", arrayOf(0.toString()))
    }

    fun isUserExists(): Boolean {
        val query = "SELECT 1 FROM ${DatabaseHelper.TABLE_USER} WHERE ${DatabaseHelper.COLUMN_IDENTITY} = ? LIMIT 1"
        val cursor: Cursor = db.rawQuery(query, arrayOf(0.toString()))
        val exists = cursor.moveToFirst()
        cursor.close()
        return exists
    }

    fun getUserName(): String? {
        val query = "SELECT ${DatabaseHelper.COLUMN_NAME} FROM ${DatabaseHelper.TABLE_USER} WHERE ${DatabaseHelper.COLUMN_IDENTITY} = ? LIMIT 1"
        val cursor: Cursor = db.rawQuery(query, arrayOf(0.toString()))
        var userName: String? = null

        if (cursor.moveToFirst()) {
            userName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME))
        }
        cursor.close()
        return userName
    }


    fun updateUser( online: Int): Int {
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_ONLINE, online)
        }
        return db.update(DatabaseHelper.TABLE_USER, values, "${DatabaseHelper.COLUMN_IDENTITY}=?", arrayOf(0.toString()))
    }

    fun getCoin(): Int?
    {
        val query = "SELECT ${DatabaseHelper.COLUMN_COIN} FROM ${DatabaseHelper.TABLE_USER} WHERE ${DatabaseHelper.COLUMN_IDENTITY} = ? LIMIT 1"
        val cursor: Cursor = db.rawQuery(query, arrayOf(0.toString()))
        var coin: Int? = null

        if (cursor.moveToFirst()) {
            coin = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_COIN))
        }
        cursor.close()
        return coin
    }

    fun getMaxLevel(): Int?
    {
        val query = "SELECT ${DatabaseHelper.COLUMN_LEVEL} FROM ${DatabaseHelper.TABLE_USER} WHERE ${DatabaseHelper.COLUMN_IDENTITY} = ? LIMIT 1"
        val cursor: Cursor = db.rawQuery(query, arrayOf(0.toString()))
        var level: Int? = null

        if (cursor.moveToFirst()) {
            level = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LEVEL))
        }
        cursor.close()
        Log.e("LEvel", "$level")
        return level
    }

    fun updateCoin(pCoin: Int)
    {
        var coin = getCoin()
        coin?.let {
            coin = coin?.plus(pCoin)
            val values = ContentValues().apply {
                put(DatabaseHelper.COLUMN_COIN, coin)
            }
            db.update(DatabaseHelper.TABLE_USER, values, "${DatabaseHelper.COLUMN_IDENTITY}=?", arrayOf(0.toString()))
        }
    }

    fun insertEnemy(pLevel: Int, pHp: Int, pName: String, pEnemySpeed: Int) : Long
    {
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_LEVEL, pLevel)
            put(DatabaseHelper.COLUMN_ENEMYHP, pHp)
            put(DatabaseHelper.COLUMN_NAME, pName)
            put(DatabaseHelper.COLUMN_ENEMYSPEED, pEnemySpeed)
            put(DatabaseHelper.COLUMN_ISBOSS, 0)
        }
        return db.insert(DatabaseHelper.TABLE_ENEMYINLEVEL, null, values)
    }

    fun insertSkill(pSkillName: String): Long {
        // Kiểm tra xem skill đã tồn tại chưa
        val query = "SELECT COUNT(*) FROM ${DatabaseHelper.TABLE_SKILL_IN_BAG} WHERE ${DatabaseHelper.COLUMN_SKILL_NAME} = ?"
        val cursor = db.rawQuery(query, arrayOf(pSkillName))
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()

        // Nếu skill đã tồn tại, trả về -1
        if (count > 0) {
            return -1L
        }

        // Nếu skill chưa tồn tại, tiến hành chèn
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_SKILL_NAME, pSkillName)
        }
        return db.insert(DatabaseHelper.TABLE_SKILL_IN_BAG, null, values)
    }

    fun getAllSkillInBag(): List<String>
    {
        val skillInBag = mutableListOf<String>()
        val query = "SELECT * FROM ${DatabaseHelper.TABLE_SKILL_IN_BAG}"
        val cursor: Cursor = db.rawQuery(query,null)

        if (cursor.moveToFirst()) {
            do {
                val skillName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SKILL_NAME))
                skillInBag.add(skillName)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return skillInBag
    }

    fun insertLevel(pLevel: Int, pBackground: String, pTime: Int, pBoss: Int): Long
    {
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_LEVEL, pLevel)
            put(DatabaseHelper.COLUMN_BACKGROUND, pBackground)
            put(DatabaseHelper.COLUMN_TIME, pTime)
            put(DatabaseHelper.COLUMN_ISBOSS, 0)
        }
        val values2 = ContentValues().apply {
            put(DatabaseHelper.COLUMN_LEVEL, pLevel)
        }
        db.update(DatabaseHelper.TABLE_USER, values2, "${DatabaseHelper.COLUMN_IDENTITY}=?", arrayOf(0.toString()))
        return db.insert(DatabaseHelper.TABLE_LEVEL, null, values)
    }

    fun getEnemiesInLevel(level: Int): List<Enemy> {
        val enemies = mutableListOf<Enemy>()
        val query = "SELECT * FROM ${DatabaseHelper.TABLE_ENEMYINLEVEL} WHERE ${DatabaseHelper.COLUMN_LEVEL} = ?"
        val cursor: Cursor = db.rawQuery(query, arrayOf(level.toString()))

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME))
                val hp = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ENEMYHP))
                val isBoss = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ISBOSS)) == 1
                val speed = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ENEMYSPEED))
                val enemy = Enemy(name, speed.toFloat(), hp)
                enemy.initial(context)
                enemies.add(enemy)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return enemies
    }

    fun getLevelInformation(level: Int): Level? {
        val query = "SELECT * FROM ${DatabaseHelper.TABLE_LEVEL} WHERE ${DatabaseHelper.COLUMN_LEVEL} = ?"
        val cursor: Cursor = db.rawQuery(query, arrayOf(level.toString()))
        var levelInfo: Level? = null

        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID))
            val background = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BACKGROUND))
            val time = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TIME))
            val isBoss = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ISBOSS)) == 1
            levelInfo = Level(level, background, time, isBoss)
        }
        cursor.close()
        return levelInfo
    }





}

data class User(
    val id: Int,
    val name: String,
    val online: Int,
    val identity: Int
)
