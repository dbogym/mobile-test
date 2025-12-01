package com.example.helloandroid

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// User 데이터 클래스
data class User(
    val userId: String,
    var password: String,
    var name: String,
    var contact: String,
    var role: String = "",
    var skills: String = "",
    var experience: String = "",
    var strength: String = "",
    var interests: String = "",
    var preferredTeammate: String = "",
    var collaborationStyle: String = "",
    var github: String = "",
    var receivedInterests: Int = 0
)

// Project 데이터 클래스
data class Project(
    val projectId: Int = 0,
    val creatorId: String,
    var title: String,
    var description: String,
    var requiredRoles: String,
    var requiredSkills: String,
    var maxMembers: Int,
    var currentMembers: Int = 1,
    var duration: String,
    var status: String = "recruiting",
    val createdAt: Long = System.currentTimeMillis()
)

// Application 데이터 클래스
data class Application(
    val applicationId: Int = 0,
    val projectId: Int,
    val userId: String,
    val userName: String,
    val userRole: String,
    val message: String,
    val status: String = "pending",
    val appliedAt: Long = System.currentTimeMillis()
)

class DBHelper(context: Context) : SQLiteOpenHelper(context, "TeamBuilding.db", null, 2) {

    override fun onCreate(db: SQLiteDatabase?) {
        // 사용자 테이블
        db?.execSQL("""
            CREATE TABLE userTBL (
                userId TEXT PRIMARY KEY,
                password TEXT,
                name TEXT,
                contact TEXT,
                role TEXT,
                skills TEXT,
                experience TEXT,
                strength TEXT,
                interests TEXT,
                preferredTeammate TEXT,
                collaborationStyle TEXT,
                github TEXT,
                receivedInterests INTEGER DEFAULT 0
            )
        """)

        // 관심 테이블
        db?.execSQL("""
            CREATE TABLE interestTBL (
                fromUserId TEXT,
                toUserId TEXT,
                timestamp INTEGER,
                PRIMARY KEY (fromUserId, toUserId)
            )
        """)

        // 프로젝트 테이블
        db?.execSQL("""
            CREATE TABLE projectTBL (
                projectId INTEGER PRIMARY KEY AUTOINCREMENT,
                creatorId TEXT,
                title TEXT,
                description TEXT,
                requiredRoles TEXT,
                requiredSkills TEXT,
                maxMembers INTEGER,
                currentMembers INTEGER DEFAULT 1,
                duration TEXT,
                status TEXT DEFAULT 'recruiting',
                createdAt INTEGER
            )
        """)

        // 지원 테이블
        db?.execSQL("""
            CREATE TABLE applicationTBL (
                applicationId INTEGER PRIMARY KEY AUTOINCREMENT,
                projectId INTEGER,
                userId TEXT,
                userName TEXT,
                userRole TEXT,
                message TEXT,
                status TEXT DEFAULT 'pending',
                appliedAt INTEGER
            )
        """)

        // 멤버 테이블
        db?.execSQL("""
            CREATE TABLE memberTBL (
                projectId INTEGER,
                userId TEXT,
                role TEXT,
                joinedAt INTEGER,
                PRIMARY KEY (projectId, userId)
            )
        """)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS userTBL")
        db?.execSQL("DROP TABLE IF EXISTS interestTBL")
        db?.execSQL("DROP TABLE IF EXISTS projectTBL")
        db?.execSQL("DROP TABLE IF EXISTS applicationTBL")
        db?.execSQL("DROP TABLE IF EXISTS memberTBL")
        onCreate(db)
    }

