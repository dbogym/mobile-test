package com.example.helloandroid

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class ProjectDetailActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var currentUserId: String
    private var currentProjectId: Int = -1
    private var currentProject: Project? = null

    private lateinit var textProjectTitle: TextView
    private lateinit var textProjectCreator: TextView
    private lateinit var textProjectStatus: TextView
    private lateinit var textProjectMembers: TextView
    private lateinit var textProjectDuration: TextView
    private lateinit var textProjectDescription: TextView
    private lateinit var textProjectRoles: TextView
    private lateinit var textProjectSkills: TextView
    private lateinit var textMembersList: TextView
    private lateinit var btnApply: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_detail)

        dbHelper = DBHelper(this)
        currentUserId = intent.getStringExtra("userId") ?: run {
            finish()
            return
        }

        currentProjectId = intent.getIntExtra("projectId", -1)
        if (currentProjectId == -1) {
            finish()
            return
        }

        initViews()
        loadProjectDetail()
    }

    private fun initViews() {
        textProjectTitle = findViewById(R.id.textTitle)
        textProjectCreator = findViewById(R.id.textCreator)
        textProjectStatus = findViewById(R.id.textStatus)
        textProjectMembers = findViewById(R.id.textMembers)
        textProjectDuration = findViewById(R.id.textDuration)
        textProjectDescription = findViewById(R.id.textDescription)
        textProjectRoles = findViewById(R.id.textRoles)
        textProjectSkills = findViewById(R.id.textSkills)
        textMembersList = findViewById(R.id.textMembersList)
        btnApply = findViewById(R.id.btnApply)

        btnApply.setOnClickListener {
            showApplyDialog()
        }
    }

    private fun loadProjectDetail() {
        currentProject = dbHelper.getProject(currentProjectId)
        val project = currentProject ?: run {
            Toast.makeText(this, "프로젝트를 찾을 수 없습니다", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val creator = dbHelper.getUser(project.creatorId)

        textProjectTitle.text = project.title
        textProjectCreator.text = "작성자: ${creator?.name ?: "알 수 없음"}"
        textProjectMembers.text = "${project.currentMembers}/${project.maxMembers}명"
        textProjectDuration.text = project.duration
        textProjectDescription.text = project.description
        textProjectRoles.text = project.requiredRoles
        textProjectSkills.text = project.requiredSkills.ifEmpty { "없음" }

        when (project.status) {
            "recruiting" -> {
                textProjectStatus.text = "모집중"
                textProjectStatus.setBackgroundColor(android.graphics.Color.parseColor("#4CAF50"))
                textProjectStatus.setTextColor(android.graphics.Color.WHITE)
            }
            "closed" -> {
                textProjectStatus.text = "모집마감"
                textProjectStatus.setBackgroundColor(android.graphics.Color.parseColor("#9E9E9E"))
                textProjectStatus.setTextColor(android.graphics.Color.WHITE)
            }
            "completed" -> {
                textProjectStatus.text = "완료"
                textProjectStatus.setBackgroundColor(android.graphics.Color.parseColor("#2196F3"))
                textProjectStatus.setTextColor(android.graphics.Color.WHITE)
            }
        }

        // 지원 버튼 표시 여부
        val isCreator = project.creatorId == currentUserId
        val hasApplied = dbHelper.hasApplied(currentProjectId, currentUserId)
        val isMember = dbHelper.getProjectMembers(currentProjectId).any { it.userId == currentUserId }

        if (isCreator || isMember) {
            btnApply.visibility = View.GONE
        } else if (hasApplied) {
            btnApply.text = "지원 완료"
            btnApply.isEnabled = false
        } else if (project.status != "recruiting") {
            btnApply.text = "모집 마감"
            btnApply.isEnabled = false
        } else {
            btnApply.visibility = View.VISIBLE
            btnApply.isEnabled = true
        }

        // 팀원 목록 표시
        loadMembers()
    }

    private fun loadMembers() {
        val members = dbHelper.getProjectMembers(currentProjectId)

        if (members.isEmpty()) {
            textMembersList.text = "아직 팀원이 없습니다"
        } else {
            textMembersList.text = members.joinToString("\n") { member ->
                "• ${member.name} (${member.role})"
            }
        }
    }

    private fun showApplyDialog() {
        val project = currentProject ?: return
        val currentUser = dbHelper.getUser(currentUserId) ?: return

        val dialogView = layoutInflater.inflate(R.layout.dialog_project_apply, null)

        val spinnerRole = dialogView.findViewById<Spinner>(R.id.spinnerApplyRole)
        val roles = project.requiredRoles.split(", ")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, roles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRole.adapter = adapter

        val editMessage = dialogView.findViewById<EditText>(R.id.editApplyMessage)

        AlertDialog.Builder(this)
            .setTitle("프로젝트 지원")
            .setView(dialogView)
            .setPositiveButton("지원하기") { _, _ ->
                val selectedRole = spinnerRole.selectedItem.toString()
                val message = editMessage.text.toString().trim()

                val application = Application(
                    projectId = currentProjectId,
                    userId = currentUserId,
                    userName = currentUser.name,
                    userRole = selectedRole,
                    message = message
                )

                val result = dbHelper.applyToProject(application)
                if (result != -1L) {
                    Toast.makeText(this, "지원이 완료되었습니다", Toast.LENGTH_SHORT).show()
                    loadProjectDetail()
                } else {
                    Toast.makeText(this, "지원 실패", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("취소", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        loadProjectDetail()
    }
}