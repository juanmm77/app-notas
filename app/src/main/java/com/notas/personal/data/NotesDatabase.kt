package com.notas.personal.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "topics")
data class Topic(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val createdAt: Long,
    val updatedAt: Long,
)

@Entity(
    tableName = "notes",
    foreignKeys = [ForeignKey(entity = Topic::class, parentColumns = ["id"], childColumns = ["topicId"], onDelete = ForeignKey.RESTRICT)],
    indices = [Index(value = ["topicId", "createdAt"])],
)
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val topicId: Long,
    val body: String,
    val createdAt: Long,
    val updatedAt: Long,
)

data class TopicSummary(
    @Embedded val topic: Topic,
    val preview: String?,
    val noteCount: Int,
)

@Dao
interface NotesDao {
    @Query("""
        SELECT topics.*,
        (SELECT body FROM notes WHERE topicId = topics.id ORDER BY createdAt DESC, id DESC LIMIT 1) AS preview,
        (SELECT COUNT(*) FROM notes WHERE topicId = topics.id) AS noteCount
        FROM topics ORDER BY updatedAt DESC, id DESC
    """)
    fun observeTopics(): Flow<List<TopicSummary>>

    @Query("SELECT * FROM notes WHERE topicId = :topicId ORDER BY createdAt ASC, id ASC")
    fun observeNotes(topicId: Long): Flow<List<Note>>

    @Query("SELECT * FROM topics WHERE id = :id")
    suspend fun getTopic(id: Long): Topic?

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getNote(id: Long): Note?

    @Insert suspend fun insertTopic(topic: Topic): Long
    @Insert suspend fun insertNote(note: Note): Long
    @Update suspend fun updateTopic(topic: Topic)
    @Update suspend fun updateNote(note: Note)
    @Query("DELETE FROM notes WHERE id = :id") suspend fun deleteNote(id: Long)
}

@Database(entities = [Topic::class, Note::class], version = 1, exportSchema = true)
abstract class NotesDatabase : RoomDatabase() {
    abstract fun notesDao(): NotesDao

    companion object {
        fun open(context: Context, name: String = "notas.db"): NotesDatabase =
            Room.databaseBuilder(context.applicationContext, NotesDatabase::class.java, name).build()
    }
}
