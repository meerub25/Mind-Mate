package com.mindmate.wellness.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mindmate.wellness.data.model.ChatMessage
import com.mindmate.wellness.data.model.MessageSender
import com.mindmate.wellness.data.model.MoodEntry
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class JournalEntry(
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val content: String,
    val dateString: String
)

data class SupportCirclePost(
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val alias: String,
    val content: String,
    var meTooCount: Int = 0
)

/**
 * SQLite Database Helper for MindEase.
 * Powers offline-first mood tracking, private journal entries, AI companion chat history,
 * and anonymous student support circle posts.
 */
class MindmateDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private val gson = Gson()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    companion object {
        private const val DATABASE_NAME = "mindease.db"
        private const val DATABASE_VERSION = 2

        // Moods Table
        private const val TABLE_MOODS = "mood_entries"
        private const val COL_MOOD_ID = "id"
        private const val COL_MOOD_TIMESTAMP = "timestamp"
        private const val COL_MOOD_SCORE = "mood_score"
        private const val COL_MOOD_EMOJI = "mood_emoji"
        private const val COL_MOOD_LABEL = "mood_label"
        private const val COL_MOOD_STRESS = "stress_level"
        private const val COL_MOOD_TRIGGERS = "triggers_json"
        private const val COL_MOOD_NOTE = "journal_note"
        private const val COL_MOOD_DATE = "date_string"

        // Chat Table
        private const val TABLE_CHAT = "chat_messages"
        private const val COL_CHAT_ID = "id"
        private const val COL_CHAT_TIMESTAMP = "timestamp"
        private const val COL_CHAT_SENDER = "sender"
        private const val COL_CHAT_CONTENT = "content"
        private const val COL_CHAT_LANG = "language"
        private const val COL_CHAT_EMOTION = "detected_emotion"
        private const val COL_CHAT_ACTION = "suggested_action"

        // Journal Table
        private const val TABLE_JOURNAL = "journal_entries"
        private const val COL_JOURNAL_ID = "id"
        private const val COL_JOURNAL_TIMESTAMP = "timestamp"
        private const val COL_JOURNAL_CONTENT = "content"
        private const val COL_JOURNAL_DATE = "date_string"

        // Support Circle Table
        private const val TABLE_CIRCLE = "support_circle_posts"
        private const val COL_CIRCLE_ID = "id"
        private const val COL_CIRCLE_TIMESTAMP = "timestamp"
        private const val COL_CIRCLE_ALIAS = "alias"
        private const val COL_CIRCLE_CONTENT = "content"
        private const val COL_CIRCLE_METOO = "metoo_count"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createMoods = """
            CREATE TABLE $TABLE_MOODS (
                $COL_MOOD_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_MOOD_TIMESTAMP INTEGER,
                $COL_MOOD_SCORE INTEGER,
                $COL_MOOD_EMOJI TEXT,
                $COL_MOOD_LABEL TEXT,
                $COL_MOOD_STRESS INTEGER,
                $COL_MOOD_TRIGGERS TEXT,
                $COL_MOOD_NOTE TEXT,
                $COL_MOOD_DATE TEXT
            )
        """.trimIndent()

        val createChat = """
            CREATE TABLE $TABLE_CHAT (
                $COL_CHAT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_CHAT_TIMESTAMP INTEGER,
                $COL_CHAT_SENDER TEXT,
                $COL_CHAT_CONTENT TEXT,
                $COL_CHAT_LANG TEXT,
                $COL_CHAT_EMOTION TEXT,
                $COL_CHAT_ACTION TEXT
            )
        """.trimIndent()

        val createJournal = """
            CREATE TABLE $TABLE_JOURNAL (
                $COL_JOURNAL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_JOURNAL_TIMESTAMP INTEGER,
                $COL_JOURNAL_CONTENT TEXT,
                $COL_JOURNAL_DATE TEXT
            )
        """.trimIndent()

        val createCircle = """
            CREATE TABLE $TABLE_CIRCLE (
                $COL_CIRCLE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_CIRCLE_TIMESTAMP INTEGER,
                $COL_CIRCLE_ALIAS TEXT,
                $COL_CIRCLE_CONTENT TEXT,
                $COL_CIRCLE_METOO INTEGER DEFAULT 0
            )
        """.trimIndent()

        db.execSQL(createMoods)
        db.execSQL(createChat)
        db.execSQL(createJournal)
        db.execSQL(createCircle)

