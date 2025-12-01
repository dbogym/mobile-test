package com.example.helloandroid

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var editUserId: EditText
    private lateinit var editPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnGoToRegister: Button
    private lateinit var textLoginInfo: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        dbHelper = DBHelper(this)

        editUserId = findViewById(R.id.editLoginUserId)
        editPassword = findViewById(R.id.editLoginPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnGoToRegister = findViewById(R.id.btnRegister)
        textLoginInfo = findViewById(R.id.textLoginInfo)

        updateUserCount()

        btnLogin.setOnClickListener {
            val userId = editUserId.text.toString().trim()
            val password = editPassword.text.toString().trim()

            if (userId.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "학번과 비밀번호를 입력하세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = dbHelper.loginUser(userId, password)
            if (user != null) {
                Toast.makeText(this, "${user.name}님 환영합니다!", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("userId", userId)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "학번 또는 비밀번호가 잘못되었습니다", Toast.LENGTH_SHORT).show()
            }
        }

        btnGoToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        updateUserCount()
    }

    private fun updateUserCount() {
        val userCount = dbHelper.getUserCount()
        textLoginInfo.text = "현재 등록된 회원: ${userCount}명"
    }
}