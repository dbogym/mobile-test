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

    // 탭 버튼
    private lateinit var btnTabProfile: Button
    private lateinit var btnTabUsers: Button
    private lateinit var btnTabInterest: Button
    private lateinit var btnTabSettings: Button
    private lateinit var btnTabProjects: Button

    // 탭 레이아웃
    private lateinit var layoutProfile: ScrollView
    private lateinit var layoutUsers: ScrollView
    private lateinit var layoutInterest: ScrollView
    private lateinit var layoutSettings: ScrollView
    private lateinit var layoutProjects: LinearLayout

    // 프로필 탭 위젯
    private lateinit var profileBadge: ProfileBadgeView
    private lateinit var textProfileSkills: TextView
    private lateinit var textProfileExperience: TextView
    private lateinit var textProfileStrength: TextView
    private lateinit var textProfileInterests: TextView
    private lateinit var textProfilePreferredTeammate: TextView
    private lateinit var textProfileCollaborationStyle: TextView
    private lateinit var textProfileGithub: TextView
    private lateinit var btnEditProfile: Button

    // 유저 탭 위젯
    private lateinit var spinnerRoleFilter: Spinner
    private lateinit var listViewUsers: ListView
    private var userListAdapter: UserListAdapter? = null

    // 관심 탭 위젯
    private lateinit var textMyInterests: TextView
    private lateinit var textReceivedInterests: TextView

    // 설정 탭 위젯
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
        setupSettingsTab()
        setupProjectsTab()

        showTab(0)
    }

    private fun initViews() {
        // 탭 버튼
        btnTabProfile = findViewById(R.id.btnTabProfile)
        btnTabUsers = findViewById(R.id.btnTabUsers)
        btnTabInterest = findViewById(R.id.btnTabInterest)
        btnTabSettings = findViewById(R.id.btnTabSettings)
        btnTabProjects = findViewById(R.id.btnTabProjects)

        // 탭 레이아웃
        layoutProfile = findViewById(R.id.layoutProfile)
        layoutUsers = findViewById(R.id.layoutUsers)
        layoutInterest = findViewById(R.id.layoutInterest)
        layoutSettings = findViewById(R.id.layoutSettings)
        layoutProjects = findViewById(R.id.layoutProjects)

        // 프로필 탭
        profileBadge = findViewById(R.id.profileBadge)
        textProfileSkills = findViewById(R.id.textProfileSkills)
        textProfileExperience = findViewById(R.id.textProfileExperience)
        textProfileStrength = findViewById(R.id.textProfileStrength)
        textProfileInterests = findViewById(R.id.textProfileInterests)
        textProfilePreferredTeammate = findViewById(R.id.textProfilePreferredTeammate)
        textProfileCollaborationStyle = findViewById(R.id.textProfileCollaborationStyle)
        textProfileGithub = findViewById(R.id.textProfileGithub)
        btnEditProfile = findViewById(R.id.btnEditProfile)

        // 유저 탭
        spinnerRoleFilter = findViewById(R.id.spinnerRoleFilter)
        listViewUsers = findViewById(R.id.listViewUsers)

        // 관심 탭
        textMyInterests = findViewById(R.id.textMyInterests)
        textReceivedInterests = findViewById(R.id.textReceivedInterests)

        // 설정 탭
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
        btnTabSettings.setOnClickListener { showTab(3) }
        btnTabProjects.setOnClickListener { showTab(4) }
    }

    private fun showTab(index: Int) {
        // 모든 탭 숨기기
        layoutProfile.visibility = View.GONE
        layoutUsers.visibility = View.GONE
        layoutInterest.visibility = View.GONE
        layoutSettings.visibility = View.GONE
        layoutProjects.visibility = View.GONE

        // 모든 버튼 기본 색상
        btnTabProfile.setBackgroundColor(Color.parseColor("#BBDEFB"))
        btnTabUsers.setBackgroundColor(Color.parseColor("#BBDEFB"))
        btnTabInterest.setBackgroundColor(Color.parseColor("#BBDEFB"))
        btnTabSettings.setBackgroundColor(Color.parseColor("#BBDEFB"))
        btnTabProjects.setBackgroundColor(Color.parseColor("#BBDEFB"))

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
                layoutSettings.visibility = View.VISIBLE
                btnTabSettings.setBackgroundColor(Color.parseColor("#6200EE"))
                refreshSettingsTab()
            }
            4 -> {
                layoutProjects.visibility = View.VISIBLE
                btnTabProjects.setBackgroundColor(Color.parseColor("#6200EE"))
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

        // Spinner 설정
        val spinnerRole = dialogView.findViewById<Spinner>(R.id.spinnerRole)
        val roles = arrayOf("프론트엔드", "백엔드", "풀스택", "디자이너", "기획자", "PM")
        val roleAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, roles)
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRole.adapter = roleAdapter

        // 현재 역할 선택
        val currentRoleIndex = roles.indexOf(currentUser.role)
        if (currentRoleIndex >= 0) {
            spinnerRole.setSelection(currentRoleIndex)
        }

        // 기술 스택 GridView
        val gridSkills = dialogView.findViewById<GridView>(R.id.gridSkills)

        // --- FIX START: SkillGridAdapter 인스턴스화 수정 및 클릭 리스너 추가 ---
        // 1. 모든 스킬 목록 가져오기
        val allSkills = SkillGridAdapter.getDefaultSkills()

        // 2. 현재 선택된 스킬 목록을 String에서 ArrayList<String>으로 변환 (어댑터에 전달)
        val userSelectedSkillsList = ArrayList(
            currentUser.skills
                .split(",")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
        )

        // 3. 올바른 3개의 인자를 사용하여 어댑터 인스턴스화
        val skillAdapter = SkillGridAdapter(
            this,
            allSkills, // 모든 스킬 목록
            userSelectedSkillsList // 현재 선택된 스킬 목록 (mutable list)
        )
        gridSkills.adapter = skillAdapter

        // 4. GridView 아이템 클릭 리스너 설정 (선택/취소 로직)
        gridSkills.setOnItemClickListener { _, _, position, _ ->
            val skill = skillAdapter.getItem(position) as String
            if (userSelectedSkillsList.contains(skill)) {
                userSelectedSkillsList.remove(skill)
            } else {
                userSelectedSkillsList.add(skill)
            }
            skillAdapter.notifyDataSetChanged()
        }
        // --- FIX END ---

        val editExperience = dialogView.findViewById<EditText>(R.id.editExperience)
        val editStrength = dialogView.findViewById<EditText>(R.id.editStrength)
        val editInterests = dialogView.findViewById<EditText>(R.id.editInterests)
        val editPreferredTeammate = dialogView.findViewById<EditText>(R.id.editPreferredTeammate)

        // 협업 스타일 Spinner
        // XML ID에 맞게 수정: spinnerCollaboration -> spinnerCollaborationStyle
        val spinnerCollaboration = dialogView.findViewById<Spinner>(R.id.spinnerCollaborationStyle)
        val collaborationStyles = arrayOf(
            "적극적 소통", "꼼꼼한 문서화", "리더십 발휘", "창의적 협업", "성실한 일정 준수",
            "분석적 접근", "코드 품질 중시", "도전적 학습", "효율성 추구", "안정성 중시"
        )
        val collaborationAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, collaborationStyles)
        collaborationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCollaboration.adapter = collaborationAdapter

        // 현재 협업 스타일 선택
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
                // --- FIX: getSelectedSkills() 대신 현재 관리하는 리스트를 String으로 변환하여 저장 ---
                currentUser.skills = userSelectedSkillsList.joinToString(", ")
                // ---------------------------------------------------------------------------------
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
        // 역할 필터 설정
        val roles = arrayOf("전체", "프론트엔드", "백엔드", "풀스택", "디자이너", "기획자", "PM")
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
            userListAdapter = UserListAdapter(
                this,
                ArrayList(users),
                currentUserId,
                dbHelper
            ) { user ->
                // 관심 표시/취소 후 새로고침
                refreshUsersTab(role)
            }
            listViewUsers.adapter = userListAdapter
        } else {
            userListAdapter?.updateData(ArrayList(users))
        }
    }

    private fun setupInterestTab() {
        // 관심 탭은 자동 새로고침
    }

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
                    finish()
                }
                .setNegativeButton("취소", null)
                .show()
        }

        btnLogout.setOnClickListener {
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
        val dialogView = layoutInflater.inflate(android.R.layout.select_dialog_item, null)
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

    private fun setupProjectsTab() {
        val btnGoToProjects = findViewById<Button>(R.id.btnGoToProjects)
        btnGoToProjects.setOnClickListener {
            val intent = Intent(this, ProjectListActivity::class.java)
            intent.putExtra("userId", currentUserId)
            startActivity(intent)
        }
    }

    // 9주차: 파일 쓰기
    private fun exportData() {
        try {
            val exportDir = getExternalFilesDir(null)
            val file = java.io.File(exportDir, "teambuilding_export.txt")
            val writer = FileWriter(file)

            writer.write("=== 팀 빌딩 데이터 내보내기 ===\n")
            writer.write("작성일: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())}\n\n")

            writer.write("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n")
            writer.write("내 프로필\n")
            writer.write("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n")

            writer.write("👤 기본 정보\n")
            writer.write("  • 이름: ${currentUser.name}\n")
            writer.write("  • 학번: ${currentUser.userId}\n")
            writer.write("  • 연락처: ${currentUser.contact}\n")
            writer.write("  • 역할: ${currentUser.role.ifEmpty { "미정" }}\n\n")

            writer.write("🛠️ 기술 스택\n")
            writer.write("  ${currentUser.skills.ifEmpty { "미정" }}\n\n")

            writer.write("📚 개발 경험\n")
            writer.write("  ${currentUser.experience.ifEmpty { "미정" }}\n\n")

            writer.write("💪 자신 있는 부분\n")
            writer.write("  ${currentUser.strength.ifEmpty { "미정" }}\n\n")

            writer.write("💡 관심 주제\n")
            writer.write("  ${currentUser.interests.ifEmpty { "미정" }}\n\n")

            writer.write("🤝 함께 하고 싶은 팀원\n")
            writer.write("  ${currentUser.preferredTeammate.ifEmpty { "미정" }}\n\n")

            writer.write("🎯 협업 스타일\n")
            writer.write("  ${currentUser.collaborationStyle.ifEmpty { "미정" }}\n\n")

            writer.write("💻 GitHub\n")
            writer.write("  ${currentUser.github.ifEmpty { "미정" }}\n\n")

            writer.write("📊 통계\n")
            writer.write("  • 받은 관심: ${currentUser.receivedInterests}개\n")
            writer.write("  • Level: ${calculateLevel(currentUser.receivedInterests)}\n\n")

            // 내가 관심 표시한 유저
            val myInterests = dbHelper.getUserInterests(currentUserId)
            writer.write("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n")
            writer.write("내가 관심 표시한 유저 (${myInterests.size}명)\n")
            writer.write("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n")
            if (myInterests.isEmpty()) {
                writer.write("  없음\n\n")
            } else {
                myInterests.forEach { user ->
                    writer.write("  📌 ${user.name} (${user.role})\n")
                    writer.write("     기술: ${user.skills}\n")
                    writer.write("     연락처: ${user.contact}\n\n")
                }
            }

            // 나에게 관심 표시한 유저
            val receivedInterests = dbHelper.getReceivedInterests(currentUserId)
            writer.write("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n")
            writer.write("나에게 관심 표시한 유저 (${receivedInterests.size}명)\n")
            writer.write("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n")
            if (receivedInterests.isEmpty()) {
                writer.write("  없음\n\n")
            } else {
                receivedInterests.forEach { user ->
                    writer.write("  💝 ${user.name} (${user.role})\n")
                    writer.write("     기술: ${user.skills}\n")
                    writer.write("     연락처: ${user.contact}\n\n")
                }
            }

            // 전체 회원 통계
            val allUsers = dbHelper.getAllUsers()
            writer.write("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n")
            writer.write("전체 회원 통계 (${allUsers.size}명)\n")
            writer.write("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n")

            // 역할별 통계
            val roleStats = allUsers.groupBy { it.role }.mapValues { it.value.size }
            writer.write("📊 역할별 분포\n")
            roleStats.forEach { (role, count) ->
                if (role.isNotEmpty()) {
                    writer.write("  • ${role}: ${count}명\n")
                }
            }
            writer.write("\n")

            // 전체 회원 목록
            writer.write("📋 전체 회원 목록\n")
            allUsers.sortedBy { it.name }.forEach { user ->
                writer.write("  • ${user.name} (${user.role.ifEmpty { "미정" }}) - ${user.contact}\n")
            }

            writer.write("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n")
            writer.write("End of Report\n")
            writer.write("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n")

            writer.close()

            Toast.makeText(this, "데이터 내보내기 완료: ${file.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "내보내기 실패: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun calculateLevel(receivedInterests: Int): String {
        return when {
            receivedInterests >= 21 -> "Level 5"
            receivedInterests >= 11 -> "Level 4"
            receivedInterests >= 6 -> "Level 3"
            receivedInterests >= 3 -> "Level 2"
            else -> "Level 1"
        }
    }

    override fun onResume() {
        super.onResume()
        // 다른 액티비티에서 돌아왔을 때 현재 탭 새로고침
        val currentTab = when {
            layoutProfile.visibility == View.VISIBLE -> 0
            layoutUsers.visibility == View.VISIBLE -> 1
            layoutInterest.visibility == View.VISIBLE -> 2
            layoutSettings.visibility == View.VISIBLE -> 3
            layoutProjects.visibility == View.VISIBLE -> 4
            else -> 0
        }
        showTab(currentTab)
    }
}