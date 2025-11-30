package com.example.helloandroid

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// 13주차: SQLite 데이터베이스
class DBHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        const val DB_NAME = "TeamBuilding.db"
        const val DB_VERSION = 2  // 프로젝트 테이블 추가로 버전 업
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // 사용자 테이블
        db?.execSQL("""
            CREATE TABLE IF NOT EXISTS userTBL (
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

        // 관심 표시 테이블
        db?.execSQL("""
            CREATE TABLE IF NOT EXISTS interestTBL (
                fromUserId TEXT,
                toUserId TEXT,
                PRIMARY KEY (fromUserId, toUserId)
            )
        """)

        // 프로젝트 테이블
        db?.execSQL("""
            CREATE TABLE IF NOT EXISTS projectTBL (
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
                createdAt INTEGER,
                FOREIGN KEY (creatorId) REFERENCES userTBL(userId)
            )
        """)

        // 프로젝트 지원 테이블
        db?.execSQL("""
            CREATE TABLE IF NOT EXISTS applicationTBL (
                applicationId INTEGER PRIMARY KEY AUTOINCREMENT,
                projectId INTEGER,
                userId TEXT,
                applyRole TEXT,
                message TEXT,
                status TEXT DEFAULT 'pending',
                appliedAt INTEGER,
                FOREIGN KEY (projectId) REFERENCES projectTBL(projectId),
                FOREIGN KEY (userId) REFERENCES userTBL(userId)
            )
        """)

        // 프로젝트 멤버 테이블
        db?.execSQL("""
            CREATE TABLE IF NOT EXISTS memberTBL (
                projectId INTEGER,
                userId TEXT,
                role TEXT,
                joinedAt INTEGER,
                PRIMARY KEY (projectId, userId),
                FOREIGN KEY (projectId) REFERENCES projectTBL(projectId),
                FOREIGN KEY (userId) REFERENCES userTBL(userId)
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

    // ========== 사용자 관련 함수 ==========

    fun registerUser(
        userId: String,
        password: String,
        name: String,
        contact: String
    ): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("userId", userId)
            put("password", password)
            put("name", name)
            put("contact", contact)
            put("role", "")
            put("skills", "")
            put("experience", "")
            put("strength", "")
            put("interests", "")
            put("preferredTeammate", "")
            put("collaborationStyle", "")
            put("github", "")
            put("receivedInterests", 0)
        }
        return db.insert("userTBL", null, values) != -1L
    }

    fun loginUser(userId: String, password: String): String? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT name FROM userTBL WHERE userId = ? AND password = ?",
            arrayOf(userId, password)
        )
        return if (cursor.moveToFirst()) {
            val name = cursor.getString(0)
            cursor.close()
            name
        } else {
            cursor.close()
            null
        }
    }

    fun getUser(userId: String): User? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM userTBL WHERE userId = ?",
            arrayOf(userId)
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

    fun getAllUsers(): ArrayList<User> {
        val users = ArrayList<User>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM userTBL", null)

        while (cursor.moveToNext()) {
            users.add(
                User(
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
            )
        }
        cursor.close()
        return users
    }

    fun getUsersByRole(role: String): ArrayList<User> {
        if (role == "전체") return getAllUsers()

        val users = ArrayList<User>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM userTBL WHERE role = ?",
            arrayOf(role)
        )

        while (cursor.moveToNext()) {
            users.add(
                User(
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
            )
        }
        cursor.close()
        return users
    }

    fun saveUser(user: User): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
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
        }
        return db.update("userTBL", values, "userId = ?", arrayOf(user.userId)) > 0
    }

    fun getUserCount(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM userTBL", null)
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()
        return count
    }

    // ========== 관심 표시 관련 함수 ==========

    fun saveInterest(fromUserId: String, toUserId: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("fromUserId", fromUserId)
            put("toUserId", toUserId)
        }
        val result = db.insert("interestTBL", null, values) != -1L

        if (result) {
            db.execSQL(
                "UPDATE userTBL SET receivedInterests = receivedInterests + 1 WHERE userId = ?",
                arrayOf(toUserId)
            )
        }
        return result
    }

    fun deleteInterest(fromUserId: String, toUserId: String): Boolean {
        val db = writableDatabase
        val result = db.delete(
            "interestTBL",
            "fromUserId = ? AND toUserId = ?",
            arrayOf(fromUserId, toUserId)
        ) > 0

        if (result) {
            db.execSQL(
                "UPDATE userTBL SET receivedInterests = receivedInterests - 1 WHERE userId = ?",
                arrayOf(toUserId)
            )
        }
        return result
    }

    fun isInterestExists(fromUserId: String, toUserId: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM interestTBL WHERE fromUserId = ? AND toUserId = ?",
            arrayOf(fromUserId, toUserId)
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
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
            users.add(
                User(
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
            )
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
            users.add(
                User(
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
            )
        }
        cursor.close()
        return users
    }

    // ========== 프로젝트 관련 함수 ==========

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
            put("status", "recruiting")
            put("createdAt", System.currentTimeMillis())
        }
        val projectId = db.insert("projectTBL", null, values)

        // 생성자를 멤버로 추가
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

    fun getAllProjects(): ArrayList<Project> {
        val projects = ArrayList<Project>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM projectTBL ORDER BY createdAt DESC",
            null
        )

        while (cursor.moveToNext()) {
            projects.add(
                Project(
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
            )
        }
        cursor.close()
        return projects
    }

    fun getProjectsByStatus(status: String): ArrayList<Project> {
        if (status == "전체") return getAllProjects()

        val projects = ArrayList<Project>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM projectTBL WHERE status = ? ORDER BY createdAt DESC",
            arrayOf(status)
        )

        while (cursor.moveToNext()) {
            projects.add(
                Project(
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
            )
        }
        cursor.close()
        return projects
    }

    fun getProject(projectId: Int): Project? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM projectTBL WHERE projectId = ?",
            arrayOf(projectId.toString())
        )

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

    fun getMyProjects(userId: String): ArrayList<Project> {
        val projects = ArrayList<Project>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM projectTBL WHERE creatorId = ? ORDER BY createdAt DESC",
            arrayOf(userId)
        )

        while (cursor.moveToNext()) {
            projects.add(
                Project(
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
            )
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
        return db.update(
            "projectTBL",
            values,
            "projectId = ?",
            arrayOf(project.projectId.toString())
        ) > 0
    }

    fun deleteProject(projectId: Int): Boolean {
        val db = writableDatabase
        // 관련 지원서와 멤버도 함께 삭제
        db.delete("applicationTBL", "projectId = ?", arrayOf(projectId.toString()))
        db.delete("memberTBL", "projectId = ?", arrayOf(projectId.toString()))
        return db.delete("projectTBL", "projectId = ?", arrayOf(projectId.toString())) > 0
    }

    fun closeProject(projectId: Int): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("status", "closed")
        }
        return db.update(
            "projectTBL",
            values,
            "projectId = ?",
            arrayOf(projectId.toString())
        ) > 0
    }

    // ========== 지원 관련 함수 ==========

    fun applyToProject(
        projectId: Int,
        userId: String,
        applyRole: String,
        message: String
    ): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("projectId", projectId)
            put("userId", userId)
            put("applyRole", applyRole)
            put("message", message)
            put("status", "pending")
            put("appliedAt", System.currentTimeMillis())
        }
        return db.insert("applicationTBL", null, values) != -1L
    }

    fun getApplicationsByProject(projectId: Int): ArrayList<Application> {
        val applications = ArrayList<Application>()
        val db = readableDatabase
        val cursor = db.rawQuery("""
            SELECT a.*, u.name, u.role, u.skills, u.contact
            FROM applicationTBL a
            INNER JOIN userTBL u ON a.userId = u.userId
            WHERE a.projectId = ?
            ORDER BY a.appliedAt DESC
        """, arrayOf(projectId.toString()))

        while (cursor.moveToNext()) {
            applications.add(
                Application(
                    applicationId = cursor.getInt(0),
                    projectId = cursor.getInt(1),
                    userId = cursor.getString(2),
                    applyRole = cursor.getString(3),
                    message = cursor.getString(4),
                    status = cursor.getString(5),
                    appliedAt = cursor.getLong(6),
                    userName = cursor.getString(7),
                    userRole = cursor.getString(8),
                    userSkills = cursor.getString(9),
                    userContact = cursor.getString(10)
                )
            )
        }
        cursor.close()
        return applications
    }

    fun getMyApplications(userId: String): ArrayList<ApplicationWithProject> {
        val applications = ArrayList<ApplicationWithProject>()
        val db = readableDatabase
        val cursor = db.rawQuery("""
            SELECT a.*, p.title, p.status as projectStatus
            FROM applicationTBL a
            INNER JOIN projectTBL p ON a.projectId = p.projectId
            WHERE a.userId = ?
            ORDER BY a.appliedAt DESC
        """, arrayOf(userId))

        while (cursor.moveToNext()) {
            applications.add(
                ApplicationWithProject(
                    applicationId = cursor.getInt(0),
                    projectId = cursor.getInt(1),
                    userId = cursor.getString(2),
                    applyRole = cursor.getString(3),
                    message = cursor.getString(4),
                    status = cursor.getString(5),
                    appliedAt = cursor.getLong(6),
                    projectTitle = cursor.getString(7),
                    projectStatus = cursor.getString(8)
                )
            )
        }
        cursor.close()
        return applications
    }

    fun approveApplication(applicationId: Int): Boolean {
        val db = writableDatabase

        // 지원서 정보 가져오기
        val cursor = db.rawQuery(
            "SELECT projectId, userId, applyRole FROM applicationTBL WHERE applicationId = ?",
            arrayOf(applicationId.toString())
        )

        if (!cursor.moveToFirst()) {
            cursor.close()
            return false
        }

        val projectId = cursor.getInt(0)
        val userId = cursor.getString(1)
        val applyRole = cursor.getString(2)
        cursor.close()

        // 지원 승인
        val values = ContentValues().apply {
            put("status", "approved")
        }
        db.update("applicationTBL", values, "applicationId = ?", arrayOf(applicationId.toString()))

        // 멤버로 추가
        val memberValues = ContentValues().apply {
            put("projectId", projectId)
            put("userId", userId)
            put("role", applyRole)
            put("joinedAt", System.currentTimeMillis())
        }
        db.insert("memberTBL", null, memberValues)

        // 현재 멤버 수 증가
        db.execSQL(
            "UPDATE projectTBL SET currentMembers = currentMembers + 1 WHERE projectId = ?",
            arrayOf(projectId.toString())
        )

        return true
    }

    fun rejectApplication(applicationId: Int): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("status", "rejected")
        }
        return db.update(
            "applicationTBL",
            values,
            "applicationId = ?",
            arrayOf(applicationId.toString())
        ) > 0
    }

    fun isAlreadyApplied(projectId: Int, userId: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM applicationTBL WHERE projectId = ? AND userId = ? AND status = 'pending'",
            arrayOf(projectId.toString(), userId)
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun getProjectMembers(projectId: Int): ArrayList<Member> {
        val members = ArrayList<Member>()
        val db = readableDatabase
        val cursor = db.rawQuery("""
            SELECT m.*, u.name, u.contact, u.skills
            FROM memberTBL m
            INNER JOIN userTBL u ON m.userId = u.userId
            WHERE m.projectId = ?
            ORDER BY m.joinedAt ASC
        """, arrayOf(projectId.toString()))

        while (cursor.moveToNext()) {
            members.add(
                Member(
                    projectId = cursor.getInt(0),
                    userId = cursor.getString(1),
                    role = cursor.getString(2),
                    joinedAt = cursor.getLong(3),
                    userName = cursor.getString(4),
                    userContact = cursor.getString(5),
                    userSkills = cursor.getString(6)
                )
            )
        }
        cursor.close()
        return members
    }

    fun getMyParticipatingProjects(userId: String): ArrayList<Project> {
        val projects = ArrayList<Project>()
        val db = readableDatabase
        val cursor = db.rawQuery("""
            SELECT p.* FROM projectTBL p
            INNER JOIN memberTBL m ON p.projectId = m.projectId
            WHERE m.userId = ?
            ORDER BY m.joinedAt DESC
        """, arrayOf(userId))

        while (cursor.moveToNext()) {
            projects.add(
                Project(
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
            )
        }
        cursor.close()
        return projects
    }
}

// Data Classes
data class User(
    val userId: String,
    var password: String,
    var name: String,
    var contact: String,
    var role: String,
    var skills: String,
    var experience: String,
    var strength: String,
    var interests: String,
    var preferredTeammate: String,
    var collaborationStyle: String,
    var github: String,
    var receivedInterests: Int
)

data class Project(
    val projectId: Int = 0,
    val creatorId: String,
    val title: String,
    val description: String,
    val requiredRoles: String,
    val requiredSkills: String,
    val maxMembers: Int,
    val currentMembers: Int = 1,
    val duration: String,
    val status: String = "recruiting",
    val createdAt: Long = System.currentTimeMillis()
)

data class Application(
    val applicationId: Int,
    val projectId: Int,
    val userId: String,
    val applyRole: String,
    val message: String,
    val status: String,
    val appliedAt: Long,
    val userName: String = "",
    val userRole: String = "",
    val userSkills: String = "",
    val userContact: String = ""
)

data class ApplicationWithProject(
    val applicationId: Int,
    val projectId: Int,
    val userId: String,
    val applyRole: String,
    val message: String,
    val status: String,
    val appliedAt: Long,
    val projectTitle: String,
    val projectStatus: String
)

data class Member(
    val projectId: Int,
    val userId: String,
    val role: String,
    val joinedAt: Long,
    val userName: String = "",
    val userContact: String = "",
    val userSkills: String = ""
)
