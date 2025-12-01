package com.example.helloandroid

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

class ProjectListAdapter(
    private val context: Context,
    private var projects: ArrayList<Project>,
    private val dbHelper: DBHelper
) : BaseAdapter() {

    override fun getCount(): Int = projects.size

    override fun getItem(position: Int): Any = projects[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_project_list, parent, false)

        val project = projects[position]
        val creator = dbHelper.getUser(project.creatorId)

        view.findViewById<TextView>(R.id.textProjectTitle).text = project.title
        view.findViewById<TextView>(R.id.textProjectCreator).text = "팀장: ${creator?.name ?: "알 수 없음"}"
        view.findViewById<TextView>(R.id.textProjectMembers).text =
            "👥 ${project.currentMembers}/${project.maxMembers}명"
        view.findViewById<TextView>(R.id.textProjectDuration).text = "⏱️ ${project.duration}"
        view.findViewById<TextView>(R.id.textProjectRoles).text = project.requiredRoles

        // 상태 표시
        val textStatus = view.findViewById<TextView>(R.id.textProjectStatus)
        when (project.status) {
            "recruiting" -> {
                textStatus.text = "모집중"
                textStatus.setBackgroundColor(Color.parseColor("#4CAF50"))
                textStatus.setTextColor(Color.WHITE)
            }
            "closed" -> {
                textStatus.text = "모집마감"
                textStatus.setBackgroundColor(Color.parseColor("#9E9E9E"))
                textStatus.setTextColor(Color.WHITE)
            }
            "completed" -> {
                textStatus.text = "완료"
                textStatus.setBackgroundColor(Color.parseColor("#2196F3"))
                textStatus.setTextColor(Color.WHITE)
            }
        }

        // 날짜 표시
        val dateFormat = SimpleDateFormat("MM/dd HH:mm", Locale.getDefault())
        view.findViewById<TextView>(R.id.textProjectDate).text =
            dateFormat.format(Date(project.createdAt))

        return view
    }

    fun updateData(newProjects: ArrayList<Project>) {
        projects = newProjects
        notifyDataSetChanged()
    }
}