        // Seed initial greeting and support circle posts
        seedInitialData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MOODS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CHAT")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_JOURNAL")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CIRCLE")
        onCreate(db)
    }

    private fun seedInitialData(db: SQLiteDatabase) {
        // Welcome AI Chat Message
        val chatCv = ContentValues().apply {
            put(COL_CHAT_TIMESTAMP, System.currentTimeMillis())
            put(COL_CHAT_SENDER, MessageSender.AI.name)
            put(
                COL_CHAT_CONTENT,
                "Assalam-o-Alaikum! I am MindEase, your supportive emotional companion. University life can feel overwhelming with exams, career uncertainty, and family expectations. You can chat with me freely in English, Urdu or Roman Urdu. How are you feeling right now?"
            )
            put(COL_CHAT_LANG, "roman_ur")
        }
        db.insert(TABLE_CHAT, null, chatCv)

        // Seed sample peer support circle posts
        val samplePosts = listOf(
            Pair("Anonymous Owl", "Feeling overwhelmed with final year project deadlines and presentation anxiety. Taking it one slide at a time."),
            Pair("Calm River", "Remember that a low quiz score does not define your future. Be kind to yourself today, fellow students!"),
            Pair("Mindful Sparrow", "First time living in a hostel away from home. Homesick, but cooking chai with my roommates helped.")
        )
        for (p in samplePosts) {
            val circleCv = ContentValues().apply {
                put(COL_CIRCLE_TIMESTAMP, System.currentTimeMillis() - (1000 * 60 * 60 * (samplePosts.indexOf(p) + 1)))
                put(COL_CIRCLE_ALIAS, p.first)
                put(COL_CIRCLE_CONTENT, p.second)
                put(COL_CIRCLE_METOO, (samplePosts.indexOf(p) + 2) * 3)
            }
            db.insert(TABLE_CIRCLE, null, circleCv)
        }
    }

    // -------------------------------------------------------------
    // MOODS
    // -------------------------------------------------------------

    fun insertMood(entry: MoodEntry): Long {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COL_MOOD_TIMESTAMP, entry.timestamp)
            put(COL_MOOD_SCORE, entry.moodScore)
            put(COL_MOOD_EMOJI, entry.moodEmoji)
            put(COL_MOOD_LABEL, entry.moodLabel)
            put(COL_MOOD_STRESS, entry.stressLevel)
            put(COL_MOOD_TRIGGERS, gson.toJson(entry.triggers))
            put(COL_MOOD_NOTE, entry.journalNote)
            put(COL_MOOD_DATE, entry.dateString)
        }
        return db.insert(TABLE_MOODS, null, cv)
    }

    fun getAllMoodEntries(): List<MoodEntry> {
        val list = mutableListOf<MoodEntry>()
        val db = readableDatabase
        val cursor = db.query(TABLE_MOODS, null, null, null, null, null, "$COL_MOOD_TIMESTAMP DESC")
        val triggerListType = object : TypeToken<List<String>>() {}.type

        cursor.use {
            while (it.moveToNext()) {
                val triggersJson = it.getString(it.getColumnIndexOrThrow(COL_MOOD_TRIGGERS))
                val triggers: List<String> = try {
                    gson.fromJson(triggersJson, triggerListType) ?: emptyList()
                } catch (_: Exception) {
                    emptyList()
                }

                list.add(
                    MoodEntry(
                        id = it.getLong(it.getColumnIndexOrThrow(COL_MOOD_ID)),
                        timestamp = it.getLong(it.getColumnIndexOrThrow(COL_MOOD_TIMESTAMP)),
                        moodScore = it.getInt(it.getColumnIndexOrThrow(COL_MOOD_SCORE)),
                        moodEmoji = it.getString(it.getColumnIndexOrThrow(COL_MOOD_EMOJI)),
                        moodLabel = it.getString(it.getColumnIndexOrThrow(COL_MOOD_LABEL)),
                        stressLevel = it.getInt(it.getColumnIndexOrThrow(COL_MOOD_STRESS)),
                        triggers = triggers,
                        journalNote = it.getString(it.getColumnIndexOrThrow(COL_MOOD_NOTE)),
                        dateString = it.getString(it.getColumnIndexOrThrow(COL_MOOD_DATE))
                    )
                )
            }
        }
        return list
    }

    fun getTodayMoodEntry(): MoodEntry? {
        val today = dateFormat.format(Date())
        val db = readableDatabase
        val cursor = db.query(
            TABLE_MOODS, null, "$COL_MOOD_DATE = ?", arrayOf(today), null, null, "$COL_MOOD_TIMESTAMP DESC", "1"
        )
        cursor.use {
            if (it.moveToFirst()) {
                val triggersJson = it.getString(it.getColumnIndexOrThrow(COL_MOOD_TRIGGERS))
                val triggerListType = object : TypeToken<List<String>>() {}.type
                val triggers: List<String> = try {
                    gson.fromJson(triggersJson, triggerListType) ?: emptyList()
                } catch (_: Exception) {
                    emptyList()
                }

                return MoodEntry(
                    id = it.getLong(it.getColumnIndexOrThrow(COL_MOOD_ID)),
                    timestamp = it.getLong(it.getColumnIndexOrThrow(COL_MOOD_TIMESTAMP)),
                    moodScore = it.getInt(it.getColumnIndexOrThrow(COL_MOOD_SCORE)),
                    moodEmoji = it.getString(it.getColumnIndexOrThrow(COL_MOOD_EMOJI)),
                    moodLabel = it.getString(it.getColumnIndexOrThrow(COL_MOOD_LABEL)),
                    stressLevel = it.getInt(it.getColumnIndexOrThrow(COL_MOOD_STRESS)),
                    triggers = triggers,
                    journalNote = it.getString(it.getColumnIndexOrThrow(COL_MOOD_NOTE)),
                    dateString = it.getString(it.getColumnIndexOrThrow(COL_MOOD_DATE))
                )
            }
        }
        return null
    }

    fun getAverageStressLevel(): Float {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT AVG($COL_MOOD_STRESS) FROM $TABLE_MOODS", null)
        var avg = 0f
        cursor.use {
            if (it.moveToFirst()) avg = it.getFloat(0)
        }
        return avg
    }

    fun get7DayMoodTrend(): List<Float> {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT $COL_MOOD_SCORE FROM $TABLE_MOODS ORDER BY $COL_MOOD_TIMESTAMP ASC LIMIT 7", null)
        val trend = mutableListOf<Float>()
        cursor.use {
            while (it.moveToNext()) {
                trend.add(it.getInt(0).toFloat())
            }
        }
        return trend
    }

    fun getTopTriggers(): List<Pair<String, Int>> {
        val allEntries = getAllMoodEntries()
        val triggerCounts = mutableMapOf<String, Int>()
        for (entry in allEntries) {
            for (t in entry.triggers) {
                triggerCounts[t] = (triggerCounts[t] ?: 0) + 1
            }
        }
        return triggerCounts.toList().sortedByDescending { it.second }.take(6)
    }

    fun getCheckinStreak(): Int {
        val entries = getAllMoodEntries()
        if (entries.isEmpty()) return 0
        val dates = entries.map { it.dateString }.distinct().sortedDescending()
        val cal = Calendar.getInstance()
        var streak = 0
        for (date in dates) {
            val expectedDate = dateFormat.format(cal.time)
            if (date == expectedDate) {
                streak++
                cal.add(Calendar.DAY_OF_YEAR, -1)
            } else if (streak == 0) {
                cal.add(Calendar.DAY_OF_YEAR, -1)
                val yesterday = dateFormat.format(cal.time)
                if (date == yesterday) {
                    streak++
                    cal.add(Calendar.DAY_OF_YEAR, -1)
                } else {
                    break
                }
            } else {
                break
            }
        }
        return streak
    }

    // -------------------------------------------------------------
    // JOURNAL
    // -------------------------------------------------------------

    fun insertJournalEntry(content: String): Long {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COL_JOURNAL_TIMESTAMP, System.currentTimeMillis())
            put(COL_JOURNAL_CONTENT, content)
            put(COL_JOURNAL_DATE, dateFormat.format(Date()))
        }
        return db.insert(TABLE_JOURNAL, null, cv)
    }

    fun getAllJournalEntries(): List<JournalEntry> {
        val list = mutableListOf<JournalEntry>()
        val db = readableDatabase
        val cursor = db.query(TABLE_JOURNAL, null, null, null, null, null, "$COL_JOURNAL_TIMESTAMP DESC")
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    JournalEntry(
                        id = it.getLong(it.getColumnIndexOrThrow(COL_JOURNAL_ID)),
                        timestamp = it.getLong(it.getColumnIndexOrThrow(COL_JOURNAL_TIMESTAMP)),
                        content = it.getString(it.getColumnIndexOrThrow(COL_JOURNAL_CONTENT)),
                        dateString = it.getString(it.getColumnIndexOrThrow(COL_JOURNAL_DATE))
                    )
                )
            }
        }
        return list
    }

    fun deleteJournalEntry(id: Long): Boolean {
        val db = writableDatabase
        return db.delete(TABLE_JOURNAL, "$COL_JOURNAL_ID = ?", arrayOf(id.toString())) > 0
    }

    // -------------------------------------------------------------
    // SUPPORT CIRCLE (ANONYMOUS COMMUNITY)
    // -------------------------------------------------------------

    fun insertCirclePost(alias: String, content: String): Long {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COL_CIRCLE_TIMESTAMP, System.currentTimeMillis())
            put(COL_CIRCLE_ALIAS, alias)
            put(COL_CIRCLE_CONTENT, content)
            put(COL_CIRCLE_METOO, 1)
        }
        return db.insert(TABLE_CIRCLE, null, cv)
    }

    fun getAllCirclePosts(): List<SupportCirclePost> {
        val list = mutableListOf<SupportCirclePost>()
        val db = readableDatabase
        val cursor = db.query(TABLE_CIRCLE, null, null, null, null, null, "$COL_CIRCLE_TIMESTAMP DESC")
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    SupportCirclePost(
                        id = it.getLong(it.getColumnIndexOrThrow(COL_CIRCLE_ID)),
                        timestamp = it.getLong(it.getColumnIndexOrThrow(COL_CIRCLE_TIMESTAMP)),
                        alias = it.getString(it.getColumnIndexOrThrow(COL_CIRCLE_ALIAS)),
                        content = it.getString(it.getColumnIndexOrThrow(COL_CIRCLE_CONTENT)),
                        meTooCount = it.getInt(it.getColumnIndexOrThrow(COL_CIRCLE_METOO))
                    )
                )
            }
        }
        return list
    }

    fun incrementMeToo(id: Long) {
        val db = writableDatabase
        db.execSQL("UPDATE $TABLE_CIRCLE SET $COL_CIRCLE_METOO = $COL_CIRCLE_METOO + 1 WHERE $COL_CIRCLE_ID = $id")
    }

    fun deleteCirclePost(id: Long) {
        val db = writableDatabase
        db.delete(TABLE_CIRCLE, "$COL_CIRCLE_ID = ?", arrayOf(id.toString()))
    }

    // -------------------------------------------------------------
    // CHAT
    // -------------------------------------------------------------

    fun insertChatMessage(msg: ChatMessage): Long {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COL_CHAT_TIMESTAMP, msg.timestamp)
            put(COL_CHAT_SENDER, msg.sender.name)
            put(COL_CHAT_CONTENT, msg.content)
            put(COL_CHAT_LANG, msg.language)
            put(COL_CHAT_EMOTION, msg.detectedEmotion)
            put(COL_CHAT_ACTION, msg.suggestedAction)
        }
        return db.insert(TABLE_CHAT, null, cv)
    }

    fun getAllChatMessages(): List<ChatMessage> {
        val list = mutableListOf<ChatMessage>()
        val db = readableDatabase
        val cursor = db.query(TABLE_CHAT, null, null, null, null, null, "$COL_CHAT_TIMESTAMP ASC")
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    ChatMessage(
                        id = it.getLong(it.getColumnIndexOrThrow(COL_CHAT_ID)),
                        timestamp = it.getLong(it.getColumnIndexOrThrow(COL_CHAT_TIMESTAMP)),
                        sender = MessageSender.valueOf(it.getString(it.getColumnIndexOrThrow(COL_CHAT_SENDER))),
                        content = it.getString(it.getColumnIndexOrThrow(COL_CHAT_CONTENT)),
                        language = it.getString(it.getColumnIndexOrThrow(COL_CHAT_LANG)) ?: "en",
                        detectedEmotion = it.getString(it.getColumnIndexOrThrow(COL_CHAT_EMOTION)),
                        suggestedAction = it.getString(it.getColumnIndexOrThrow(COL_CHAT_ACTION))
                    )
                )
            }
        }
        return list
    }

    fun clearAllData() {
        val db = writableDatabase
        db.delete(TABLE_MOODS, null, null)
        db.delete(TABLE_CHAT, null, null)
        db.delete(TABLE_JOURNAL, null, null)
        db.delete(TABLE_CIRCLE, null, null)
        seedInitialData(db)
    }
}
