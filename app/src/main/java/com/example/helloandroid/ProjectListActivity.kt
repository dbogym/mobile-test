package com.example.helloandroid

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class ProjectListActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var currentUserId: String

    private lateinit var btnCreateProject: Button
    private lateinit var spinnerStatusFilter: Spinner
    private lateinit var textProjectCount: TextView
    private lateinit var listViewProjects: ListView
    private lateinit var btnMyProjects: Button
    private lateinit var btnMyApplications: Button

    private var projectListAdapter: ProjectListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_list)

        dbHelper = DBHelper(this)
        currentUserId = intent.getStringExtra("userId") ?: run {
            finish()
            return
        }

        initViews()
        setupFilters()
        setupButtons()
        loadProjects()
    }

    private fun initViews() {
        btnCreateProject = findViewById(R.id.btnCreateProject)
        spinnerStatusFilter = findViewById(R.id.spinnerStatusFilter)
        textProjectCount = findViewById(R.id.textProjectCount)
        listViewProjects = findViewById(R.id.listViewProjects)
        btnMyProjects = findViewById(R.id.btnMyProjects)
        btnMyApplications = findViewById(R.id.btnMyApplications)
    }

    private fun setupFilters() {
        // 상태 필터
        val statuses = arrayOf("전체", "recruiting", "closed", "completed")
        val statusLabels = arrayOf("전체", "모집중", "모집마감", "완료")
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
    }

    private fun setupButtons() {
        btnCreateProject.setOnClickListener {
            val intent = Intent(this, CreateProjectActivity::class.java)
            intent.putExtra("userId", currentUserId)
            startActivity(intent)
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

    override fun onResume() {
        super.onResume()
        // 목록 새로고침
        val selectedPosition = spinnerStatusFilter.selectedItemPosition
        val statuses = arrayOf("전체", "recruiting", "closed", "completed")
        loadProjects(statuses[selectedPosition])
    }
}
