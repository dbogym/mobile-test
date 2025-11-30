package com.example.helloandroid

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class UserListAdapter(
    private val context: Context,
    private var users: ArrayList<User>,
    private val currentUserId: String,
    private val dbHelper: DBHelper,
    private val onInterestClick: (User) -> Unit
) : BaseAdapter() {

    override fun getCount(): Int = users.size

    override fun getItem(position: Int): Any = users[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_user_list, parent, false)

        val user = users[position]

        view.findViewById<TextView>(R.id.textUserName).text = user.name
        view.findViewById<TextView>(R.id.textUserRole).text = user.role.ifEmpty { "역할 미정" }
        view.findViewById<TextView>(R.id.textUserSkills).text = user.skills.ifEmpty { "기술 스택 미정" }
        view.findViewById<TextView>(R.id.textUserInterestCount).text = "❤️ ${user.receivedInterests}"

        val btnInterest = view.findViewById<Button>(R.id.btnUserInterest)
        val isInterested = dbHelper.isInterestExists(currentUserId, user.userId)

        if (isInterested) {
            btnInterest.text = "❤️ 관심 취소"
            btnInterest.setBackgroundColor(android.graphics.Color.parseColor("#E0E0E0"))
        } else {
            btnInterest.text = "💝 관심 표시"
            btnInterest.setBackgroundColor(android.graphics.Color.parseColor("#FF4081"))
        }

        btnInterest.setOnClickListener {
            if (isInterested) {
                if (dbHelper.deleteInterest(currentUserId, user.userId)) {
                    Toast.makeText(context, "관심 취소", Toast.LENGTH_SHORT).show()
                    onInterestClick(user)
                }
            } else {
                if (dbHelper.saveInterest(currentUserId, user.userId)) {
                    Toast.makeText(context, "관심 표시 완료", Toast.LENGTH_SHORT).show()
                    onInterestClick(user)
                }
            }
        }

        return view
    }

    fun updateData(newUsers: ArrayList<User>) {
        users = newUsers
        notifyDataSetChanged()
    }
}
