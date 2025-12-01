package com.example.helloandroid

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class CreateProjectActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var currentUserId: String

    private lateinit var editTitle: EditText
    private lateinit var editDescription: EditText
    private lateinit var gridRoles: GridView
    private lateinit var gridSkills: GridView
    private lateinit var editMaxMembers: EditText
    private lateinit var editDuration: EditText
    private lateinit var btnSubmit: Button

    private val availableRoles = arrayOf("프론트엔드", "백엔드", "풀스택", "디자이너", "기획자", "PM", "AI/ML 엔지니어", "데이터 사이언티스트")
    private val selectedRoles = ArrayList<String>()

    private val availableSkills = arrayOf(
        "HTML/CSS", "JavaScript", "TypeScript", "React", "Vue.js", "Angular", "Next.js", "Tailwind CSS",
        "Java", "Kotlin", "Python", "Node.js", "Spring Boot", "Django", "Flask", "Express.js", "FastAPI",
        "Android", "iOS", "Flutter", "React Native", "Swift", "SwiftUI",
        "MySQL", "PostgreSQL", "MongoDB", "Redis", "Firebase", "SQLite",
        "AWS", "Azure", "GCP", "Docker", "Kubernetes", "GitHub Actions", "Jenkins",
        "TensorFlow", "PyTorch", "Scikit-learn", "Pandas", "NumPy", "OpenCV", "Keras", "Hugging Face",
        "Git", "REST API", "GraphQL", "WebSocket", "Linux", "Figma", "Adobe XD", "Unity", "C++", "C#", "Go", "Rust"
    )
    private val selectedSkills = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_project)

        dbHelper = DBHelper(this)
        currentUserId = intent.getStringExtra("userId") ?: run {
            finish()
            return
        }

        initViews()
        setupRoleGrid()
        setupSkillGrid()
        setupSubmitButton()
    }

    private fun initViews() {
        editTitle = findViewById(R.id.editTitle)
        editDescription = findViewById(R.id.editDescription)
        gridRoles = findViewById(R.id.gridRoles)
        gridSkills = findViewById(R.id.gridSkills)
        editMaxMembers = findViewById(R.id.editMaxMembers)
        editDuration = findViewById(R.id.editDuration)
        btnSubmit = findViewById(R.id.btnSubmit)
    }

    private fun setupRoleGrid() {
        val adapter = RoleGridAdapter(this, availableRoles.toList(), selectedRoles)
        gridRoles.adapter = adapter

        gridRoles.setOnItemClickListener { _, _, position, _ ->
            val role = availableRoles[position]
            if (selectedRoles.contains(role)) {
                selectedRoles.remove(role)
            } else {
                selectedRoles.add(role)
            }
            adapter.notifyDataSetChanged()
        }
    }

    private fun setupSkillGrid() {
        val adapter = RoleGridAdapter(this, availableSkills.toList(), selectedSkills)
        gridSkills.adapter = adapter

        gridSkills.setOnItemClickListener { _, _, position, _ ->
            val skill = availableSkills[position]
            if (selectedSkills.contains(skill)) {
                selectedSkills.remove(skill)
            } else {
                selectedSkills.add(skill)
            }
            adapter.notifyDataSetChanged()
        }
    }

    private fun setupSubmitButton() {
        btnSubmit.setOnClickListener {
            val title = editTitle.text.toString().trim()
            val description = editDescription.text.toString().trim()
            val maxMembersStr = editMaxMembers.text.toString().trim()
            val duration = editDuration.text.toString().trim()

            if (title.isEmpty()) {
                Toast.makeText(this, "프로젝트 제목을 입력하세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (description.isEmpty()) {
                Toast.makeText(this, "프로젝트 설명을 입력하세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedRoles.isEmpty()) {
                Toast.makeText(this, "필요한 역할을 선택하세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (maxMembersStr.isEmpty()) {
                Toast.makeText(this, "모집 인원을 입력하세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val maxMembers = maxMembersStr.toIntOrNull()
            if (maxMembers == null || maxMembers < 2) {
                Toast.makeText(this, "모집 인원은 2명 이상이어야 합니다", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (duration.isEmpty()) {
                Toast.makeText(this, "프로젝트 기간을 입력하세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val project = Project(
                creatorId = currentUserId,
                title = title,
                description = description,
                requiredRoles = selectedRoles.joinToString(", "),
                requiredSkills = selectedSkills.joinToString(", "),
                maxMembers = maxMembers,
                duration = duration
            )

            val projectId = dbHelper.createProject(project)
            if (projectId != -1L) {
                Toast.makeText(this, "프로젝트가 등록되었습니다!", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            } else {
                Toast.makeText(this, "프로젝트 등록 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }
}