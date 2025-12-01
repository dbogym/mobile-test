package com.example.helloandroid

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class SkillGridAdapter(
    private val context: Context,
    private val skills: ArrayList<String>,
    private val selectedSkills: ArrayList<String>
) : BaseAdapter() {

    override fun getCount(): Int = skills.size

    override fun getItem(position: Int): Any = skills[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val textView: TextView = if (convertView == null) {
            TextView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    120
                )
                setPadding(12, 12, 12, 12)
                gravity = android.view.Gravity.CENTER
                textSize = 12f
            }
        } else {
            convertView as TextView
        }

        val skill = skills[position]
        textView.text = skill

        if (selectedSkills.contains(skill)) {
            textView.setBackgroundColor(Color.parseColor("#4CAF50"))
            textView.setTextColor(Color.WHITE)
        } else {
            textView.setBackgroundColor(Color.parseColor("#E0E0E0"))
            textView.setTextColor(Color.BLACK)
        }

        return textView
    }

    companion object {
        fun getDefaultSkills(): ArrayList<String> {
            return arrayListOf(
                // 프론트엔드
                "HTML/CSS",
                "JavaScript",
                "TypeScript",
                "React",
                "Vue.js",
                "Angular",
                "Next.js",
                "Tailwind CSS",

                // 백엔드
                "Java",
                "Kotlin",
                "Python",
                "Node.js",
                "Spring Boot",
                "Django",
                "Flask",
                "Express.js",
                "FastAPI",

                // 모바일
                "Android",
                "iOS",
                "Flutter",
                "React Native",
                "Swift",
                "SwiftUI",

                // 데이터베이스
                "MySQL",
                "PostgreSQL",
                "MongoDB",
                "Redis",
                "Firebase",
                "SQLite",

                // 클라우드/DevOps
                "AWS",
                "Azure",
                "GCP",
                "Docker",
                "Kubernetes",
                "GitHub Actions",
                "Jenkins",

                // AI/ML & 데이터 과학
                "TensorFlow",
                "PyTorch",
                "Scikit-learn",
                "Pandas",
                "NumPy",
                "OpenCV",
                "Keras",
                "Hugging Face",

                // 기타
                "Git",
                "REST API",
                "GraphQL",
                "WebSocket",
                "Linux",
                "Figma",
                "Adobe XD",
                "Unity",
                "C++",
                "C#",
                "Go",
                "Rust"
            )
        }
    }
}