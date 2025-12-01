package com.example.helloandroid

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

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

    private val cardPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    fun setBadgeData(name: String, interests: Int, role: String) {
        this.userName = name
        this.receivedInterests = interests
        this.userRole = role
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()
        val centerX = width / 2f
        val centerY = height / 2f

        // 그라디언트 배경 카드
        val gradient = LinearGradient(
            0f, 0f, width, height,
            intArrayOf(
                Color.parseColor("#667eea"),
                Color.parseColor("#764ba2")
            ),
            null,
            Shader.TileMode.CLAMP
        )
        cardPaint.shader = gradient

        val cornerRadius = 24f
        val cardRect = RectF(20f, 20f, width - 20f, height - 20f)
        canvas.drawRoundRect(cardRect, cornerRadius, cornerRadius, cardPaint)

        // 그림자 효과를 위한 반투명 레이어
        paint.color = Color.argb(30, 0, 0, 0)
        paint.style = Paint.Style.FILL
        val shadowRect = RectF(24f, 24f, width - 16f, height - 16f)
        canvas.drawRoundRect(shadowRect, cornerRadius, cornerRadius, paint)

        // 이름
        paint.color = Color.WHITE
        paint.textSize = 42f
        paint.style = Paint.Style.FILL
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText(userName, centerX, centerY - 20, paint)

        // 역할
        paint.textSize = 26f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.color = Color.parseColor("#E0E0E0")
        canvas.drawText(userRole, centerX, centerY + 20, paint)

        // 받은 관심 카드
        val interestCardTop = centerY + 50
        val interestCardHeight = 50f

        // 반투명 흰색 배경
        paint.color = Color.argb(40, 255, 255, 255)
        paint.style = Paint.Style.FILL
        val interestRect = RectF(
            centerX - 120f,
            interestCardTop,
            centerX + 120f,
            interestCardTop + interestCardHeight
        )
        canvas.drawRoundRect(interestRect, 16f, 16f, paint)

        // 하트 아이콘
        paint.color = Color.parseColor("#FF4081")
        paint.textSize = 28f
        canvas.drawText("❤", centerX - 60f, interestCardTop + 34f, paint)

        // 받은 관심 수
        paint.color = Color.WHITE
        paint.textSize = 32f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("$receivedInterests", centerX, interestCardTop + 36f, paint)

        // "관심" 텍스트
        paint.textSize = 18f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.color = Color.parseColor("#E0E0E0")
        canvas.drawText("관심", centerX + 50f, interestCardTop + 32f, paint)
    }
}