package com.example.theworldrequiem.Database
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "thehouse.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_USER = "user"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_ONLINE = "online"
        const val COLUMN_IDENTITY = "identity"
        const val COLUMN_LEVEL = "level"
        const val COLUMN_COIN = "coin"
        const val COLUMN_SOUND = "sound"


        const val TABLE_SKILL_IN_BAG = "skillinbag"
        const val COLUMN_SKILL_NAME = "name"
        const val COLUMN_SKILL_TYPE = "type"

        const val TABLE_ENEMYINLEVEL = "enemyinlevel"
        const val COLUMN_ISBOSS = "boss"
        const val COLUMN_ENEMYHP = "hp"
        const val COLUMN_ENEMYSPEED = "speed"

        const val TABLE_LEVEL = "level"
        const val COLUMN_BACKGROUND = "background"
        const val COLUMN_TIME = "time"
        const val COLUMN_BOSS = "boss"


    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = """
            CREATE TABLE $TABLE_USER (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_ONLINE INTEGER NOT NULL,
                $COLUMN_IDENTITY INTEGER NOT NULL,
                $COLUMN_LEVEL INTEGER NOT NULL,
                $COLUMN_COIN INTEGER NOT NULL,
                $COLUMN_SOUND FLOAT NOT NULL
            )
        """.trimIndent()
        db?.execSQL(createTable)

        val createTableSkillInBag = """
            CREATE TABLE $TABLE_SKILL_IN_BAG (
                $COLUMN_SKILL_NAME TEXT PRIMARY KEY
            )
        """.trimIndent()
        db?.execSQL(createTableSkillInBag)

        val createEnemiesInLevelTable = """
            CREATE TABLE $TABLE_ENEMYINLEVEL (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_LEVEL INTEGER NOT NULL,
                $COLUMN_ENEMYHP INTEGER NOT NULL,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_BOSS INTEGER NOT NULL,
                $COLUMN_ENEMYSPEED INTEGER NOT NULL
            )
        """.trimIndent()
        db?.execSQL(createEnemiesInLevelTable)

        val createLevelTable = """
            CREATE TABLE $TABLE_LEVEL (
                $COLUMN_LEVEL INTEGER PRIMARY KEY,
                $COLUMN_BACKGROUND TEXT NOT NULL,
                $COLUMN_TIME INTEGER NOT NULL,
                $COLUMN_ISBOSS INTEGER NOT NULL
            )
        """.trimIndent()
        db?.execSQL(createLevelTable)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USER")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_ENEMYINLEVEL")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_LEVEL")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_SKILL_IN_BAG")


        onCreate(db)
    }
}
