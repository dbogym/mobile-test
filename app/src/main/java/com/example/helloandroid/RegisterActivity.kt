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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        dbHelper = DBHelper(this)

        editUserId = findViewById(R.id.editRegisterUserId)
        editPassword = findViewById(R.id.editRegisterPassword)
        editName = findViewById(R.id.editRegisterName)
        editContact = findViewById(R.id.editRegisterContact)
        btnRegister = findViewById(R.id.btnRegisterSubmit)

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

            val newUser = User(
                userId = userId,
                password = password,
                name = name,
                contact = contact
            )

            if (dbHelper.registerUser(newUser)) {
                Toast.makeText(this, "회원가입 성공!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "회원가입 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }
}