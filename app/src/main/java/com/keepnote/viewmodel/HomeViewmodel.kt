package com.keepnote.viewmodel

import android.app.Application
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.keepnote.notesDB.Notes
import com.keepnote.notesDB.NotesDao
import com.keepnote.utils.Constants
import kotlinx.coroutines.*

class HomeViewmodel(val database: NotesDao, application: Application):AndroidViewModel(application) {

      var allNotes = MutableLiveData<List<Notes>>()
      var viewModelJob = Job()
    var views:MutableLiveData<View> = MutableLiveData()
    val passedData:MutableLiveData<String> = MutableLiveData()
    val menus = MutableLiveData<MenuItem>()

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)


    init {

    }

    fun initview(view:View):MutableLiveData<View>{
        Log.d("@@@@2","vvvvvvvv")
        views.value = view
        return views
    }

    fun passMenu(menu:MenuItem){
        menus.value = menu
    }

    fun passData(For:String){
        passedData.value = For
    }

    fun insertNote(notes: Notes){
        uiScope.launch{
            withContext(Dispatchers.IO){
               database.insertNote(notes)
            }
        }
    }
    
    fun getallNotes() {
        uiScope.launch {
            allNotes.value = getallnote()
        }

    }


    fun updateDeleteById(noteID: Long?, isDeleted:Int){
            uiScope.launch {

                val deleteRequest =async {
                    noteID?.let { database.updateDeleteById(it,isDeleted) }
                }.await()
                val getAllNotesRequest = async {
                    getallNotes()
                    if (isDeleted==1)
                    Constants.showToast("The note has been moved to the trash",getApplication())
                    else Constants.showToast("Restored",getApplication())

                }.await()

            }
    }

    fun deleteNoteById(noteID: Long?){
        uiScope.launch {

            val deleteRequest =async {
                noteID?.let { database.deletenoteById(it) }
            }.await()
            val getAllNotesRequest = async {
                getallNotes()
                Constants.showToast("Deleted",getApplication())

            }.await()

        }
    }


    fun updateLockbyId(noteID: Long?,isLocked:Int){
        uiScope.launch {
            val updateLockRequest =async {
                noteID?.let { database.updateLockById(it,isLocked) }
            }.await()
            val getAllNotesRequest = async {
                getallNotes()
                if (isLocked==0) Constants.showToast("UnLocked",getApplication())
                else Constants.showToast("Locked",getApplication())

            }.await()

        }
    }

    private suspend fun getallnote(): List<Notes>? {
        return withContext(Dispatchers.IO){
            return@withContext database.getAllNotes()
        }
    }


}