    // 사용자 관련 메서드
    fun loginUser(userId: String, password: String): User? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM userTBL WHERE userId = ? AND password = ?",
            arrayOf(userId, password)
        )

        return if (cursor.moveToFirst()) {
            val user = User(
                userId = cursor.getString(0),
                password = cursor.getString(1),
                name = cursor.getString(2),
                contact = cursor.getString(3),
                role = cursor.getString(4),
                skills = cursor.getString(5),
                experience = cursor.getString(6),
                strength = cursor.getString(7),
                interests = cursor.getString(8),
                preferredTeammate = cursor.getString(9),
                collaborationStyle = cursor.getString(10),
                github = cursor.getString(11),
                receivedInterests = cursor.getInt(12)
            )
            cursor.close()
            user
        } else {
            cursor.close()
            null
        }
    }

    fun registerUser(user: User): Boolean {
        val db = writableDatabase

        // 이미 존재하는 userId인지 확인
        val cursor = db.rawQuery("SELECT userId FROM userTBL WHERE userId = ?", arrayOf(user.userId))
        val exists = cursor.moveToFirst()
        cursor.close()

        if (exists) {
            return false
        }

        return saveUser(user)
    }

    fun saveUser(user: User): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("userId", user.userId)
            put("password", user.password)
            put("name", user.name)
            put("contact", user.contact)
            put("role", user.role)
            put("skills", user.skills)
            put("experience", user.experience)
            put("strength", user.strength)
            put("interests", user.interests)
            put("preferredTeammate", user.preferredTeammate)
            put("collaborationStyle", user.collaborationStyle)
            put("github", user.github)
            put("receivedInterests", user.receivedInterests)
        }

        val result = db.insertWithOnConflict("userTBL", null, values, SQLiteDatabase.CONFLICT_REPLACE)
        return result != -1L
    }

    fun getUser(userId: String): User? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM userTBL WHERE userId = ?", arrayOf(userId))

        return if (cursor.moveToFirst()) {
            val user = User(
                userId = cursor.getString(0),
                password = cursor.getString(1),
                name = cursor.getString(2),
                contact = cursor.getString(3),
                role = cursor.getString(4),
                skills = cursor.getString(5),
                experience = cursor.getString(6),
                strength = cursor.getString(7),
                interests = cursor.getString(8),
                preferredTeammate = cursor.getString(9),
                collaborationStyle = cursor.getString(10),
                github = cursor.getString(11),
                receivedInterests = cursor.getInt(12)
            )
            cursor.close()
            user
        } else {
            cursor.close()
            null
        }
    }

    fun getUserCount(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM userTBL", null)
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        return count
    }

    fun getAllUsers(): ArrayList<User> {
        val users = ArrayList<User>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM userTBL", null)

        while (cursor.moveToNext()) {
            users.add(User(
                userId = cursor.getString(0),
                password = cursor.getString(1),
                name = cursor.getString(2),
                contact = cursor.getString(3),
                role = cursor.getString(4),
                skills = cursor.getString(5),
                experience = cursor.getString(6),
                strength = cursor.getString(7),
                interests = cursor.getString(8),
                preferredTeammate = cursor.getString(9),
                collaborationStyle = cursor.getString(10),
                github = cursor.getString(11),
                receivedInterests = cursor.getInt(12)
            ))
        }
        cursor.close()
        return users
    }

    fun getUsersByRole(role: String): ArrayList<User> {
        if (role == "전체") return getAllUsers()

        val users = ArrayList<User>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM userTBL WHERE role = ?", arrayOf(role))

        while (cursor.moveToNext()) {
            users.add(User(
                userId = cursor.getString(0),
                password = cursor.getString(1),
                name = cursor.getString(2),
                contact = cursor.getString(3),
                role = cursor.getString(4),
                skills = cursor.getString(5),
                experience = cursor.getString(6),
                strength = cursor.getString(7),
                interests = cursor.getString(8),
                preferredTeammate = cursor.getString(9),
                collaborationStyle = cursor.getString(10),
                github = cursor.getString(11),
                receivedInterests = cursor.getInt(12)
            ))
        }
        cursor.close()
        return users
    }

    // 관심 관련 메서드
    fun isInterestExists(fromUserId: String, toUserId: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT COUNT(*) FROM interestTBL WHERE fromUserId = ? AND toUserId = ?",
            arrayOf(fromUserId, toUserId)
        )
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        return count > 0
    }

    fun saveInterest(fromUserId: String, toUserId: String): Boolean {
        return addInterest(fromUserId, toUserId)
    }

    fun deleteInterest(fromUserId: String, toUserId: String): Boolean {
        return removeInterest(fromUserId, toUserId)
    }

    fun addInterest(fromUserId: String, toUserId: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("fromUserId", fromUserId)
            put("toUserId", toUserId)
            put("timestamp", System.currentTimeMillis())
        }

        val result = db.insert("interestTBL", null, values)
        if (result != -1L) {
            db.execSQL("UPDATE userTBL SET receivedInterests = receivedInterests + 1 WHERE userId = ?", arrayOf(toUserId))
            return true
        }
        return false
    }

    fun removeInterest(fromUserId: String, toUserId: String): Boolean {
        val db = writableDatabase
        val result = db.delete("interestTBL", "fromUserId = ? AND toUserId = ?", arrayOf(fromUserId, toUserId))
        if (result > 0) {
            db.execSQL("UPDATE userTBL SET receivedInterests = receivedInterests - 1 WHERE userId = ?", arrayOf(toUserId))
            return true
        }
        return false
    }

    fun getUserInterests(userId: String): ArrayList<User> {
        val users = ArrayList<User>()
        val db = readableDatabase
        val cursor = db.rawQuery("""
            SELECT u.* FROM userTBL u
            INNER JOIN interestTBL i ON u.userId = i.toUserId
            WHERE i.fromUserId = ?
        """, arrayOf(userId))

        while (cursor.moveToNext()) {
            users.add(User(
                userId = cursor.getString(0),
                password = cursor.getString(1),
                name = cursor.getString(2),
                contact = cursor.getString(3),
                role = cursor.getString(4),
                skills = cursor.getString(5),
                experience = cursor.getString(6),
                strength = cursor.getString(7),
                interests = cursor.getString(8),
                preferredTeammate = cursor.getString(9),
                collaborationStyle = cursor.getString(10),
                github = cursor.getString(11),
                receivedInterests = cursor.getInt(12)
            ))
        }
        cursor.close()
        return users
    }

    fun getReceivedInterests(userId: String): ArrayList<User> {
        val users = ArrayList<User>()
        val db = readableDatabase
        val cursor = db.rawQuery("""
            SELECT u.* FROM userTBL u
            INNER JOIN interestTBL i ON u.userId = i.fromUserId
            WHERE i.toUserId = ?
        """, arrayOf(userId))

        while (cursor.moveToNext()) {
            users.add(User(
                userId = cursor.getString(0),
                password = cursor.getString(1),
                name = cursor.getString(2),
                contact = cursor.getString(3),
                role = cursor.getString(4),
                skills = cursor.getString(5),
                experience = cursor.getString(6),
                strength = cursor.getString(7),
                interests = cursor.getString(8),
                preferredTeammate = cursor.getString(9),
                collaborationStyle = cursor.getString(10),
                github = cursor.getString(11),
                receivedInterests = cursor.getInt(12)
            ))
        }
        cursor.close()
        return users
    }

    // 프로젝트 관련 메서드
    fun createProject(project: Project): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("creatorId", project.creatorId)
            put("title", project.title)
            put("description", project.description)
            put("requiredRoles", project.requiredRoles)
            put("requiredSkills", project.requiredSkills)
            put("maxMembers", project.maxMembers)
            put("currentMembers", 1)
            put("duration", project.duration)
            put("status", project.status)
            put("createdAt", project.createdAt)
        }
        val projectId = db.insert("projectTBL", null, values)

        // 프로젝트 생성자를 팀원으로 자동 추가
        if (projectId != -1L) {
            val memberValues = ContentValues().apply {
                put("projectId", projectId)
                put("userId", project.creatorId)
                put("role", "팀장")
                put("joinedAt", System.currentTimeMillis())
            }
            db.insert("memberTBL", null, memberValues)
        }

        return projectId
    }

    fun getProject(projectId: Int): Project? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM projectTBL WHERE projectId = ?", arrayOf(projectId.toString()))

        return if (cursor.moveToFirst()) {
            val project = Project(
                projectId = cursor.getInt(0),
                creatorId = cursor.getString(1),
                title = cursor.getString(2),
                description = cursor.getString(3),
                requiredRoles = cursor.getString(4),
                requiredSkills = cursor.getString(5),
                maxMembers = cursor.getInt(6),
                currentMembers = cursor.getInt(7),
                duration = cursor.getString(8),
                status = cursor.getString(9),
                createdAt = cursor.getLong(10)
            )
            cursor.close()
            project
        } else {
            cursor.close()
            null
        }
    }

    fun getAllProjects(): ArrayList<Project> {
        val projects = ArrayList<Project>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM projectTBL ORDER BY createdAt DESC", null)

        while (cursor.moveToNext()) {
            projects.add(Project(
                projectId = cursor.getInt(0),
                creatorId = cursor.getString(1),
                title = cursor.getString(2),
                description = cursor.getString(3),
                requiredRoles = cursor.getString(4),
                requiredSkills = cursor.getString(5),
                maxMembers = cursor.getInt(6),
                currentMembers = cursor.getInt(7),
                duration = cursor.getString(8),
                status = cursor.getString(9),
                createdAt = cursor.getLong(10)
            ))
        }
        cursor.close()
        return projects
    }

    fun getProjectsByStatus(status: String): ArrayList<Project> {
        val projects = ArrayList<Project>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM projectTBL WHERE status = ? ORDER BY createdAt DESC",
            arrayOf(status)
        )

        while (cursor.moveToNext()) {
            projects.add(Project(
                projectId = cursor.getInt(0),
                creatorId = cursor.getString(1),
                title = cursor.getString(2),
                description = cursor.getString(3),
                requiredRoles = cursor.getString(4),
                requiredSkills = cursor.getString(5),
                maxMembers = cursor.getInt(6),
                currentMembers = cursor.getInt(7),
                duration = cursor.getString(8),
                status = cursor.getString(9),
                createdAt = cursor.getLong(10)
            ))
        }
        cursor.close()
        return projects
    }

    fun getMyProjects(userId: String): ArrayList<Project> {
        val projects = ArrayList<Project>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM projectTBL WHERE creatorId = ? ORDER BY createdAt DESC",
            arrayOf(userId)
        )

        while (cursor.moveToNext()) {
            projects.add(Project(
                projectId = cursor.getInt(0),
                creatorId = cursor.getString(1),
                title = cursor.getString(2),
                description = cursor.getString(3),
                requiredRoles = cursor.getString(4),
                requiredSkills = cursor.getString(5),
                maxMembers = cursor.getInt(6),
                currentMembers = cursor.getInt(7),
                duration = cursor.getString(8),
                status = cursor.getString(9),
                createdAt = cursor.getLong(10)
            ))
        }
        cursor.close()
        return projects
    }

    fun getMyParticipatingProjects(userId: String): ArrayList<Project> {
        val projects = ArrayList<Project>()
        val db = readableDatabase
        val cursor = db.rawQuery("""
            SELECT p.* FROM projectTBL p
            INNER JOIN memberTBL m ON p.projectId = m.projectId
            WHERE m.userId = ? AND p.creatorId != ?
            ORDER BY m.joinedAt DESC
        """, arrayOf(userId, userId))

        while (cursor.moveToNext()) {
            projects.add(Project(
                projectId = cursor.getInt(0),
                creatorId = cursor.getString(1),
                title = cursor.getString(2),
                description = cursor.getString(3),
                requiredRoles = cursor.getString(4),
                requiredSkills = cursor.getString(5),
                maxMembers = cursor.getInt(6),
                currentMembers = cursor.getInt(7),
                duration = cursor.getString(8),
                status = cursor.getString(9),
                createdAt = cursor.getLong(10)
            ))
        }
        cursor.close()
        return projects
    }

    fun updateProject(project: Project): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("title", project.title)
            put("description", project.description)
            put("requiredRoles", project.requiredRoles)
            put("requiredSkills", project.requiredSkills)
            put("maxMembers", project.maxMembers)
            put("duration", project.duration)
            put("status", project.status)
        }
        val result = db.update("projectTBL", values, "projectId = ?", arrayOf(project.projectId.toString()))
        return result > 0
    }

    fun deleteProject(projectId: Int): Boolean {
        val db = writableDatabase
        db.delete("applicationTBL", "projectId = ?", arrayOf(projectId.toString()))
        db.delete("memberTBL", "projectId = ?", arrayOf(projectId.toString()))
        val result = db.delete("projectTBL", "projectId = ?", arrayOf(projectId.toString()))
        return result > 0
    }

    // 지원 관련 메서드
    fun applyToProject(application: Application): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("projectId", application.projectId)
            put("userId", application.userId)
            put("userName", application.userName)
            put("userRole", application.userRole)
            put("message", application.message)
            put("status", application.status)
            put("appliedAt", application.appliedAt)
        }
        return db.insert("applicationTBL", null, values)
    }

    fun getApplicationsByProject(projectId: Int): ArrayList<Application> {
        val applications = ArrayList<Application>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM applicationTBL WHERE projectId = ? ORDER BY appliedAt DESC",
            arrayOf(projectId.toString())
        )

        while (cursor.moveToNext()) {
            applications.add(Application(
                applicationId = cursor.getInt(0),
                projectId = cursor.getInt(1),
                userId = cursor.getString(2),
                userName = cursor.getString(3),
                userRole = cursor.getString(4),
                message = cursor.getString(5),
                status = cursor.getString(6),
                appliedAt = cursor.getLong(7)
            ))
        }
        cursor.close()
        return applications
    }

    fun getMyApplications(userId: String): ArrayList<Application> {
        val applications = ArrayList<Application>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM applicationTBL WHERE userId = ? ORDER BY appliedAt DESC",
            arrayOf(userId)
        )

        while (cursor.moveToNext()) {
            applications.add(Application(
                applicationId = cursor.getInt(0),
                projectId = cursor.getInt(1),
                userId = cursor.getString(2),
                userName = cursor.getString(3),
                userRole = cursor.getString(4),
                message = cursor.getString(5),
                status = cursor.getString(6),
                appliedAt = cursor.getLong(7)
            ))
        }
        cursor.close()
        return applications
    }

    fun hasApplied(projectId: Int, userId: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT COUNT(*) FROM applicationTBL WHERE projectId = ? AND userId = ?",
            arrayOf(projectId.toString(), userId)
        )
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        return count > 0
    }

    fun approveApplication(applicationId: Int, projectId: Int, userId: String, role: String): Boolean {
        val db = writableDatabase

        // 1. 지원 상태를 approved로 변경
        val appValues = ContentValues().apply {
            put("status", "approved")
        }
        db.update("applicationTBL", appValues, "applicationId = ?", arrayOf(applicationId.toString()))

        // 2. memberTBL에 추가
        val memberValues = ContentValues().apply {
            put("projectId", projectId)
            put("userId", userId)
            put("role", role)
            put("joinedAt", System.currentTimeMillis())
        }
        db.insert("memberTBL", null, memberValues)

        // 3. currentMembers 증가
        db.execSQL("UPDATE projectTBL SET currentMembers = currentMembers + 1 WHERE projectId = ?", arrayOf(projectId.toString()))

        return true
    }

    fun rejectApplication(applicationId: Int): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("status", "rejected")
        }
        val result = db.update("applicationTBL", values, "applicationId = ?", arrayOf(applicationId.toString()))
        return result > 0
    }

    fun getProjectMembers(projectId: Int): ArrayList<User> {
        val users = ArrayList<User>()
        val db = readableDatabase
        val cursor = db.rawQuery("""
            SELECT u.* FROM userTBL u
            INNER JOIN memberTBL m ON u.userId = m.userId
            WHERE m.projectId = ?
        """, arrayOf(projectId.toString()))

        while (cursor.moveToNext()) {
            users.add(User(
                userId = cursor.getString(0),
                password = cursor.getString(1),
                name = cursor.getString(2),
                contact = cursor.getString(3),
                role = cursor.getString(4),
                skills = cursor.getString(5),
                experience = cursor.getString(6),
                strength = cursor.getString(7),
                interests = cursor.getString(8),
                preferredTeammate = cursor.getString(9),
                collaborationStyle = cursor.getString(10),
                github = cursor.getString(11),
                receivedInterests = cursor.getInt(12)
            ))
        }
        cursor.close()
        return users
    }
}