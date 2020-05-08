package com.raks.roomdatabase

import android.content.Context
import android.os.Environment
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.keepnote.model.preferences.StoreSharedPrefData
import com.keepnote.notesDB.Notes
import com.keepnote.notesDB.NotesDao
import java.io.File

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

        fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            NoteDatabase::class.java,
            "note_db"
        )
            .fallbackToDestructiveMigration()
            .build()

        fun closeDatabase(){
            if (this.instance?.isOpen!!){
                instance?.openHelper?.close()
            }
        }
        fun openDatabase() {
            if(!(this.instance?.isOpen)!!){
                this.instance?.openHelper?.writableDatabase
            }
        }
    }
}