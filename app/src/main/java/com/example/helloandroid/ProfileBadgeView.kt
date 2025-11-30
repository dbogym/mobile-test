package com.example.helloandroid

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

// 10주차: Canvas 그래픽
class ProfileBadgeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var userName: String = ""
    private var receivedInterests: Int = 0
    private var userRole: String = ""

    private val paint = Paint().apply {
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
    }

    fun setBadgeData(name: String, interests: Int, role: String) {
        this.userName = name
        this.receivedInterests = interests
        this.userRole = role
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f

        // 배경 원
        paint.color = Color.parseColor("#6200EE")
        paint.style = Paint.Style.FILL
        canvas.drawCircle(centerX, centerY - 20, 80f, paint)

        // 이름
        paint.color = Color.WHITE
        paint.textSize = 50f
        paint.style = Paint.Style.FILL
        canvas.drawText(userName, centerX, centerY, paint)

        // 역할
        paint.textSize = 30f
        canvas.drawText(userRole, centerX, centerY + 40, paint)

        // Level 계산
        val level = when {
            receivedInterests >= 21 -> 5
            receivedInterests >= 11 -> 4
            receivedInterests >= 6 -> 3
            receivedInterests >= 3 -> 2
            else -> 1
        }

        // Level 표시
        paint.color = Color.parseColor("#FFD700")
        paint.textSize = 40f
        canvas.drawText("Level $level", centerX, centerY + 80, paint)

        // 별 표시
        val stars = "⭐".repeat(level)
        canvas.drawText(stars, centerX, centerY + 120, paint)

        // 받은 관심 수
        paint.color = Color.parseColor("#FF4081")
        paint.textSize = 25f
        canvas.drawText("받은 관심: $receivedInterests", centerX, centerY + 155, paint)
    }
}
