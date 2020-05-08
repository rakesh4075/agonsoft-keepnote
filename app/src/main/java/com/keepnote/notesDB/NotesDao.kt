package com.keepnote.notesDB

import androidx.room.*

@Dao
interface NotesDao {

    @Insert
    suspend fun insertNote(note:Notes)

    @Query("SELECT * FROM notes ORDER BY noteId DESC")
    suspend fun getAllNotes():List<Notes>?

    @Query("SELECT * FROM notes WHERE noteId = :key")
    suspend fun getNote(key:Long):Notes?

    @Query("DELETE FROM notes")
    suspend fun clearNotes()

    @Query("DELETE FROM notes WHERE noteId = :noteId")
    suspend fun deletenoteById(noteId: Long)

    @Query("SELECT * FROM notes ORDER BY noteId DESC LIMIT 1")
    suspend fun getLastNote():Notes?


    @Query("UPDATE notes SET title = :title ,content=:content,notecolor =:notecolor,noteUpdateMills=:noteUpdateMills WHERE noteId=:noteId")
    suspend fun updateNoteById(noteId:Long,title:String,content:String,notecolor:Int,noteUpdateMills:Long)


    @Query("UPDATE notes SET isLocked=:isLocked WHERE noteId=:noteId")
    suspend fun updateLockById(noteId:Long,isLocked:Int)

    @Query("UPDATE notes SET isDeleted=:isDeleted WHERE noteId=:noteId")
    suspend fun updateDeleteById(noteId:Long,isDeleted:Int)

}