package com.example.helloandroid

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class ProjectDetailActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var currentUserId: String
    private var projectId: Int = 0

    private lateinit var textTitle: TextView
    private lateinit var textCreator: TextView
    private lateinit var textDescription: TextView
    private lateinit var textRoles: TextView
    private lateinit var textSkills: TextView
    private lateinit var textMembers: TextView
    private lateinit var textDuration: TextView
    private lateinit var textStatus: TextView
    private lateinit var listViewMembers: ListView
    private lateinit var btnApply: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_detail)

        dbHelper = DBHelper(this)
        currentUserId = intent.getStringExtra("userId") ?: run {
            finish()
            return
        }
        projectId = intent.getIntExtra("projectId", 0)

        if (projectId == 0) {
            Toast.makeText(this, "잘못된 프로젝트입니다", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        initViews()
        loadProjectDetail()
    }

    private fun initViews() {
        textTitle = findViewById(R.id.textTitle)
        textCreator = findViewById(R.id.textCreator)
        textDescription = findViewById(R.id.textDescription)
        textRoles = findViewById(R.id.textRoles)
        textSkills = findViewById(R.id.textSkills)
        textMembers = findViewById(R.id.textMembers)
        textDuration = findViewById(R.id.textDuration)
        textStatus = findViewById(R.id.textStatus)
        listViewMembers = findViewById(R.id.listViewMembers)
        btnApply = findViewById(R.id.btnApply)
    }

    private fun loadProjectDetail() {
        val project = dbHelper.getProject(projectId)
        if (project == null) {
            Toast.makeText(this, "프로젝트를 찾을 수 없습니다", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val creator = dbHelper.getUser(project.creatorId)

        textTitle.text = project.title
        textCreator.text = "팀장: ${creator?.name ?: "알 수 없음"}"
        textDescription.text = project.description
        textRoles.text = "필요한 역할: ${project.requiredRoles}"
        textSkills.text = "필요한 기술: ${project.requiredSkills}"
        textMembers.text = "모집 인원: ${project.currentMembers}/${project.maxMembers}명"
        textDuration.text = "프로젝트 기간: ${project.duration}"

        when (project.status) {
            "recruiting" -> {
                textStatus.text = "모집중"
                textStatus.setBackgroundColor(android.graphics.Color.parseColor("#4CAF50"))
            }
            "closed" -> {
                textStatus.text = "모집마감"
                textStatus.setBackgroundColor(android.graphics.Color.parseColor("#9E9E9E"))
            }
        }

        // 팀원 목록
        val members = dbHelper.getProjectMembers(projectId)
        val memberAdapter = MemberAdapter(this, members)
        listViewMembers.adapter = memberAdapter

        // 지원 버튼 설정
        setupApplyButton(project)
    }

    private fun setupApplyButton(project: Project) {
        // 자신의 프로젝트면 지원 불가
        if (project.creatorId == currentUserId) {
            btnApply.visibility = View.GONE
            return
        }

        // 이미 지원했는지 확인
        if (dbHelper.isAlreadyApplied(projectId, currentUserId)) {
            btnApply.text = "이미 지원함"
            btnApply.isEnabled = false
            return
        }

        // 모집 마감이면 지원 불가
        if (project.status == "closed") {
            btnApply.text = "모집 마감"
            btnApply.isEnabled = false
            return
        }

        btnApply.setOnClickListener {
            showApplyDialog(project)
        }
    }

    private fun showApplyDialog(project: Project) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_apply_project, null)

        val spinnerRole = dialogView.findViewById<Spinner>(R.id.spinnerApplyRole)
        val editMessage = dialogView.findViewById<EditText>(R.id.editApplyMessage)

        // 필요한 역할로 Spinner 설정
        val roles = project.requiredRoles.split(", ")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, roles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRole.adapter = adapter

        AlertDialog.Builder(this)
            .setTitle("프로젝트 지원")
            .setView(dialogView)
            .setPositiveButton("지원하기") { _, _ ->
                val selectedRole = spinnerRole.selectedItem.toString()
                val message = editMessage.text.toString().trim()

                if (message.isEmpty()) {
                    Toast.makeText(this, "지원 메시지를 입력하세요", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (dbHelper.applyToProject(projectId, currentUserId, selectedRole, message)) {
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

class MemberAdapter(
    private val context: android.content.Context,
    private val members: ArrayList<Member>
) : BaseAdapter() {

    override fun getCount(): Int = members.size

    override fun getItem(position: Int): Any = members[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: android.view.ViewGroup?): View {
        val view = convertView ?: android.view.LayoutInflater.from(context)
            .inflate(R.layout.item_member_list, parent, false)

        val member = members[position]

        view.findViewById<TextView>(R.id.textMemberName).text = member.userName
        view.findViewById<TextView>(R.id.textMemberRole).text = member.role
        view.findViewById<TextView>(R.id.textMemberSkills).text = member.userSkills.ifEmpty { "기술 스택 미정" }
        view.findViewById<TextView>(R.id.textMemberContact).text = "📞 ${member.userContact}"

        return view
    }
}