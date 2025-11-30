package com.example.helloandroid

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MyApplicationsActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var currentUserId: String

    private lateinit var listViewMyApplications: ListView
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
        loadMyApplications()
    }

    private fun initViews() {
        listViewMyApplications = findViewById(R.id.listViewMyApplications)
        textNoApplications = findViewById(R.id.textNoApplications)
    }

    private fun loadMyApplications() {
        val myApplications = dbHelper.getMyApplications(currentUserId)

        if (myApplications.isEmpty()) {
            textNoApplications.visibility = android.view.View.VISIBLE
            listViewMyApplications.visibility = android.view.View.GONE
        } else {
            textNoApplications.visibility = android.view.View.GONE
            listViewMyApplications.visibility = android.view.View.VISIBLE

            if (applicationAdapter == null) {
                applicationAdapter = MyApplicationAdapter(this, myApplications)
                listViewMyApplications.adapter = applicationAdapter
            } else {
                applicationAdapter?.updateData(myApplications)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadMyApplications()
    }
}

class MyApplicationAdapter(
    private val context: android.content.Context,
    private var applications: ArrayList<ApplicationWithProject>
) : BaseAdapter() {

    override fun getCount(): Int = applications.size

    override fun getItem(position: Int): Any = applications[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup?): android.view.View {
        val view = convertView ?: android.view.LayoutInflater.from(context)
            .inflate(R.layout.item_my_application, parent, false)

        val application = applications[position]

        view.findViewById<TextView>(R.id.textApplicationProject).text = application.projectTitle
        view.findViewById<TextView>(R.id.textApplicationRole).text = "지원 역할: ${application.applyRole}"
        view.findViewById<TextView>(R.id.textApplicationMessage).text = application.message

        val textStatus = view.findViewById<TextView>(R.id.textApplicationStatus)
        when (application.status) {
            "pending" -> {
                textStatus.text = "⏳ 대기중"
                textStatus.setBackgroundColor(android.graphics.Color.parseColor("#FFA726"))
                textStatus.setTextColor(android.graphics.Color.WHITE)
            }
            "approved" -> {
                textStatus.text = "✅ 승인됨"
                textStatus.setBackgroundColor(android.graphics.Color.parseColor("#4CAF50"))
                textStatus.setTextColor(android.graphics.Color.WHITE)
            }
            "rejected" -> {
                textStatus.text = "❌ 거절됨"
                textStatus.setBackgroundColor(android.graphics.Color.parseColor("#F44336"))
                textStatus.setTextColor(android.graphics.Color.WHITE)
            }
        }

        val dateFormat = java.text.SimpleDateFormat("MM/dd HH:mm", java.util.Locale.getDefault())
        view.findViewById<TextView>(R.id.textApplicationDate).text =
            "지원일: ${dateFormat.format(java.util.Date(application.appliedAt))}"

        return view
    }

    fun updateData(newApplications: ArrayList<ApplicationWithProject>) {
        applications = newApplications
        notifyDataSetChanged()
    }
}