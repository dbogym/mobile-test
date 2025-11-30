package com.example.helloandroid

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class ApplicationAdapter(
    private val context: Context,
    private var applications: ArrayList<Application>,
    private val dbHelper: DBHelper,
    private val isCreator: Boolean,
    private val onActionComplete: () -> Unit
) : BaseAdapter() {

    override fun getCount(): Int = applications.size

    override fun getItem(position: Int): Any = applications[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_application_list, parent, false)

        val application = applications[position]

        view.findViewById<TextView>(R.id.textApplicantName).text = application.userName
        view.findViewById<TextView>(R.id.textApplicantRole).text =
            "${application.userRole} → ${application.applyRole} 지원"
        view.findViewById<TextView>(R.id.textApplicantSkills).text = application.userSkills
        view.findViewById<TextView>(R.id.textApplicantMessage).text = application.message
        view.findViewById<TextView>(R.id.textApplicantContact).text = "📞 ${application.userContact}"

        val textStatus = view.findViewById<TextView>(R.id.textApplicantStatus)
        val btnApprove = view.findViewById<Button>(R.id.btnApprove)
        val btnReject = view.findViewById<Button>(R.id.btnReject)

        when (application.status) {
            "pending" -> {
                textStatus.text = "⏳ 대기중"
                textStatus.setBackgroundColor(android.graphics.Color.parseColor("#FFA726"))
                textStatus.setTextColor(android.graphics.Color.WHITE)

                if (isCreator) {
                    btnApprove.visibility = View.VISIBLE
                    btnReject.visibility = View.VISIBLE
                } else {
                    btnApprove.visibility = View.GONE
                    btnReject.visibility = View.GONE
                }
            }
            "approved" -> {
                textStatus.text = "✅ 승인됨"
                textStatus.setBackgroundColor(android.graphics.Color.parseColor("#4CAF50"))
                textStatus.setTextColor(android.graphics.Color.WHITE)
                btnApprove.visibility = View.GONE
                btnReject.visibility = View.GONE
            }
            "rejected" -> {
                textStatus.text = "❌ 거절됨"
                textStatus.setBackgroundColor(android.graphics.Color.parseColor("#F44336"))
                textStatus.setTextColor(android.graphics.Color.WHITE)
                btnApprove.visibility = View.GONE
                btnReject.visibility = View.GONE
            }
        }

        btnApprove.setOnClickListener {
            if (dbHelper.approveApplication(application.applicationId)) {
                Toast.makeText(context, "${application.userName}님을 승인했습니다", Toast.LENGTH_SHORT).show()
                onActionComplete()
            } else {
                Toast.makeText(context, "승인 실패", Toast.LENGTH_SHORT).show()
            }
        }

        btnReject.setOnClickListener {
            if (dbHelper.rejectApplication(application.applicationId)) {
                Toast.makeText(context, "${application.userName}님을 거절했습니다", Toast.LENGTH_SHORT).show()
                onActionComplete()
            } else {
                Toast.makeText(context, "거절 실패", Toast.LENGTH_SHORT).show()
            }
        }

        val dateFormat = java.text.SimpleDateFormat("MM/dd HH:mm", java.util.Locale.getDefault())
        view.findViewById<TextView>(R.id.textApplicationDate).text =
            "지원일: ${dateFormat.format(java.util.Date(application.appliedAt))}"

        return view
    }

    fun updateData(newApplications: ArrayList<Application>) {
        applications = newApplications
        notifyDataSetChanged()
    }
}