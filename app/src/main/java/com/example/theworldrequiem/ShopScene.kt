package com.example.theworldrequiem

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.theworldrequiem.Data.Skill
import com.example.theworldrequiem.Database.DatabaseManager
import com.example.theworldrequiem.databinding.ActivityShopSceneBinding

class ShopScene : AppCompatActivity() {
    lateinit var binding: ActivityShopSceneBinding
    var skillInShop: MutableList<SkillWithPrice> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityShopSceneBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.shopBack.setOnClickListener{
            finish()
        }
        val databaseManager = DatabaseManager(this)
        binding.shopCoinShow.text = databaseManager.getCoin().toString()

        skillInShop = mutableListOf(
            SkillWithPrice("glow", 5000, binding.shopItem1, binding.shopItem1Price),
            SkillWithPrice("accelerate", 500, binding.shopItem2, binding.shopItem2Price),
            SkillWithPrice("negotigate", 5000, binding.shopItem3, binding.shopItem3Price),
            SkillWithPrice("burn", 1000, binding.shopItem4, binding.shopItem4Price)
            )

        for(skill in skillInShop)
        {
            if(checkSkillHad(skill.pSkillName)) skill.pLayoutView.visibility = View.GONE
            else
            {
                skill.coinView.text = skill.pPrice.toString()
                skill.pLayoutView.setOnClickListener {
                    if(databaseManager.getCoin()!! < skill.pPrice)
                    {
                        Toast.makeText(this, "Not enough coin!! Please play more!", Toast.LENGTH_SHORT).show()
                    }
                    else
                    {
                        Toast.makeText(this, "Bought Skill Successfully", Toast.LENGTH_SHORT).show()
                        databaseManager.insertSkill(skill.pSkillName)
                        refreshActivity()
                    }
                }
            }
        }


    }

    fun checkSkillHad(pSkillName: String): Boolean
    {
        val databaseManager = DatabaseManager(this)
        for(skill in databaseManager.getAllSkillInBag())
        {
            if(skill == pSkillName) return true
        }
        return false
    }

    @SuppressLint("UnsafeIntentLaunch")
    fun refreshActivity() {
        val intent = intent
        finish()
        startActivity(intent)
    }
}

data class SkillWithPrice(val pSkillName: String, val pPrice: Int, val pLayoutView: LinearLayout, val coinView: TextView)