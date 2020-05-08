package com.keepnote.notesDB

class NoteRepository(private val notesDao: NotesDao) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.


    suspend fun insert(note: Notes) {
        notesDao.insertNote(note)
    }

//    fun getallNotes(): List<Notes>? {
//        return notesDao.getAllNotes()
//    }
//
//    fun getNote():Notes {
//        return  notesDao.getNote(1)
//    }
}