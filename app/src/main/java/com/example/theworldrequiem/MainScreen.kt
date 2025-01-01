package com.example.theworldrequiem

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import com.example.theworldrequiem.Data.Skill
import com.example.theworldrequiem.Database.DatabaseManager
import com.example.theworldrequiem.databinding.ActivityMainScreenBinding
import com.example.theworldrequiem.global.MusicManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainScreen : AppCompatActivity() {

    private var startButtonFrames: MutableList<Bitmap> = mutableListOf()

    private lateinit var binding: ActivityMainScreenBinding

    private var startJob: Job? = null
    private var initialJob: Job? = null
    private var initialLeveljob: Job? = null

    private var skillList: MutableList<Skill> = mutableListOf()
    private var skillIndex = 0
    private var levelIndex = 1

    private val requestCameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (!isGranted) {
                // Hiển thị thông báo khi từ chối quyền
                Toast.makeText(
                    this,
                    "Camera permission is required to proceed. Please enable it in settings.",
                    Toast.LENGTH_LONG
                ).show()
                finish() // Đóng app nếu quyền bị từ chối
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()




        binding = ActivityMainScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        startJob = this.lifecycleScope.launch {
            startGame()
        }

        startButtonFrames = mutableListOf(
            BitmapFactory.decodeResource(this.resources, R.drawable.start_button_1),
            BitmapFactory.decodeResource(this.resources, R.drawable.start_button_2),
            BitmapFactory.decodeResource(this.resources, R.drawable.start_button_3)
        )


        binding.confirmName.setOnClickListener {
            if(binding.inputName.text.toString() == "")
            {
                Toast.makeText(this, "Name required!!!", Toast.LENGTH_SHORT).show()
            }
            else
            {
                if(initialJob?.isCompleted == true || initialJob == null)
                {
                    val context = this
                    initialJob = this.lifecycleScope.launch {
                        val databaseManager = DatabaseManager(context)
                        val userExists = databaseManager.isUserExists()

                        if(!userExists)
                        {
                            databaseManager.insertUser(binding.inputName.text.toString())
                            initialEnemyData()
                        }
                        refreshActivity()
                    }
                }
            }
        }

        binding.shopNavigate.setOnClickListener {
            val intent = Intent(this, ShopScene::class.java)
            startActivity(intent)
        }

        binding.settingsNavigate.setOnClickListener {
            val intent = Intent(this, SettingsScene::class.java)
            startActivity(intent)
        }

        binding.startButton.updateAnimation(startButtonFrames, 0.3f)
        binding.startButton.setOnClickListener {
            val intent = Intent(this, BodyCamera::class.java)
            intent.putExtra("level", levelIndex)
            intent.putExtra("startskill", skillList[skillIndex].name)
            startActivity(intent)
            finish()
        }

    }

    fun startGame()
    {
        val databaseManager = DatabaseManager(this)
        val userExists = databaseManager.isUserExists()

        if(!userExists)
        {
            binding.startButton.visibility = View.GONE
            binding.inputName.visibility = View.VISIBLE
            binding.confirmName.visibility = View.VISIBLE
            binding.tableTool.visibility = View.GONE
        }
        else
        {

            databaseManager.insertSkill("harm")
            databaseManager.insertSkill("recover")

            skillList = mutableListOf(
                Skill(3500,"attack",true,"glow", BitmapFactory.decodeResource(this.resources, R.drawable.glow)),
                Skill(2000,"attack",true,"harm", BitmapFactory.decodeResource(this.resources, R.drawable.harm)),
                Skill(5000,"attack",true,"burn", BitmapFactory.decodeResource(this.resources, R.drawable.burn)),
                Skill(0,"buff",true,"accelerate", BitmapFactory.decodeResource(this.resources, R.drawable.accelerate)),
                //Skill(15000,"buff",true,"protection", BitmapFactory.decodeResource(context.resources, R.drawable.protection)),
                Skill(15000,"attack",true,"negotigate", BitmapFactory.decodeResource(this.resources, R.drawable.negotigate)),
                //Skill(5000,"buff",true,"purify", BitmapFactory.decodeResource(context.resources, R.drawable.purify)),
                Skill(10000,"buff",true,"recover", BitmapFactory.decodeResource(this.resources, R.drawable.recover)),
            )
            for(i in skillList.size-1 downTo 0)
            {
                if(!checkSkillClaimed(skillList[i].name))
                    skillList.removeAt(i)
            }
            binding.skillChoose.setImageBitmap(skillList[skillIndex].image)

            binding.switchSkill.setOnClickListener {
                skillIndex += 1
                if(skillIndex >= skillList.size) skillIndex = 0
                binding.skillChoose.setImageBitmap(skillList[skillIndex].image)
            }

            levelIndex = databaseManager.getMaxLevel()!!
            binding.levelSelect.text = levelIndex.toString()
            binding.minusLevel.setOnClickListener {
                levelIndex -= 1
                if(levelIndex <= 0) levelIndex = 1
                binding.levelSelect.text = levelIndex.toString()
            }
            binding.plusLevel.setOnClickListener {
                levelIndex += 1
                if (levelIndex > databaseManager.getMaxLevel()!!) levelIndex = databaseManager.getMaxLevel()!!
                binding.levelSelect.text = levelIndex.toString()
            }

            MusicManager.playBackgroundMusic(this, R.raw.game_background, true)
            databaseManager.getSound()?.let {
                MusicManager.setNewVolume(it)
            }

            val coinText = databaseManager.getCoin()
            coinText?.let {
                binding.coinText.text = coinText.toString()
            }
            val userName = databaseManager.getUserName()
            binding.nameShow.setText(userName)
            binding.startButton.visibility = View.VISIBLE
            binding.inputName.visibility = View.GONE
            binding.confirmName.visibility = View.GONE
            binding.tableTool.visibility = View.VISIBLE
        }
    }

    @SuppressLint("UnsafeIntentLaunch")
    fun refreshActivity() {
        val intent = intent
        finish()
        startActivity(intent)
    }

    fun initialEnemyData()
    {
        val databaseManager = DatabaseManager(this)
        databaseManager.insertLevel(1,"what", 0, 0)
        databaseManager.insertEnemy(1, 1,"Lizard", 1)
        databaseManager.insertEnemy(1,1, "Spider", 1)
        databaseManager.insertEnemy(1,1, "Scorpion", 1)
        databaseManager.insertEnemy(1,1, "Ant", 2)

        databaseManager.insertLevel(2,"what", 0, 0)
        databaseManager.insertEnemy(2,1, "Spider", 1)
        databaseManager.insertEnemy(2,1, "Scorpion", 1)
        databaseManager.insertEnemy(2,1, "Ant", 2)

    }

    fun checkSkillClaimed(pSkillName: String): Boolean
    {
        val databaseManager = DatabaseManager(this)
        for(skill in databaseManager.getAllSkillInBag())
        {
            if (pSkillName == skill) return true
        }
        return false
    }



}

data class SkillShow(val skillName: String, val skillImage: Bitmap)
