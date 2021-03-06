package com.keepnote.notesDB

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.keepnote.utils.Constants
import kotlinx.coroutines.*

class NoteViewmodel(val database: NotesDao, application: Application):AndroidViewModel(application) {

      private var allNotes = MutableLiveData<List<Notes>>()
      private var viewModelJob = Job()
    var note = MutableLiveData<Notes?>()

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    fun insertNote(notes: Notes){
        uiScope.launch {
            database.insertNote(notes)
            getallNotes()
            Constants.showToast("Note saved",getApplication())
        }
    }
    
    private fun getallNotes() {
        uiScope.launch {
            allNotes.value = getallnote()
        }

    }

    fun getNotebyId(noteID: Long?){
        uiScope.launch {
            note.value = getNote(noteID)
        }
    }


    /* fun deleteNotebyId(noteID: Long?){
        uiScope.launch {
            noteID?.let { database.deletenoteById(it) }
        }
    }*/

    private suspend fun getNote(noteID:Long?): Notes?{
        return withContext(Dispatchers.IO){
            return@withContext noteID?.let { database.getNote(it) }
        }
    }

    fun updateNote(noteId:Long?,title:String?,content:String?,notecolor:Int?,isFavourite:Int){
        uiScope.launch {
            withContext(Dispatchers.IO){
                if (noteId!=null){
                  if (title!=null && content!=null && notecolor!=null && isFavourite!=null){
                      database.updateNoteById(noteId,isFavourite, title, content,notecolor,System.currentTimeMillis())
                  }
                }

            }
        }
    }

    private suspend fun getallnote(): List<Notes>? {
        return withContext(Dispatchers.IO){
            return@withContext database.getAllNotes()
        }
    }


}