package com.keepnote.viewmodel

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.keepnote.notesDB.Notes
import com.keepnote.notesDB.NotesDao
import com.keepnote.utils.Constants
import kotlinx.coroutines.*

class HomeViewmodel(val database: NotesDao, application: Application):AndroidViewModel(application) {

      var allNotes = MutableLiveData<List<Notes>>()
      private var viewModelJob = Job()
    var views:MutableLiveData<View> = MutableLiveData()
    val passedData:MutableLiveData<String> = MutableLiveData()


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)


    fun initview(view:View):MutableLiveData<View>{
        views.value = view
        return views
    }


    
    fun getallNotes() {
        uiScope.launch {
            allNotes.value = getallnote()
        }

    }


    fun updateDeleteById(noteID: Long?, isDeleted:Int){
            uiScope.launch {

                withContext(Dispatchers.Default) {
                    noteID?.let { database.updateDeleteById(it, isDeleted) }
                }
                withContext(Dispatchers.Default) {
                    getallNotes()
                    if (isDeleted == 1)
                        Constants.showToast(
                            "The note has been moved to the trash",
                            getApplication()
                        )
                    else Constants.showToast("Restored", getApplication())

                }

            }
    }

    fun deleteNoteById(noteID: Long?){
        uiScope.launch {

            withContext(Dispatchers.Default) {
                noteID?.let { database.deletenoteById(it) }
            }
            withContext(Dispatchers.Default) {
                getallNotes()
                Constants.showToast("Deleted", getApplication())

            }

        }
    }


    fun updateLockbyId(noteID: Long?,isLocked:Int){
        uiScope.launch {
            withContext(Dispatchers.Default) {
                noteID?.let { database.updateLockById(it, isLocked) }
            }
            withContext(Dispatchers.Default) {
                getallNotes()
                if (isLocked == 0) Constants.showToast("UnLocked", getApplication())
                else Constants.showToast("Locked", getApplication())

            }

        }
    }

    private suspend fun getallnote(): List<Notes>? {
        val sortValue = Constants.getSortOrder(getApplication())
        return withContext(Dispatchers.Main){
            when(sortValue){
                1-> return@withContext database.getAllNotesbyalphabet()

                2-> return@withContext database.getAllNotesbycolor()

                3-> return@withContext  database.getAllNotesbycreatedtime()

                4-> return@withContext  database.getAllNotesbyupdatemills()

                else->return@withContext database.getAllNotes()
            }

        }
    }


}