package com.example.helloandroid

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.io.FileWriter

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var currentUserId: String
    private lateinit var currentUser: User

    private lateinit var btnTabProfile: Button
    private lateinit var btnTabUsers: Button
    private lateinit var btnTabInterest: Button
    private lateinit var btnTabProjects: Button
    private lateinit var btnTabSettings: Button

    private lateinit var layoutProfile: ScrollView
    private lateinit var layoutUsers: ScrollView
    private lateinit var layoutInterest: ScrollView
    private lateinit var layoutProjects: ScrollView
    private lateinit var layoutSettings: ScrollView

    private lateinit var profileBadge: ProfileBadgeView
    private lateinit var textProfileSkills: TextView
    private lateinit var textProfileExperience: TextView
    private lateinit var textProfileStrength: TextView
    private lateinit var textProfileInterests: TextView
    private lateinit var textProfilePreferredTeammate: TextView
    private lateinit var textProfileCollaborationStyle: TextView
    private lateinit var textProfileGithub: TextView
    private lateinit var btnEditProfile: Button

    private lateinit var spinnerRoleFilter: Spinner
    private lateinit var listViewUsers: ListView
    private var userListAdapter: UserListAdapter? = null

    private lateinit var textMyInterests: TextView
    private lateinit var textReceivedInterests: TextView

    private lateinit var btnCreateProject: Button
    private lateinit var spinnerStatusFilter: Spinner
    private lateinit var textProjectCount: TextView
    private lateinit var listViewProjects: ListView
    private lateinit var btnMyProjects: Button
    private lateinit var btnMyApplications: Button
    private var projectListAdapter: ProjectListAdapter? = null

    private lateinit var textUserInfo: TextView
    private lateinit var btnEditUserInfo: Button
    private lateinit var btnExportData: Button
    private lateinit var btnResetData: Button
    private lateinit var btnLogout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DBHelper(this)
        currentUserId = intent.getStringExtra("userId") ?: run {
            finish()
            return
        }

        currentUser = dbHelper.getUser(currentUserId) ?: run {
            Toast.makeText(this, "사용자 정보를 불러올 수 없습니다", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        initViews()
        setupTabs()
        setupProfileTab()
        setupUsersTab()
        setupInterestTab()
        setupProjectsTab()
        setupSettingsTab()

        showTab(0)
    }

    private fun initViews() {
        btnTabProfile = findViewById(R.id.btnTabProfile)
        btnTabUsers = findViewById(R.id.btnTabUsers)
        btnTabInterest = findViewById(R.id.btnTabInterest)
        btnTabProjects = findViewById(R.id.btnTabProjects)
        btnTabSettings = findViewById(R.id.btnTabSettings)

        layoutProfile = findViewById(R.id.layoutProfile)
        layoutUsers = findViewById(R.id.layoutUsers)
        layoutInterest = findViewById(R.id.layoutInterest)
        layoutProjects = findViewById(R.id.layoutProjects)
        layoutSettings = findViewById(R.id.layoutSettings)

        profileBadge = findViewById(R.id.profileBadge)
        textProfileSkills = findViewById(R.id.textProfileSkills)
        textProfileExperience = findViewById(R.id.textProfileExperience)
        textProfileStrength = findViewById(R.id.textProfileStrength)
        textProfileInterests = findViewById(R.id.textProfileInterests)
        textProfilePreferredTeammate = findViewById(R.id.textProfilePreferredTeammate)
        textProfileCollaborationStyle = findViewById(R.id.textProfileCollaborationStyle)
        textProfileGithub = findViewById(R.id.textProfileGithub)
        btnEditProfile = findViewById(R.id.btnEditProfile)

        spinnerRoleFilter = findViewById(R.id.spinnerRoleFilter)
        listViewUsers = findViewById(R.id.listViewUsers)

        textMyInterests = findViewById(R.id.textMyInterests)
        textReceivedInterests = findViewById(R.id.textReceivedInterests)

        btnCreateProject = findViewById(R.id.btnCreateProject)
        spinnerStatusFilter = findViewById(R.id.spinnerStatusFilter)
        textProjectCount = findViewById(R.id.textProjectCount)
        listViewProjects = findViewById(R.id.listViewProjects)
        btnMyProjects = findViewById(R.id.btnMyProjects)
        btnMyApplications = findViewById(R.id.btnMyApplications)

        textUserInfo = findViewById(R.id.textUserInfo)
        btnEditUserInfo = findViewById(R.id.btnEditUserInfo)
        btnExportData = findViewById(R.id.btnExportData)
        btnResetData = findViewById(R.id.btnResetData)
        btnLogout = findViewById(R.id.btnLogout)
    }

    private fun setupTabs() {
        btnTabProfile.setOnClickListener { showTab(0) }
        btnTabUsers.setOnClickListener { showTab(1) }
        btnTabInterest.setOnClickListener { showTab(2) }
        btnTabProjects.setOnClickListener { showTab(3) }
        btnTabSettings.setOnClickListener { showTab(4) }
    }

    private fun showTab(index: Int) {
        layoutProfile.visibility = View.GONE
        layoutUsers.visibility = View.GONE
        layoutInterest.visibility = View.GONE
        layoutProjects.visibility = View.GONE
        layoutSettings.visibility = View.GONE

        btnTabProfile.setBackgroundColor(Color.parseColor("#BBDEFB"))
        btnTabUsers.setBackgroundColor(Color.parseColor("#BBDEFB"))
        btnTabInterest.setBackgroundColor(Color.parseColor("#BBDEFB"))
        btnTabProjects.setBackgroundColor(Color.parseColor("#BBDEFB"))
        btnTabSettings.setBackgroundColor(Color.parseColor("#BBDEFB"))

        when (index) {
            0 -> {
                layoutProfile.visibility = View.VISIBLE
                btnTabProfile.setBackgroundColor(Color.parseColor("#6200EE"))
                refreshProfileTab()
            }
            1 -> {
                layoutUsers.visibility = View.VISIBLE
                btnTabUsers.setBackgroundColor(Color.parseColor("#6200EE"))
                refreshUsersTab()
            }
            2 -> {
                layoutInterest.visibility = View.VISIBLE
                btnTabInterest.setBackgroundColor(Color.parseColor("#6200EE"))
                refreshInterestTab()
            }
            3 -> {
                layoutProjects.visibility = View.VISIBLE
                btnTabProjects.setBackgroundColor(Color.parseColor("#6200EE"))
                refreshProjectsTab()
            }
            4 -> {
                layoutSettings.visibility = View.VISIBLE
                btnTabSettings.setBackgroundColor(Color.parseColor("#6200EE"))
                refreshSettingsTab()
            }
        }
    }

    private fun setupProfileTab() {
        btnEditProfile.setOnClickListener {
            showEditProfileDialog()
        }
    }

    private fun refreshProfileTab() {
        currentUser = dbHelper.getUser(currentUserId) ?: return

        profileBadge.setBadgeData(
            currentUser.name,
            currentUser.receivedInterests,
            currentUser.role.ifEmpty { "미정" }
        )

        textProfileSkills.text = currentUser.skills.ifEmpty { "미정" }
        textProfileExperience.text = currentUser.experience.ifEmpty { "미정" }
        textProfileStrength.text = currentUser.strength.ifEmpty { "미정" }
        textProfileInterests.text = currentUser.interests.ifEmpty { "미정" }
        textProfilePreferredTeammate.text = currentUser.preferredTeammate.ifEmpty { "미정" }
        textProfileCollaborationStyle.text = currentUser.collaborationStyle.ifEmpty { "미정" }
        textProfileGithub.text = currentUser.github.ifEmpty { "미정" }
    }

    private fun showEditProfileDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_profile, null)

        val spinnerRole = dialogView.findViewById<Spinner>(R.id.spinnerRole)
        val roles = arrayOf("프론트엔드", "백엔드", "풀스택", "디자이너", "기획자", "PM", "AI/ML 엔지니어", "데이터 사이언티스트")
        val roleAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, roles)
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRole.adapter = roleAdapter

        val currentRoleIndex = roles.indexOf(currentUser.role)
        if (currentRoleIndex >= 0) {
            spinnerRole.setSelection(currentRoleIndex)
        }

        val gridSkills = dialogView.findViewById<GridView>(R.id.gridSkills)
        val allSkills = SkillGridAdapter.getDefaultSkills()
        val userSelectedSkillsList = ArrayList(
            currentUser.skills.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        )

        val skillAdapter = SkillGridAdapter(this, allSkills, userSelectedSkillsList)
        gridSkills.adapter = skillAdapter

        gridSkills.setOnItemClickListener { _, _, position, _ ->
            val skill = skillAdapter.getItem(position) as String
            if (userSelectedSkillsList.contains(skill)) {
                userSelectedSkillsList.remove(skill)
            } else {
                userSelectedSkillsList.add(skill)
            }
            skillAdapter.notifyDataSetChanged()
        }

        val editExperience = dialogView.findViewById<EditText>(R.id.editExperience)
        val editStrength = dialogView.findViewById<EditText>(R.id.editStrength)
        val editInterests = dialogView.findViewById<EditText>(R.id.editInterests)
        val editPreferredTeammate = dialogView.findViewById<EditText>(R.id.editPreferredTeammate)
        val spinnerCollaboration = dialogView.findViewById<Spinner>(R.id.spinnerCollaborationStyle)
        val collaborationStyles = arrayOf(
            "적극적 소통", "꼼꼼한 문서화", "리더십 발휘", "창의적 협업", "성실한 일정 준수",
            "분석적 접근", "코드 품질 중시", "도전적 학습", "효율성 추구", "안정성 중시"
        )
        val collaborationAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, collaborationStyles)
        collaborationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCollaboration.adapter = collaborationAdapter

        val currentCollaborationIndex = collaborationStyles.indexOf(currentUser.collaborationStyle)
        if (currentCollaborationIndex >= 0) {
            spinnerCollaboration.setSelection(currentCollaborationIndex)
        }

        val editGithub = dialogView.findViewById<EditText>(R.id.editGithub)

        editExperience.setText(currentUser.experience)
        editStrength.setText(currentUser.strength)
        editInterests.setText(currentUser.interests)
        editPreferredTeammate.setText(currentUser.preferredTeammate)
        editGithub.setText(currentUser.github)

        AlertDialog.Builder(this)
            .setTitle("프로필 수정")
            .setView(dialogView)
            .setPositiveButton("저장") { _, _ ->
                currentUser.role = spinnerRole.selectedItem.toString()
                currentUser.skills = userSelectedSkillsList.joinToString(", ")
                currentUser.experience = editExperience.text.toString()
                currentUser.strength = editStrength.text.toString()
                currentUser.interests = editInterests.text.toString()
                currentUser.preferredTeammate = editPreferredTeammate.text.toString()
                currentUser.collaborationStyle = spinnerCollaboration.selectedItem.toString()
                currentUser.github = editGithub.text.toString()

                if (dbHelper.saveUser(currentUser)) {
                    Toast.makeText(this, "프로필이 저장되었습니다", Toast.LENGTH_SHORT).show()
                    refreshProfileTab()
                } else {
                    Toast.makeText(this, "저장 실패", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun setupUsersTab() {
        val roles = arrayOf("전체", "프론트엔드", "백엔드", "풀스택", "디자이너", "기획자", "PM", "AI/ML 엔지니어", "데이터 사이언티스트")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, roles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRoleFilter.adapter = adapter

        spinnerRoleFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedRole = roles[position]
                refreshUsersTab(selectedRole)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        listViewUsers.setOnItemClickListener { _, _, position, _ ->
            val user = userListAdapter?.getItem(position) as? User ?: return@setOnItemClickListener
            val intent = Intent(this, UserDetailActivity::class.java)
            intent.putExtra("userId", currentUserId)
            intent.putExtra("targetUserId", user.userId)
            startActivity(intent)
        }
    }

    private fun refreshUsersTab(role: String = "전체") {
        val users = dbHelper.getUsersByRole(role).filter { it.userId != currentUserId }

        if (userListAdapter == null) {
            userListAdapter = UserListAdapter(this, ArrayList(users), currentUserId, dbHelper) {
                refreshUsersTab(role)
            }
            listViewUsers.adapter = userListAdapter
        } else {
            userListAdapter?.updateData(ArrayList(users))
        }
    }

    private fun setupInterestTab() {}

    private fun refreshInterestTab() {
        val myInterests = dbHelper.getUserInterests(currentUserId)
        val receivedInterests = dbHelper.getReceivedInterests(currentUserId)

        textMyInterests.text = if (myInterests.isEmpty()) {
            "관심 표시한 유저가 없습니다"
        } else {
            myInterests.joinToString("\n") { "• ${it.name} (${it.role})" }
        }

        textReceivedInterests.text = if (receivedInterests.isEmpty()) {
            "나에게 관심을 표시한 유저가 없습니다"
        } else {
            receivedInterests.joinToString("\n") { "• ${it.name} (${it.role})" }
        }
    }

    private fun setupProjectsTab() {
        btnCreateProject.setOnClickListener {
            val intent = Intent(this, CreateProjectActivity::class.java)
            intent.putExtra("userId", currentUserId)
            startActivityForResult(intent, 100)
        }

        val statuses = arrayOf("전체", "recruiting", "closed")
        val statusLabels = arrayOf("전체", "모집중", "모집마감")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, statusLabels)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStatusFilter.adapter = spinnerAdapter

        spinnerStatusFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedStatus = statuses[position]
                loadProjects(selectedStatus)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        btnMyProjects.setOnClickListener {
            val intent = Intent(this, MyProjectsActivity::class.java)
            intent.putExtra("userId", currentUserId)
            startActivity(intent)
        }

        btnMyApplications.setOnClickListener {
            val intent = Intent(this, MyApplicationsActivity::class.java)
            intent.putExtra("userId", currentUserId)
            startActivity(intent)
        }

        listViewProjects.setOnItemClickListener { _, _, position, _ ->
            val project = projectListAdapter?.getItem(position) as? Project ?: return@setOnItemClickListener
            val intent = Intent(this, ProjectDetailActivity::class.java)
            intent.putExtra("userId", currentUserId)
            intent.putExtra("projectId", project.projectId)
            startActivity(intent)
        }
    }

    private fun refreshProjectsTab() {
        val selectedPosition = spinnerStatusFilter.selectedItemPosition
        val statuses = arrayOf("전체", "recruiting", "closed")
        loadProjects(statuses[selectedPosition])
    }

    private fun loadProjects(status: String = "전체") {
        val projects = if (status == "전체") {
            dbHelper.getAllProjects()
        } else {
            dbHelper.getProjectsByStatus(status)
        }

        textProjectCount.text = "${projects.size}개 프로젝트"

        if (projectListAdapter == null) {
            projectListAdapter = ProjectListAdapter(this, projects, dbHelper)
            listViewProjects.adapter = projectListAdapter
        } else {
            projectListAdapter?.updateData(projects)
        }
    }

    private fun setupSettingsTab() {
        btnEditUserInfo.setOnClickListener {
            showEditUserInfoDialog()
        }

        btnExportData.setOnClickListener {
            exportData()
        }

        btnResetData.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("데이터 초기화")
                .setMessage("정말 모든 데이터를 초기화하시겠습니까?")
                .setPositiveButton("초기화") { _, _ ->
                    dbHelper.writableDatabase.execSQL("DELETE FROM userTBL")
                    dbHelper.writableDatabase.execSQL("DELETE FROM interestTBL")
                    dbHelper.writableDatabase.execSQL("DELETE FROM projectTBL")
                    dbHelper.writableDatabase.execSQL("DELETE FROM applicationTBL")
                    dbHelper.writableDatabase.execSQL("DELETE FROM memberTBL")
                    Toast.makeText(this, "데이터가 초기화되었습니다", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton("취소", null)
                .show()
        }

        btnLogout.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun refreshSettingsTab() {
        textUserInfo.text = """
            이름: ${currentUser.name}
            학번: ${currentUser.userId}
            연락처: ${currentUser.contact}
        """.trimIndent()
    }

    private fun showEditUserInfoDialog() {
        val container = LinearLayout(this)
        container.orientation = LinearLayout.VERTICAL
        container.setPadding(50, 40, 50, 10)

        val editName = EditText(this)
        editName.hint = "이름"
        editName.setText(currentUser.name)
        container.addView(editName)

        val editContact = EditText(this)
        editContact.hint = "연락처"
        editContact.setText(currentUser.contact)
        container.addView(editContact)

        val editPassword = EditText(this)
        editPassword.hint = "새 비밀번호 (변경 시에만 입력)"
        editPassword.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        container.addView(editPassword)

        AlertDialog.Builder(this)
            .setTitle("내 정보 수정")
            .setView(container)
            .setPositiveButton("저장") { _, _ ->
                val newName = editName.text.toString().trim()
                val newContact = editContact.text.toString().trim()
                val newPassword = editPassword.text.toString().trim()

                if (newName.isEmpty() || newContact.isEmpty()) {
                    Toast.makeText(this, "이름과 연락처는 필수입니다", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                currentUser.name = newName
                currentUser.contact = newContact
                if (newPassword.isNotEmpty()) {
                    currentUser.password = newPassword
                }

                if (dbHelper.saveUser(currentUser)) {
                    Toast.makeText(this, "정보가 수정되었습니다", Toast.LENGTH_SHORT).show()
                    refreshSettingsTab()
                    refreshProfileTab()
                } else {
                    Toast.makeText(this, "수정 실패", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun exportData() {
        try {
            val exportDir = getExternalFilesDir(null)
            val file = java.io.File(exportDir, "teambuilding_export.txt")
            val writer = FileWriter(file)

            writer.write("=== 팀 빌딩 데이터 내보내기 ===\n")
            writer.write("작성일: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())}\n\n")

            writer.write("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n")
            writer.write("내 프로필\n")
            writer.write("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n")
            writer.write("이름: ${currentUser.name}\n")
            writer.write("학번: ${currentUser.userId}\n")
            writer.write("연락처: ${currentUser.contact}\n\n")

            // 역할
            if (currentUser.role.isNotEmpty()) {
                writer.write("역할: ${currentUser.role}\n")
            }

            // 보유 기술
            if (currentUser.skills.isNotEmpty()) {
                writer.write("보유 기술: ${currentUser.skills}\n")
            } else {
                writer.write("보유 기술: 미등록\n")
            }

            // 프로젝트 경험
            if (currentUser.experience.isNotEmpty()) {
                writer.write("\n프로젝트 경험:\n${currentUser.experience}\n")
            } else {
                writer.write("\n프로젝트 경험: 미등록\n")
            }

            // 강점
            if (currentUser.strength.isNotEmpty()) {
                writer.write("\n강점:\n${currentUser.strength}\n")
            } else {
                writer.write("\n강점: 미등록\n")
            }

            // 관심 분야
            if (currentUser.interests.isNotEmpty()) {
                writer.write("\n관심 분야:\n${currentUser.interests}\n")
            } else {
                writer.write("\n관심 분야: 미등록\n")
            }

            // 선호하는 팀원 유형
            if (currentUser.preferredTeammate.isNotEmpty()) {
                writer.write("\n선호하는 팀원 유형:\n${currentUser.preferredTeammate}\n")
            } else {
                writer.write("\n선호하는 팀원 유형: 미등록\n")
            }

            // 협업 스타일
            if (currentUser.collaborationStyle.isNotEmpty()) {
                writer.write("\n협업 스타일:\n${currentUser.collaborationStyle}\n")
            } else {
                writer.write("\n협업 스타일: 미등록\n")
            }

            // GitHub
            if (currentUser.github.isNotEmpty()) {
                writer.write("\nGitHub: ${currentUser.github}\n")
            }

            // 통계
            writer.write("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n")
            writer.write("활동 통계\n")
            writer.write("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n")
            writer.write("받은 관심 표시: ${currentUser.receivedInterests}명\n")

            // 내가 생성한 프로젝트
            val myProjects = dbHelper.getMyProjects(currentUserId)
            writer.write("생성한 프로젝트: ${myProjects.size}개\n")

            // 참여 중인 프로젝트
            val participatingProjects = dbHelper.getMyParticipatingProjects(currentUserId)
            writer.write("참여 중인 프로젝트: ${participatingProjects.size}개\n")

            // 지원한 프로젝트
            val myApplications = dbHelper.getMyApplications(currentUserId)
            writer.write("지원한 프로젝트: ${myApplications.size}개\n")

            writer.close()

            Toast.makeText(this, "데이터 내보내기 완료: ${file.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "내보내기 실패: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        val currentTab = when {
            layoutProfile.visibility == View.VISIBLE -> 0
            layoutUsers.visibility == View.VISIBLE -> 1
            layoutInterest.visibility == View.VISIBLE -> 2
            layoutProjects.visibility == View.VISIBLE -> 3
            layoutSettings.visibility == View.VISIBLE -> 4
            else -> 0
        }
        showTab(currentTab)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            refreshProjectsTab()
        }
    }
}