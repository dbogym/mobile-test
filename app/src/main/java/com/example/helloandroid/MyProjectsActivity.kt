package com.example.helloandroid

import android.content.Intent
import android.os.Bundle
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
            textNoProjects.visibility = android.view.View.VISIBLE
            listViewMyProjects.visibility = android.view.View.GONE
        } else {
            textNoProjects.visibility = android.view.View.GONE
            listViewMyProjects.visibility = android.view.View.VISIBLE

            if (projectAdapter == null) {
                projectAdapter = MyProjectAdapter(this, myProjects, dbHelper)
                listViewMyProjects.adapter = projectAdapter
            } else {
                projectAdapter?.updateData(myProjects)
            }
        }
    }

    private fun showProjectOptions(project: Project) {
        val options = arrayOf("상세보기", "지원자 관리", "모집마감", "수정", "삭제")

        AlertDialog.Builder(this)
            .setTitle(project.title)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showProjectDetail(project)
                    1 -> showApplications(project)
                    2 -> closeProject(project)
                    3 -> editProject(project)
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

        val dialogView = layoutInflater.inflate(android.R.layout.select_dialog_item, null)
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

    private fun closeProject(project: Project) {
        if (project.status == "closed") {
            Toast.makeText(this, "이미 모집 마감된 프로젝트입니다", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("모집 마감")
            .setMessage("정말 모집을 마감하시겠습니까?")
            .setPositiveButton("마감") { _, _ ->
                if (dbHelper.closeProject(project.projectId)) {
                    Toast.makeText(this, "모집이 마감되었습니다", Toast.LENGTH_SHORT).show()
                    loadMyProjects()
                } else {
                    Toast.makeText(this, "모집 마감 실패", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun editProject(project: Project) {
        Toast.makeText(this, "수정 기능은 추후 구현 예정입니다", Toast.LENGTH_SHORT).show()
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

    override fun getView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup?): android.view.View {
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