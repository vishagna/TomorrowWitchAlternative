package com.example.theworldrequiem.Controllers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.theworldrequiem.Data.Skill
import com.example.theworldrequiem.Data.SkillClaimed
import com.example.theworldrequiem.ExtendView.AnimationObjectView
import com.example.theworldrequiem.GameObject.EnemyObject
import com.example.theworldrequiem.R
import com.example.theworldrequiem.databinding.ActivityBodyCameraBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SkillClaim (pStartSkill: String, pBinding: ActivityBodyCameraBinding, pContext: Context) {

    val binding = pBinding
    val context = pContext

    val startSkill = pStartSkill
    var skillShow: MutableList<ImageView> = mutableListOf()
    var frameSkillShow: MutableList<ImageView> = mutableListOf()
    var ultimateFrameSkillShow: AnimationObjectView? = null

    private var vertical: MutableList<Bitmap> = mutableListOf()
    private var horizontal: MutableList<Bitmap> = mutableListOf()
    private var plus: MutableList<Bitmap> = mutableListOf()
    private var skillClaimJob: Job? = null

    private var skillImages: MutableList<Bitmap> = mutableListOf()
    private var skillList: MutableList<Skill> = mutableListOf()

    var skillCanClaim: MutableList<Skill> = mutableListOf()
    var skillClaimed: MutableList<SkillClaimed> = mutableListOf()

    var isUltimateState = false
    var isSelectState = false
    var selectState = "none"
    var ultimateState = "none"

    fun onCreate(context: Context, binding: ActivityBodyCameraBinding)
    {
        binding.ultimateFrame.stopAnimation()
        skillShow = mutableListOf(
            binding.skill1Show,
            binding.skill2Show,
            binding.skill3Show,
            binding.skill4Show
        )
        frameSkillShow = mutableListOf(
            binding.frame1Show,
            binding.frame2Show,
            binding.frame3Show,
            binding.frame4Show
        )
        ultimateFrameSkillShow = binding.ultimateFrame


        vertical = mutableListOf(
            BitmapFactory.decodeResource(context.resources, R.drawable.vertical_1),
            BitmapFactory.decodeResource(context.resources, R.drawable.vertical_2),
            BitmapFactory.decodeResource(context.resources, R.drawable.vertical_3),
            BitmapFactory.decodeResource(context.resources, R.drawable.vertical_4),
            BitmapFactory.decodeResource(context.resources, R.drawable.vertical_5)
            )

        horizontal = mutableListOf(
            BitmapFactory.decodeResource(context.resources, R.drawable.horizontal_1),
            BitmapFactory.decodeResource(context.resources, R.drawable.horizontal_2),
            BitmapFactory.decodeResource(context.resources, R.drawable.horizontal_3),
            BitmapFactory.decodeResource(context.resources, R.drawable.horizontal_4),
            BitmapFactory.decodeResource(context.resources, R.drawable.horizontal_5)
        )

        plus = mutableListOf(
            BitmapFactory.decodeResource(context.resources, R.drawable.vertical_1),
            BitmapFactory.decodeResource(context.resources, R.drawable.vertical_2),
            BitmapFactory.decodeResource(context.resources, R.drawable.vertical_3),
            BitmapFactory.decodeResource(context.resources, R.drawable.vertical_4),
            BitmapFactory.decodeResource(context.resources, R.drawable.vertical_5),
            BitmapFactory.decodeResource(context.resources, R.drawable.plus_1),
            BitmapFactory.decodeResource(context.resources, R.drawable.plus_2),
            BitmapFactory.decodeResource(context.resources, R.drawable.plus_3),
            BitmapFactory.decodeResource(context.resources, R.drawable.plus_4),
            BitmapFactory.decodeResource(context.resources, R.drawable.plus_5)
            )

        skillList = mutableListOf(
            Skill(3500,"attack",true,"glow", BitmapFactory.decodeResource(context.resources, R.drawable.glow)),
            Skill(2000,"attack",true,"harm", BitmapFactory.decodeResource(context.resources, R.drawable.harm)),
            Skill(5000,"attack",true,"burn", BitmapFactory.decodeResource(context.resources, R.drawable.burn)),
            Skill(0,"buff",true,"accelerate", BitmapFactory.decodeResource(context.resources, R.drawable.accelerate)),
            //Skill(15000,"buff",true,"protection", BitmapFactory.decodeResource(context.resources, R.drawable.protection)),
            Skill(15000,"attack",true,"negotigate", BitmapFactory.decodeResource(context.resources, R.drawable.negotigate)),
            //Skill(5000,"buff",true,"purify", BitmapFactory.decodeResource(context.resources, R.drawable.purify)),
            Skill(10000,"buff",true,"recover", BitmapFactory.decodeResource(context.resources, R.drawable.recover)),
            Skill(1500,"buff",true,"enhance", BitmapFactory.decodeResource(context.resources, R.drawable.enhance))
            )


        for (skill in skillList)
        {
            skill.initial(context)
            if(startSkill == skill.name)
            {
                claimSkill(skill)
//                claimSkill(skill)

//                claimSkill(skillList[0])
//                claimSkill(skillList[2])
//                claimSkill(skillList[5])
            }
        }





        binding.vertical.isVisible = false
        binding.horizontal.isVisible = false
        binding.plus.isVisible = false
        binding.skillClaimer.isVisible = false

        binding.gameScene.skillClaim = this
    }

    fun checkSkillClaimed(pSkill: Skill): Boolean
    {
        for(skill in skillClaimed)
        {
            if(skill.skill.name == pSkill.name)
                return true
        }
        return false
    }

    fun claimSkill(pSkill: Skill)
    {
        if (skillClaimed.size >= 4) {
            for(s in skillList.size -1 downTo 0) {
                if (!checkSkillClaimed(skillList[s])) {
                    skillList.removeAt(s)
                }
            }
        }

        var isEnhance = false
        for(index in 0 until skillClaimed.size)
        {
            if(skillClaimed.size >= 4)
            {
                if(!checkSkillClaimed(skillClaimed[index].skill))
                {
                    for(s in skillList.size -1 downTo 0) {
                        if(skillList[s].name == skillClaimed[index].skill.name)
                        {
                            skillList.removeAt(s)
                        }
                    }
                }
            }

            if(skillClaimed[index].skill.name == pSkill.name)
            {
                enhanceSkill(index)
                isEnhance = true
                for(s in skillList.size -1 downTo 0) {
                    if(skillList[s].name == pSkill.name)
                    {
                        skillList.removeAt(s)
                    }
                }
                break
            }
        }
        if(!isEnhance)
        {
            if(pSkill.name == "enhance")
            {
                for(e in 0 until skillClaimed.size)
                {
                    if(!skillClaimed[e].isEnhance) claimSkill(pSkill)
                    break
                }
            }
            else
            {
                skillClaimed.add(SkillClaimed(pSkill))
            }
        }

        for(i in 0 until skillShow.size)
        {
            if(i < skillClaimed.size)
            {
                skillShow[i].setImageBitmap(skillClaimed[i].skill.image)
                skillShow[i].visibility = View.VISIBLE
            }
            else
            {
                skillShow[i].visibility = View.INVISIBLE
            }
        }

        binding.gameScene.clearShotJob()
        for(skill in skillClaimed)
        {
            if(skill.skill.type == "attack")
            {
                binding.gameScene.shotBullet(context, skill)
            }
            else if(skill.skill.type == "buff")
            {
                binding.gameScene.buff(context, skill)
            }
        }
    }

    fun castingSelectState()
    {
        if (selectState == "vertical")
        {
            claimSkill(skillCanClaim[0])
            isSelectState = false
            selectState = "none"
            binding.skillClaimer.visibility = View.GONE
        }
        else if(selectState == "horizontal")
        {
            claimSkill(skillCanClaim[1])
            isSelectState = false
            selectState = "none"
            binding.skillClaimer.visibility = View.GONE
        }
        else if(selectState == "plus")
        {
            claimSkill(skillCanClaim[2])
            isSelectState = false
            selectState = "none"
            binding.skillClaimer.visibility = View.GONE
        }
    }

    fun startUltimate()
    {
        isUltimateState = true
        binding.ultimateFrame.castAnimation()
    }

    fun castingUltimateState()
    {
            if(ultimateState == "holy_circle")
            {
                binding.gameScene.castUltimate(ultimateState)
                isUltimateState = false
                binding.ultimateFrame.stopAnimation()
            }
    }

    fun updateSelectState(pSelectState: String)
    {
        selectState = pSelectState
    }

    fun enhanceSkill(index: Int)
    {
        if(index < skillClaimed.size)
        {
            skillClaimed[index].enhanceSkill()
            frameSkillShow[index].setImageBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.enhanced_skill))
        }
    }


    fun startClaim(context: Context)
    {
        selectState = "none"
        binding.skillClaimer.visibility = View.VISIBLE
        skillCanClaim = skillList.shuffled().take(3).toMutableList()
        while (skillCanClaim.size < 3)
        {
            skillCanClaim.add(Skill(1500,"buff",true,"enhance", BitmapFactory.decodeResource(context.resources, R.drawable.enhance)))
        }
        skillClaimJob?.cancel()
        if (context is LifecycleOwner) {
            skillClaimJob = context.lifecycleScope.launch(Dispatchers.Main) {
                for(i in 0 until vertical.size)
                {
                    binding.vertical.isVisible = true
                    binding.vertical.setImageBitmap(vertical[i])
                    delay(100)
                }
                binding.vertical.setImageBitmap(skillCanClaim[0].image)

                for(i in 0 until horizontal.size)
                {
                    binding.horizontal.isVisible = true
                    binding.horizontal.setImageBitmap(horizontal[i])
                    delay(100)
                }
                binding.horizontal.setImageBitmap(skillCanClaim[1].image)

                for(i in 0 until plus.size)
                {
                    binding.plus.isVisible = true
                    binding.plus.setImageBitmap(plus[i])
                    delay(100)
                }
                binding.plus.setImageBitmap(skillCanClaim[2].image)
                isSelectState = true
            }
        }
    }
}
