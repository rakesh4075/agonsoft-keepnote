package com.keepnote.notesDB

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class NoteViewmodelFactory(
    private val notesDao: NotesDao,
    private val application: Application):ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewmodel::class.java)){
            return NoteViewmodel(notesDao,application) as T
        }
        throw IllegalArgumentException("unknown Viewmodel class")
    }
}