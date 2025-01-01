package com.example.theworldrequiem.Data

class SkillClaimed(pSkill: Skill) {
    var skill: Skill = pSkill
    var isEnhance: Boolean = false

    fun enhanceSkill() {
        isEnhance = true
    }
}