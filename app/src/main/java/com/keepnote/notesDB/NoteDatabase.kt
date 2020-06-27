package com.keepnote.notesDB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Notes::class],
    version = 1,
    exportSchema = false
)
abstract class NoteDatabase : RoomDatabase(){

    abstract fun getNoteDao() : NotesDao

    companion object {

        @Volatile private var instance : NoteDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK){
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            NoteDatabase::class.java,
            "note_db"
        )
            .fallbackToDestructiveMigration()
            .build()


    }
}