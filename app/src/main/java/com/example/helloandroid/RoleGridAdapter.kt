package com.example.helloandroid

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

// GridView용 역할 어댑터
class RoleGridAdapter(
    private val context: Context,
    private val items: List<String>,
    private val selectedItems: ArrayList<String>
) : BaseAdapter() {

    override fun getCount(): Int = items.size

    override fun getItem(position: Int): Any = items[position]

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

        val item = items[position]
        textView.text = item

        // 선택된 항목: 초록색, 미선택: 회색
        if (selectedItems.contains(item)) {
            textView.setBackgroundColor(Color.parseColor("#4CAF50"))
            textView.setTextColor(Color.WHITE)
        } else {
            textView.setBackgroundColor(Color.parseColor("#E0E0E0"))
            textView.setTextColor(Color.BLACK)
        }

        return textView
    }
}
