package com.example.helloandroid

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

// 11주차: 액티비티와 인텐트 - 사용자 상세 정보
class UserDetailActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var currentUserId: String
    private lateinit var targetUserId: String
    private lateinit var targetUser: User

    private lateinit var textDetailName: TextView
    private lateinit var textDetailReceivedInterests: TextView
    private lateinit var textDetailRole: TextView
    private lateinit var textDetailSkills: TextView
    private lateinit var textDetailExperience: TextView
    private lateinit var textDetailStrength: TextView
    private lateinit var textDetailInterests: TextView
    private lateinit var textDetailPreferredTeammate: TextView
    private lateinit var textDetailCollaborationStyle: TextView
    private lateinit var textDetailGithub: TextView
    private lateinit var textDetailContact: TextView
    private lateinit var btnToggleInterest: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_detail)

        dbHelper = DBHelper(this)
        currentUserId = intent.getStringExtra("currentUserId") ?: run {
            finish()
            return
        }
        targetUserId = intent.getStringExtra("targetUserId") ?: run {
            finish()
            return
        }

        initViews()
        loadUserDetail()
        setupInterestButton()
    }

    private fun initViews() {
        textDetailName = findViewById(R.id.textDetailName)
        textDetailReceivedInterests = findViewById(R.id.textDetailReceivedInterests)
        textDetailRole = findViewById(R.id.textDetailRole)
        textDetailSkills = findViewById(R.id.textDetailSkills)
        textDetailExperience = findViewById(R.id.textDetailExperience)
        textDetailStrength = findViewById(R.id.textDetailStrength)
        textDetailInterests = findViewById(R.id.textDetailInterests)
        textDetailPreferredTeammate = findViewById(R.id.textDetailPreferredTeammate)
        textDetailCollaborationStyle = findViewById(R.id.textDetailCollaborationStyle)
        textDetailGithub = findViewById(R.id.textDetailGithub)
        textDetailContact = findViewById(R.id.textDetailContact)
        btnToggleInterest = findViewById(R.id.btnToggleInterest)
    }

    private fun loadUserDetail() {
        targetUser = dbHelper.getUser(targetUserId) ?: run {
            Toast.makeText(this, "사용자 정보를 찾을 수 없습니다", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        textDetailName.text = targetUser.name
        textDetailReceivedInterests.text = "받은 관심: ${targetUser.receivedInterests}개"
        textDetailRole.text = targetUser.role.ifEmpty { "미정" }
        textDetailSkills.text = targetUser.skills.ifEmpty { "미정" }
        textDetailExperience.text = targetUser.experience.ifEmpty { "미정" }
        textDetailStrength.text = targetUser.strength.ifEmpty { "미정" }
        textDetailInterests.text = targetUser.interests.ifEmpty { "미정" }
        textDetailPreferredTeammate.text = targetUser.preferredTeammate.ifEmpty { "미정" }
        textDetailCollaborationStyle.text = targetUser.collaborationStyle.ifEmpty { "미정" }
        textDetailGithub.text = targetUser.github.ifEmpty { "미정" }
        textDetailContact.text = targetUser.contact.ifEmpty { "미정" }

        updateInterestButton()
    }

    private fun setupInterestButton() {
        btnToggleInterest.setOnClickListener {
            toggleInterest()
        }
    }

    private fun toggleInterest() {
        val isInterested = dbHelper.isInterestExists(currentUserId, targetUserId)

        if (isInterested) {
            if (dbHelper.deleteInterest(currentUserId, targetUserId)) {
                Toast.makeText(this, "관심 표시를 취소했습니다", Toast.LENGTH_SHORT).show()
                updateInterestButton()
                loadUserDetail()  // 받은 관심 수 업데이트
            }
        } else {
            if (dbHelper.saveInterest(currentUserId, targetUserId)) {
                Toast.makeText(this, "관심 표시했습니다", Toast.LENGTH_SHORT).show()
                updateInterestButton()
                loadUserDetail()  // 받은 관심 수 업데이트
            }
        }
    }

    private fun updateInterestButton() {
        val isInterested = dbHelper.isInterestExists(currentUserId, targetUserId)
        if (isInterested) {
            btnToggleInterest.text = "❤️ 관심 표시 취소"
            btnToggleInterest.setBackgroundColor(android.graphics.Color.parseColor("#FF5722"))
        } else {
            btnToggleInterest.text = "🤍 관심 표시하기"
            btnToggleInterest.setBackgroundColor(android.graphics.Color.parseColor("#4CAF50"))
        }
    }
}