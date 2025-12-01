package com.example.helloandroid

import android.content.Context
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
    private val onDataChanged: () -> Unit
) : BaseAdapter() {

    override fun getCount(): Int = applications.size

    override fun getItem(position: Int): Any = applications[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: android.view.LayoutInflater.from(context)
            .inflate(R.layout.item_application_list, parent, false)

        val application = applications[position]
        val applicant = dbHelper.getUser(application.userId)

        view.findViewById<TextView>(R.id.textApplicantName).text = application.userName
        view.findViewById<TextView>(R.id.textApplicantRole).text = "지원 역할: ${application.userRole}"
        view.findViewById<TextView>(R.id.textApplicantSkills).text =
            "보유 기술: ${applicant?.skills?.ifEmpty { "없음" } ?: "없음"}"
        view.findViewById<TextView>(R.id.textApplicantMessage).text = application.message
        view.findViewById<TextView>(R.id.textApplicantContact).text =
            "연락처: ${applicant?.contact ?: "없음"}"

        val btnApprove = view.findViewById<Button>(R.id.btnApprove)
        val btnReject = view.findViewById<Button>(R.id.btnReject)
        val textApplicationStatus = view.findViewById<TextView>(R.id.textApplicantStatus)

        if (!isCreator) {
            // 본인이 프로젝트 생성자가 아닌 경우 (팀원인 경우)
            btnApprove.visibility = View.GONE
            btnReject.visibility = View.GONE
            textApplicationStatus.visibility = View.VISIBLE

            when (application.status) {
                "pending" -> {
                    textApplicationStatus.text = "대기중"
                    textApplicationStatus.setBackgroundColor(android.graphics.Color.parseColor("#FF9800"))
                }
                "approved" -> {
                    textApplicationStatus.text = "승인됨"
                    textApplicationStatus.setBackgroundColor(android.graphics.Color.parseColor("#4CAF50"))
                }
                "rejected" -> {
                    textApplicationStatus.text = "거절됨"
                    textApplicationStatus.setBackgroundColor(android.graphics.Color.parseColor("#F44336"))
                }
            }
        } else {
            // 본인이 프로젝트 생성자인 경우
            when (application.status) {
                "pending" -> {
                    btnApprove.visibility = View.VISIBLE
                    btnReject.visibility = View.VISIBLE
                    textApplicationStatus.visibility = View.GONE

                    btnApprove.setOnClickListener {
                        val result = dbHelper.approveApplication(
                            application.applicationId,
                            application.projectId,
                            application.userId,
                            application.userRole
                        )
                        if (result) {
                            Toast.makeText(context, "${application.userName}님의 지원을 승인했습니다", Toast.LENGTH_SHORT).show()
                            onDataChanged()
                        } else {
                            Toast.makeText(context, "승인 실패", Toast.LENGTH_SHORT).show()
                        }
                    }

                    btnReject.setOnClickListener {
                        val result = dbHelper.rejectApplication(application.applicationId)
                        if (result) {
                            Toast.makeText(context, "${application.userName}님의 지원을 거절했습니다", Toast.LENGTH_SHORT).show()
                            onDataChanged()
                        } else {
                            Toast.makeText(context, "거절 실패", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                "approved" -> {
                    btnApprove.visibility = View.GONE
                    btnReject.visibility = View.GONE
                    textApplicationStatus.visibility = View.VISIBLE
                    textApplicationStatus.text = "승인됨"
                    textApplicationStatus.setBackgroundColor(android.graphics.Color.parseColor("#4CAF50"))
                }
                "rejected" -> {
                    btnApprove.visibility = View.GONE
                    btnReject.visibility = View.GONE
                    textApplicationStatus.visibility = View.VISIBLE
                    textApplicationStatus.text = "거절됨"
                    textApplicationStatus.setBackgroundColor(android.graphics.Color.parseColor("#F44336"))
                }
            }
        }

        return view
    }

    fun updateData(newApplications: ArrayList<Application>) {
        applications = newApplications
        notifyDataSetChanged()
    }
}