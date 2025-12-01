package com.example.helloandroid

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MyProjectsActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var currentUserId: String

    private lateinit var listViewMyProjects: ListView
    private lateinit var textNoProjects: TextView
    private var projectAdapter: MyProjectAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_projects)

        dbHelper = DBHelper(this)
        currentUserId = intent.getStringExtra("userId") ?: run {
            finish()
            return
        }

        initViews()
        loadMyProjects()
    }

    private fun initViews() {
        listViewMyProjects = findViewById(R.id.listViewMyProjects)
        textNoProjects = findViewById(R.id.textNoProjects)

        listViewMyProjects.setOnItemClickListener { _, _, position, _ ->
            val project = projectAdapter?.getItem(position) as? Project ?: return@setOnItemClickListener
            showProjectOptions(project)
        }
    }

    private fun loadMyProjects() {
        val myProjects = dbHelper.getMyProjects(currentUserId)

        if (myProjects.isEmpty()) {
            textNoProjects.visibility = View.VISIBLE
            listViewMyProjects.visibility = View.GONE
        } else {
            textNoProjects.visibility = View.GONE
            listViewMyProjects.visibility = View.VISIBLE

            if (projectAdapter == null) {
                projectAdapter = MyProjectAdapter(this, myProjects, dbHelper)
                listViewMyProjects.adapter = projectAdapter
            } else {
                projectAdapter?.updateData(myProjects)
            }
        }
    }

    private fun showProjectOptions(project: Project) {
        val options = arrayOf("상세보기", "지원자 관리", "상태 변경", "수정", "삭제")

        AlertDialog.Builder(this)
            .setTitle(project.title)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showProjectDetail(project)
                    1 -> showApplications(project)
                    2 -> showStatusChangeDialog(project)
                    3 -> showEditProjectDialog(project)
                    4 -> deleteProject(project)
                }
            }
            .show()
    }

    private fun showProjectDetail(project: Project) {
        val intent = Intent(this, ProjectDetailActivity::class.java)
        intent.putExtra("userId", currentUserId)
        intent.putExtra("projectId", project.projectId)
        startActivity(intent)
    }

    private fun showApplications(project: Project) {
        val applications = dbHelper.getApplicationsByProject(project.projectId)

        if (applications.isEmpty()) {
            Toast.makeText(this, "아직 지원자가 없습니다", Toast.LENGTH_SHORT).show()
            return
        }

        val listView = ListView(this)
        val adapter = ApplicationAdapter(this, applications, dbHelper, true) {
            loadMyProjects()
        }
        listView.adapter = adapter

        AlertDialog.Builder(this)
            .setTitle("지원자 목록 (${applications.size}명)")
            .setView(listView)
            .setNegativeButton("닫기", null)
            .show()
    }

    private fun showStatusChangeDialog(project: Project) {
        val statuses = arrayOf("recruiting", "closed", "completed")
        val statusLabels = arrayOf("모집중", "모집마감", "완료")

        val currentIndex = statuses.indexOf(project.status)

        AlertDialog.Builder(this)
            .setTitle("프로젝트 상태 변경")
            .setSingleChoiceItems(statusLabels, currentIndex) { dialog, which ->
                val newStatus = statuses[which]
                project.status = newStatus

                if (dbHelper.updateProject(project)) {
                    Toast.makeText(this, "상태가 변경되었습니다", Toast.LENGTH_SHORT).show()
                    loadMyProjects()
                    dialog.dismiss()
                } else {
                    Toast.makeText(this, "상태 변경 실패", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun showEditProjectDialog(project: Project) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_project, null)

        val editTitle = dialogView.findViewById<EditText>(R.id.editTitle)
        val editDescription = dialogView.findViewById<EditText>(R.id.editDescription)
        val gridRoles = dialogView.findViewById<GridView>(R.id.gridRoles)
        val gridSkills = dialogView.findViewById<GridView>(R.id.gridSkills)
        val editMaxMembers = dialogView.findViewById<EditText>(R.id.editMaxMembers)
        val editDuration = dialogView.findViewById<EditText>(R.id.editDuration)

        editTitle.setText(project.title)
        editDescription.setText(project.description)
        editMaxMembers.setText(project.maxMembers.toString())
        editMaxMembers.inputType = android.text.InputType.TYPE_CLASS_NUMBER
        editDuration.setText(project.duration)

        // 역할 설정
        val availableRoles = arrayOf("프론트엔드", "백엔드", "풀스택", "디자이너", "기획자", "PM", "AI/ML 엔지니어", "데이터 사이언티스트")
        val selectedRoles = ArrayList(
            project.requiredRoles.split(", ").filter { it.isNotEmpty() }
        )

        val roleAdapter = RoleGridAdapter(this, availableRoles.toList(), selectedRoles)
        gridRoles.adapter = roleAdapter

        gridRoles.setOnItemClickListener { _, _, position, _ ->
            val role = availableRoles[position]
            if (selectedRoles.contains(role)) {
                selectedRoles.remove(role)
            } else {
                selectedRoles.add(role)
            }
            roleAdapter.notifyDataSetChanged()
        }

        // 기술 스택 설정
        val availableSkills = arrayOf(
            "HTML/CSS", "JavaScript", "TypeScript", "React", "Vue.js", "Angular", "Next.js", "Tailwind CSS",
            "Java", "Kotlin", "Python", "Node.js", "Spring Boot", "Django", "Flask", "Express.js", "FastAPI",
            "Android", "iOS", "Flutter", "React Native", "Swift", "SwiftUI",
            "MySQL", "PostgreSQL", "MongoDB", "Redis", "Firebase", "SQLite",
            "AWS", "Azure", "GCP", "Docker", "Kubernetes", "GitHub Actions", "Jenkins",
            "TensorFlow", "PyTorch", "Scikit-learn", "Pandas", "NumPy", "OpenCV", "Keras", "Hugging Face",
            "Git", "REST API", "GraphQL", "WebSocket", "Linux", "Figma", "Adobe XD", "Unity", "C++", "C#", "Go", "Rust"
        )
        val selectedSkills = ArrayList(
            project.requiredSkills.split(", ").filter { it.isNotEmpty() }
        )

        val skillAdapter = RoleGridAdapter(this, availableSkills.toList(), selectedSkills)
        gridSkills.adapter = skillAdapter

        gridSkills.setOnItemClickListener { _, _, position, _ ->
            val skill = availableSkills[position]
            if (selectedSkills.contains(skill)) {
                selectedSkills.remove(skill)
            } else {
                selectedSkills.add(skill)
            }
            skillAdapter.notifyDataSetChanged()
        }

        AlertDialog.Builder(this)
            .setTitle("프로젝트 수정")
            .setView(dialogView)
            .setPositiveButton("저장") { _, _ ->
                val newTitle = editTitle.text.toString().trim()
                val newDescription = editDescription.text.toString().trim()
                val newMaxMembersStr = editMaxMembers.text.toString().trim()
                val newDuration = editDuration.text.toString().trim()

                if (newTitle.isEmpty() || newDescription.isEmpty() || newMaxMembersStr.isEmpty() || newDuration.isEmpty()) {
                    Toast.makeText(this, "모든 필수 필드를 입력하세요", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (selectedRoles.isEmpty()) {
                    Toast.makeText(this, "필요한 역할을 최소 1개 선택하세요", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val newMaxMembers = newMaxMembersStr.toIntOrNull()
                if (newMaxMembers == null || newMaxMembers < project.currentMembers) {
                    Toast.makeText(this, "모집 인원은 현재 인원(${project.currentMembers}명) 이상이어야 합니다", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                project.title = newTitle
                project.description = newDescription
                project.requiredRoles = selectedRoles.joinToString(", ")
                project.requiredSkills = selectedSkills.joinToString(", ")
                project.maxMembers = newMaxMembers
                project.duration = newDuration

                if (dbHelper.updateProject(project)) {
                    Toast.makeText(this, "프로젝트가 수정되었습니다", Toast.LENGTH_SHORT).show()
                    loadMyProjects()
                } else {
                    Toast.makeText(this, "수정 실패", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun deleteProject(project: Project) {
        AlertDialog.Builder(this)
            .setTitle("프로젝트 삭제")
            .setMessage("정말 삭제하시겠습니까? 모든 지원 내역도 함께 삭제됩니다.")
            .setPositiveButton("삭제") { _, _ ->
                if (dbHelper.deleteProject(project.projectId)) {
                    Toast.makeText(this, "프로젝트가 삭제되었습니다", Toast.LENGTH_SHORT).show()
                    loadMyProjects()
                } else {
                    Toast.makeText(this, "삭제 실패", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("취소", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        loadMyProjects()
    }
}

class MyProjectAdapter(
    private val context: android.content.Context,
    private var projects: ArrayList<Project>,
    private val dbHelper: DBHelper
) : BaseAdapter() {

    override fun getCount(): Int = projects.size

    override fun getItem(position: Int): Any = projects[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: android.view.ViewGroup?): View {
        val view = convertView ?: android.view.LayoutInflater.from(context)
            .inflate(R.layout.item_project_list, parent, false)

        val project = projects[position]

        view.findViewById<TextView>(R.id.textProjectTitle).text = project.title
        view.findViewById<TextView>(R.id.textProjectCreator).text = "내 프로젝트"
        view.findViewById<TextView>(R.id.textProjectMembers).text = "👥 ${project.currentMembers}/${project.maxMembers}명"
        view.findViewById<TextView>(R.id.textProjectDuration).text = "⏱️ ${project.duration}"
        view.findViewById<TextView>(R.id.textProjectRoles).text = project.requiredRoles

        val textStatus = view.findViewById<TextView>(R.id.textProjectStatus)
        when (project.status) {
            "recruiting" -> {
                textStatus.text = "모집중"
                textStatus.setBackgroundColor(android.graphics.Color.parseColor("#4CAF50"))
                textStatus.setTextColor(android.graphics.Color.WHITE)
            }
            "closed" -> {
                textStatus.text = "모집마감"
                textStatus.setBackgroundColor(android.graphics.Color.parseColor("#9E9E9E"))
                textStatus.setTextColor(android.graphics.Color.WHITE)
            }
            "completed" -> {
                textStatus.text = "완료"
                textStatus.setBackgroundColor(android.graphics.Color.parseColor("#2196F3"))
                textStatus.setTextColor(android.graphics.Color.WHITE)
            }
        }

        val dateFormat = java.text.SimpleDateFormat("MM/dd HH:mm", java.util.Locale.getDefault())
        view.findViewById<TextView>(R.id.textProjectDate).text = dateFormat.format(java.util.Date(project.createdAt))

        return view
    }

    fun updateData(newProjects: ArrayList<Project>) {
        projects = newProjects
        notifyDataSetChanged()
    }
}