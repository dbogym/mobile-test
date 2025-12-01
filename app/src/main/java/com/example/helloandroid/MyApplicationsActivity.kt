package com.example.helloandroid

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

// Application과 Project 정보를 함께 담는 데이터 클래스
data class ApplicationWithProject(
    val application: Application,
    val projectTitle: String,
    val projectStatus: String
)

class MyApplicationsActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var currentUserId: String

    private lateinit var listViewApplications: ListView
    private lateinit var textNoApplications: TextView
    private var applicationAdapter: MyApplicationAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_applications)

        dbHelper = DBHelper(this)
        currentUserId = intent.getStringExtra("userId") ?: run {
            finish()
            return
        }

        initViews()
        loadApplications()
    }

    private fun initViews() {
        listViewApplications = findViewById(R.id.listViewMyApplications)
        textNoApplications = findViewById(R.id.textNoApplications)

        listViewApplications.setOnItemClickListener { _, _, position, _ ->
            val appWithProject = applicationAdapter?.getItem(position) as? ApplicationWithProject ?: return@setOnItemClickListener
            val intent = Intent(this, ProjectDetailActivity::class.java)
            intent.putExtra("userId", currentUserId)
            intent.putExtra("projectId", appWithProject.application.projectId)
            startActivity(intent)
        }
    }

    private fun loadApplications() {
        val applications = dbHelper.getMyApplications(currentUserId)

        if (applications.isEmpty()) {
            textNoApplications.visibility = View.VISIBLE
            listViewApplications.visibility = View.GONE
        } else {
            textNoApplications.visibility = View.GONE
            listViewApplications.visibility = View.VISIBLE

            // Application과 Project 정보를 함께 가져오기
            val applicationsWithProjects = applications.map { app ->
                val project = dbHelper.getProject(app.projectId)
                ApplicationWithProject(
                    application = app,
                    projectTitle = project?.title ?: "알 수 없는 프로젝트",
                    projectStatus = project?.status ?: "unknown"
                )
            }

            if (applicationAdapter == null) {
                applicationAdapter = MyApplicationAdapter(this, ArrayList(applicationsWithProjects))
                listViewApplications.adapter = applicationAdapter
            } else {
                applicationAdapter?.updateData(ArrayList(applicationsWithProjects))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadApplications()
    }
}

class MyApplicationAdapter(
    private val context: android.content.Context,
    private var applications: ArrayList<ApplicationWithProject>
) : BaseAdapter() {

    override fun getCount(): Int = applications.size

    override fun getItem(position: Int): Any = applications[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: android.view.ViewGroup?): View {
        val view = convertView ?: android.view.LayoutInflater.from(context)
            .inflate(R.layout.item_my_application, parent, false)

        val appWithProject = applications[position]
        val app = appWithProject.application

        view.findViewById<TextView>(R.id.textApplicationProject).text = appWithProject.projectTitle
        view.findViewById<TextView>(R.id.textApplicationRole).text = "지원 역할: ${app.userRole}"
        view.findViewById<TextView>(R.id.textApplicationMessage).text = app.message

        val textStatus = view.findViewById<TextView>(R.id.textApplicationStatus)
        when (app.status) {
            "pending" -> {
                textStatus.text = "대기중"
                textStatus.setBackgroundColor(android.graphics.Color.parseColor("#FF9800"))
                textStatus.setTextColor(android.graphics.Color.WHITE)
            }
            "approved" -> {
                textStatus.text = "승인됨"
                textStatus.setBackgroundColor(android.graphics.Color.parseColor("#4CAF50"))
                textStatus.setTextColor(android.graphics.Color.WHITE)
            }
            "rejected" -> {
                textStatus.text = "거절됨"
                textStatus.setBackgroundColor(android.graphics.Color.parseColor("#F44336"))
                textStatus.setTextColor(android.graphics.Color.WHITE)
            }
        }

        val dateFormat = java.text.SimpleDateFormat("MM/dd HH:mm", java.util.Locale.getDefault())
        view.findViewById<TextView>(R.id.textApplicationDate).text =
            "지원일: ${dateFormat.format(java.util.Date(app.appliedAt))}"

        return view
    }

    fun updateData(newApplications: ArrayList<ApplicationWithProject>) {
        applications = newApplications
        notifyDataSetChanged()
    }
}