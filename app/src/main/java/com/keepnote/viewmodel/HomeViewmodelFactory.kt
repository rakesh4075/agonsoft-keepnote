package com.keepnote.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.keepnote.notesDB.NotesDao

class HomeViewmodelFactory(
    private val notesDao: NotesDao,
    private val application: Application):ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewmodel::class.java)){
            return HomeViewmodel(notesDao,application) as T
        }
        throw IllegalArgumentException("unknown Viewmodel class")
    }
}