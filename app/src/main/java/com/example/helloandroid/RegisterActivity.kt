package com.example.helloandroid

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var editUserId: EditText
    private lateinit var editPassword: EditText
    private lateinit var editName: EditText
    private lateinit var editContact: EditText
    private lateinit var btnRegister: Button
    private lateinit var btnBack: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        dbHelper = DBHelper(this)

        editUserId = findViewById(R.id.editUserId)
        editPassword = findViewById(R.id.editPassword)
        editName = findViewById(R.id.editName)
        editContact = findViewById(R.id.editContact)
        btnRegister = findViewById(R.id.btnRegister)
        btnBack = findViewById(R.id.btnBack)

        btnRegister.setOnClickListener {
            val userId = editUserId.text.toString().trim()
            val password = editPassword.text.toString().trim()
            val name = editName.text.toString().trim()
            val contact = editContact.text.toString().trim()

            if (userId.isEmpty() || password.isEmpty() || name.isEmpty() || contact.isEmpty()) {
                Toast.makeText(this, "모든 정보를 입력하세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 중복 체크
            val existingUser = dbHelper.getUser(userId)
            if (existingUser != null) {
                Toast.makeText(this, "이미 존재하는 학번입니다", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (dbHelper.registerUser(userId, password, name, contact)) {
                Toast.makeText(this, "회원가입 성공!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "회원가입 실패", Toast.LENGTH_SHORT).show()
            }
        }

        btnBack.setOnClickListener {
            finish()
        }
    }
}
